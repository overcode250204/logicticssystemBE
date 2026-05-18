package com.overcode250204.smartlogicticssystem.base;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public abstract class BaseCrudController<D, ID> extends BaseController {
    private final BaseService<D, ID> service;

    protected BaseCrudController(BaseService<D, ID> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<D>> create(@Valid @RequestBody D dto, @RequestHeader(name = "X-Role-Id") int roleId, @RequestHeader(name = "X-User-Id") int userId) {
        D createdData = service.create(dto, roleId, userId);
        return success(createdData, "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<D>> getById(@PathVariable ID id, @RequestHeader(name = "X-Role-Id") int roleId, @RequestHeader(name = "X-User-Id") int userId) {
        D data = service.getById(id, roleId, userId);
        return success(data, "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<D>> update(@PathVariable ID id, @Valid @RequestBody D dto , @RequestHeader(name = "X-Role-Id") int roleId, @RequestHeader(name = "X-User-Id") int userId) {
        D updatedData = service.update(id, dto, roleId, userId);
        return success(updatedData, "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable ID id, @RequestHeader(name = "X-Role-Id") int roleId, @RequestHeader(name = "X-User-Id") int userId) {
        service.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }

}
