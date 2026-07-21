package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Pallet;
import com.overcode250204.smartlogicticssystem.entities.PalletItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PalletItemRepository extends JpaRepository<PalletItem, Long> {
    List<PalletItem> findAllByPallet(Pallet pallet);

    boolean existsByOrder_OrderId(Long orderId);
}
