package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.RouteProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteProvinceRepository extends JpaRepository<RouteProvince, Long> {
    boolean existsByProvinceName(String provinceName);
    RouteProvince findByProvinceName(String provinceName);
}
