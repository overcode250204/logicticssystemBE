package com.overcode250204.smartlogicticssystem.base;

public interface BaseService<D, ID> {
    D create(D dto, int roleId, int userId);
    D update(ID id, D dto, int roleId, int userId);
    D getById(ID id, int roleId, int userId);
    void delete(ID id, int roleId, int userId);
}
