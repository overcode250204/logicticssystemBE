package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.ProductUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {
    List<ProductUnit> findAllByProduct_ProductId(Long productId);
    boolean existsByProduct_ProductIdAndUnit_Id(Long productId, Long unitId);
}
