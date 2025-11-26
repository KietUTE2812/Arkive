package com.example.arkivebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectionRequest {
    @NotBlank(message = "COLLECTION_NAME_REQUIRED")
    @Size(max = 100, message = "COLLECTION_NAME_TOO_LONG")
    String name;

    @Size(max = 1000, message = "DESCRIPTION_TOO_LONG")
    String description;
}
