# Hướng dẫn cho GitHub Copilot - Dự án Arkive-Asset Manager

## Giới thiệu dự án

Dự án này là **Arkive-Asset Manager**, một hệ thống quản lý tài sản sử dụng Java Spring Boot. Mục tiêu là xây dựng một API RESTful mạnh mẽ và dễ bảo trì.

---

### LUÔN PHẢN HỒI BẰNG TIẾNG VIỆT KHI TRẢ LỜI CÁC CÂU HỎI LIÊN QUAN ĐẾN DỰ ÁN NÀY.

## Công nghệ chính (Tech Stack)

Hãy ưu tiên và tuân thủ các công nghệ và thư viện sau:

* **Ngôn ngữ:** Java 17+
* **Framework:** Spring Boot 3+
* **Truy cập dữ liệu:** Spring Data JPA
* **Cơ sở dữ liệu:** PostgreSQL
* **Build Tool:** Maven  - tuân theo `pom.xml`
* **Testing:** JUnit 5, Mockito, AssertJ
* **Logging:** SLF4J (với Logback)

---

## Nguyên tắc Code & Thiết kế API

### Java & Spring Boot

1.  **Dependency Injection:** Luôn sử dụng **constructor injection** để tiêm (inject) các bean. Không sử dụng `@Autowired` trên các trường (field injection).
2.  **DTOs (Data Transfer Objects):**
    * Luôn sử dụng DTOs cho tất cả các request (đầu vào) và response (đầu ra) của API.
    * **KHÔNG** bao giờ lộ JPA Entities (`@Entity`) trực tiếp qua API.
    * Sử dụng các DTO riêng biệt cho việc tạo (ví dụ: `AssetCreationDTO`) và cập nhật (ví dụ: `AssetUpdateDTO`).
3.  **Service Layer:** Toàn bộ logic nghiệp vụ (business logic) phải nằm trong các lớp `@Service`. Controllers chỉ nên làm nhiệm vụ điều phối request và response.
4.  **Immutability:** Ưu tiên sử dụng các đối tượng bất biến (immutable) cho DTOs (ví dụ: sử dụng Java `record` nếu có thể).
5.  **Exception Handling:** Sử dụng `@RestControllerAdvice` và `@ExceptionHandler` để xử lý ngoại lệ một cách tập trung và trả về các response lỗi (error response) nhất quán.
6.  **Optional:** Sử dụng `java.util.Optional` cho các phương thức repository (ví dụ: `findById`) và trong service layer để xử lý các trường hợp không tìm thấy (not-found).
7.  **Logging:** Sử dụng SLF4J để ghi log. Ghi log các thông tin quan trọng, đặc biệt là lỗi và các quyết định nghiệp vụ.

### Thiết kế RESTful API

1.  **Endpoints:**
    * Sử dụng **danh từ (số nhiều)** cho tên tài nguyên (ví dụ: `/api/v1/assets`, `/api/v1/users`).
    * Sử dụng URL phân cấp cho các mối quan hệ (ví dụ: `/api/v1/users/{userId}/assets`).
2.  **Phương thức HTTP:**
    * `GET`: Lấy tài nguyên (idempotent).
    * `POST`: Tạo tài nguyên mới (không idempotent).
    * `PUT`: Cập nhật/Thay thế toàn bộ tài nguyên (idempotent).
* `PATCH`: Cập nhật một phần tài nguyên (không nhất thiết idempotent).
    * `DELETE`: Xóa tài nguyên (idempotent).
3.  **Mã trạng thái (Status Codes):**
    * `200 OK`: Thành công cho `GET`, `PUT`, `PATCH`.
    * `201 Created`: Thành công cho `POST` (nên trả về header `Location`).
    * `204 No Content`: Thành công cho `DELETE` hoặc `PUT`/`PATCH` không trả về nội dung.
    * `400 Bad Request`: Lỗi từ phía client (ví dụ: validation thất bại).
    * `401 Unauthorized`: Chưa xác thực.
    * `403 Forbidden`: Đã xác thực nhưng không có quyền.
    * `404 Not Found`: Không tìm thấy tài nguyên.
    * `500 Internal Server Error`: Lỗi từ phía máy chủ.
4.  **Versioning:** Sử dụng tiền tố phiên bản trong URL (ví dụ: `/api/v1/`).
5.  **Payloads:**
    * Sử dụng **JSON** cho tất cả request và response body.
    * Tuân thủ quy tắc đặt tên **camelCase** cho các trường JSON.
6.  **Validation:** Sử dụng `jakarta.validation` (ví dụ: `@Valid`, `@NotNull`, `@Size`) trên DTOs của request và xử lý `MethodArgumentNotValidException` (trong global exception handler) để trả về lỗi 400.

---

## Cấu trúc dự án (Project Structure)

Vui lòng tuân theo cấu trúc gói (package) tiêu chuẩn của Spring Boot:
com.arkive.assetmanager
├── controller/   # Chứa các lớp RestController (API endpoints)
├── service/      # Chứa logic nghiệp vụ (Business logic)
├── repository/   # Chứa các interface Spring Data JPA
├── model/        # Chứa các lớp Entity (@Entity) của JPA
├── dto/          # Chứa Data Transfer Objects (cho Request/Response)
├── mapper/       # (Tùy chọn) Chứa mappers (ví dụ: MapStruct) để chuyển đổi Entity <-> DTO
├── exception/    # Chứa các lớp ngoại lệ tùy chỉnh và global exception handler (@RestControllerAdvice)
├── config/       # Chứa các lớp cấu hình Spring (@Configuration)
└── AssetManagerApplication.java  # Lớp Spring Boot chính

## Tài liệu tham khảo

* **Spring Boot:** [https://docs.spring.io/spring-boot/docs/current/reference/html/](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* **Spring Data JPA:** [https://docs.spring.io/spring-data/jpa/docs/current/reference/html/](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
* **PostgreSQL:** [https://www.postgresql.org/docs/](https://www.postgresql.org/docs/)