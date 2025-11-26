package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.CollectionRequest;
import com.example.arkivebackend.dto.response.CollectionResponse;

import java.util.List;

public interface CollectionService extends BaseCrudService <String, CollectionRequest, CollectionResponse> {
    List<CollectionResponse> findAllByOwerId();
}
