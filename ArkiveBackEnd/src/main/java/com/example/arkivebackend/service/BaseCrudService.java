package com.example.arkivebackend.service;

/**
 * Interface chung cho các nghiệp vụ CRUD cơ bản.
 *
 * @param <ID>     Kiểu dữ liệu của ID (String, Long...)
 * @param <ReqDTO> DTO dùng cho request (Create/Update)
 * @param <ResDTO> DTO dùng cho response (Get)
 */
public interface BaseCrudService <ID, ReqDTO, ResDTO> {
    ResDTO create(ReqDTO request);
    ResDTO getById(ID id);
    ResDTO update(ID id, ReqDTO request);
    void delete(ID id);
}
