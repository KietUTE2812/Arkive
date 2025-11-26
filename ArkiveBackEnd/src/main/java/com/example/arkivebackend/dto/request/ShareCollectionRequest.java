package com.example.arkivebackend.dto.request;

import lombok.Data;

@Data
public class ShareCollectionRequest {
    String collectionId;
    boolean protectWithPassword;
    String password;
}
