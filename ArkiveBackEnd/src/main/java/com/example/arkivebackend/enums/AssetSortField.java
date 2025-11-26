package com.example.arkivebackend.enums;

import lombok.Getter;

@Getter
public enum AssetSortField {
    // Định nghĩa các trường được phép.
    // Tên Enum (viết hoa) và giá trị thực tế (tên thuộc tính trong Entity)
    FILENAME("filename"),
    FILE_SIZE("fileSize"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    // Tên thuộc tính trong Entity (ví dụ: "filename")
    private final String fieldName;

    // Constructor
    AssetSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Phương thức 'thông dịch' chuỗi string an toàn từ API.
     * Nó sẽ tìm Enum tương ứng, nếu không tìm thấy, nó sẽ trả về giá trị mặc định.
     */
    public static AssetSortField fromString(String text) {
        if (text != null) {
            // Duyệt qua tất cả các giá trị Enum (FILENAME, FILE_TYPE, ...)
            for (AssetSortField field : AssetSortField.values()) {
                // So sánh không phân biệt hoa thường với 'fieldName' (ví dụ: "filename")
                if (field.fieldName.equalsIgnoreCase(text)) {
                    return field; // Tìm thấy -> trả về
                }
            }
        }

        // --- GIÁ TRỊ MẶC ĐỊNH ---
        // Nếu 'text' là null, rỗng, hoặc không khớp với bất kỳ giá trị nào
        // Luôn trả về giá trị mặc định là CREATED_AT (mới nhất)
        return AssetSortField.CREATED_AT;
    }
}
