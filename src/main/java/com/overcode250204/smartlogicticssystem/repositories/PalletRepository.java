package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PalletRepository extends JpaRepository<Pallet, Long> {
   List<Pallet> findPalletByLinehaulTrip(LinehaulTrip linehaulTrip);
}
