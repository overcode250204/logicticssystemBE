package com.overcode250204.smartlogicticssystem.base;

public interface BaseService<D, ID> {
    D create(D dto);
    D update(ID id, D dto);
    D getById(ID id);
    void delete(ID id);
}
