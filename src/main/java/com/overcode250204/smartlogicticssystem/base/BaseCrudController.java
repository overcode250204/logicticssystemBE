package com.overcode250204.smartlogicticssystem.base;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public abstract class BaseCrudController<D, ID> extends BaseController {
    private final BaseService<D, ID> service;

    protected BaseCrudController(BaseService<D, ID> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<D>> create(@RequestBody D dto) {
        D createdData = service.create(dto);
        return success(createdData, "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<D>> getById(@PathVariable ID id) {
        D data = service.getById(id);
        return success(data, "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<D>> update(@PathVariable ID id, @RequestBody D dto) {
        D updatedData = service.update(id, dto);
        return success(updatedData, "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable ID id) {
        service.delete(id);
        return success(null, "Delete successfully");
    }

}
