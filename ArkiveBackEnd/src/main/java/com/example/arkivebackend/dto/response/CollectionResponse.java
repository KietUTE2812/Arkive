package com.example.arkivebackend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectionResponse {
    String id;
    String name;
    String description;
    String ownerId;
    Integer assetCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
