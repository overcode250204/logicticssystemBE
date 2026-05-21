package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
}
