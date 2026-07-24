package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PalletRepository extends JpaRepository<Pallet, Long> {
   List<Pallet> findPalletByLinehaulTrip(LinehaulTrip linehaulTrip);

    boolean existsPalletByPalletCode(String code);

    Optional<Pallet> findByPalletCode(String palletCode);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Pallet p LEFT JOIN FETCH p.palletItems pi LEFT JOIN FETCH pi.order WHERE p.palletId = :palletId")
    Optional<Pallet> findByIdWithItemsAndOrders(@org.springframework.data.repository.query.Param("palletId") Long palletId);

    @org.springframework.data.jpa.repository.Query("""
            SELECT DISTINCT p FROM Pallet p
            LEFT JOIN FETCH p.palletItems pi
            LEFT JOIN FETCH pi.order
            WHERE 
                p.status IN :statuses
            ORDER BY p.createdAt DESC
            """)
    List<Pallet> findSystemTasksByStatusIn(@org.springframework.data.repository.query.Param("statuses") List<com.overcode250204.smartlogicticssystem.enums.PalletStatus> statuses);

    @org.springframework.data.jpa.repository.Query("""
            SELECT DISTINCT p FROM Pallet p
            LEFT JOIN FETCH p.palletItems pi
            LEFT JOIN FETCH pi.order
            WHERE p.palletId = :palletId
            """)
    Optional<Pallet> findSystemTaskByIdWithItemsAndOrders(@org.springframework.data.repository.query.Param("palletId") Long palletId);
}
