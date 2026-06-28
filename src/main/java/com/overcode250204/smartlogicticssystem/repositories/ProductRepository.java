package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);

}
