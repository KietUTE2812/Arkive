//
// src/lib/thumbnail-generator.ts (hoặc bất cứ đâu bạn đặt file)
//

// --- PHẦN 1: Helper chuyển Canvas sang Blob (tránh lặp code) ---
/**
 * Chuyển đổi an toàn một HTMLCanvasElement sang Blob.
 */
function canvasToBlob(
    canvas: HTMLCanvasElement,
    type = "image/jpeg",
    quality = 0.8
): Promise<Blob> {
    return new Promise((resolve, reject) => {
        canvas.toBlob(
            (blob) => {
                if (blob) {
                    resolve(blob);
                } else {
                    reject(new Error("Không thể tạo Blob từ canvas"));
                }
            },
            type,
            quality
        );
    });
}

// --- PHẦN 2: Hàm xử lý chính đã được sửa lỗi và tối ưu ---
/**
 * Tạo thumbnail từ File (Ảnh, Video, PDF) hoàn toàn ở phía client.
 * Tự động fallback về icon mặc định nếu có lỗi.
 *
 * @param file File object từ <input> hoặc Kéo/thả.
 * @returns Promise<Blob> - Blob của ảnh thumbnail.
 */
export async function generateThumbnail(file: File): Promise<Blob> {
    try {
        const type = file.type;

        // ---- 1. Xử lý Ảnh ----
        if (type.startsWith("image/")) {
            const img = await createImageBitmap(file);
            const canvas = document.createElement("canvas");
            const ctx = canvas.getContext("2d");
            if (!ctx) throw new Error("Không thể lấy 2D context");

            const maxWidth = 300;
            const scale = maxWidth / img.width;
            canvas.width = maxWidth;
            canvas.height = img.height * scale;

            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            return await canvasToBlob(canvas);
        }

        // ---- 2. Xử lý Video (Đã sửa lỗi logic) ----
        if (type.startsWith("video/")) {
            return await new Promise((resolve, reject) => {
                const video = document.createElement("video");
                const videoUrl = URL.createObjectURL(file);
                video.src = videoUrl;

                // Xử lý khi video bị lỗi (ví dụ: file hỏng)
                video.addEventListener("error", (e) => {
                    URL.revokeObjectURL(videoUrl); // Dọn dẹp
                    reject(new Error("Không thể tải video"));
                });

                // Chỉ khi metadata (như kích thước) đã tải xong...
                video.addEventListener("loadedmetadata", () => {
                    video.currentTime = 1; // ...mới tua đến giây thứ 1
                });

                // Chỉ khi tua (seek) thành công...
                video.addEventListener("seeked", () => {
                    const canvas = document.createElement("canvas");
                    const ctx = canvas.getContext("2d");
                    if (!ctx) {
                        URL.revokeObjectURL(videoUrl); // Dọn dẹp
                        return reject(new Error("Không thể lấy 2D context"));
                    }

                    // Tính toán tỷ lệ
                    const maxWidth = 320;
                    const scale = maxWidth / video.videoWidth;
                    canvas.width = maxWidth;
                    canvas.height = video.videoHeight * scale;

                    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
                    URL.revokeObjectURL(videoUrl); // Dọn dẹp rác (RẤT QUAN TRỌNG)

                    // Dùng helper
                    canvasToBlob(canvas).then(resolve).catch(reject);
                });
            });
        }

        // ---- 3. Xử lý PDF (Đã tối ưu và thêm Worker) ----
        if (type === "application/pdf") {
            const pdfjsLib = await import("pdfjs-dist");

            // BẮT BUỘC: Thiết lập worker để PDF.js chạy nền
            // Nếu không, nó sẽ chạy trên main thread và rất chậm/lỗi
            // Bạn cần copy file "pdf.worker.min.js" từ "node_modules/pdfjs-dist/build/"
            // vào thư mục "public/" của Nextjs.
            pdfjsLib.GlobalWorkerOptions.workerSrc = "/pdf.worker.min.js";

            const arrayBuffer = await file.arrayBuffer();
            const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
            const page = await pdf.getPage(1); // Lấy trang đầu tiên

            // Tối ưu: Scale thumbnail về chiều rộng 300px
            const originalViewport = page.getViewport({ scale: 1 });
            const maxWidth = 300;
            const scale = maxWidth / originalViewport.width;
            const viewport = page.getViewport({ scale });

            const canvas = document.createElement("canvas");
            canvas.width = viewport.width;
            canvas.height = viewport.height;
            const ctx = canvas.getContext("2d");
            if (!ctx) throw new Error("Không thể lấy 2D context");

            // Cú pháp render mới và chính xác hơn
            await page.render({ canvasContext: ctx, viewport }).promise;

            // Dùng helper
            return await canvasToBlob(canvas);
        }

        // ---- 4. Loại file khác (Giữ nguyên) ----
        // Nếu không khớp loại nào, ném lỗi để nhảy xuống catch
        throw new Error("Định dạng file không được hỗ trợ");

    } catch (error) {
        // ---- 5. Fallback (Xử lý khi có lỗi) ----
        // Nếu bất kỳ khối try nào ở trên thất bại (file hỏng, lỗi render...)
        // nó sẽ nhảy xuống đây và trả về ảnh mặc định.
        console.error("Lỗi khi tạo thumbnail:", error);
        // Sửa đường dẫn: file trong /public được phục vụ từ root /
        const icon = await fetch("/default-thumbnail.png");
        return await icon.blob();
    }
}