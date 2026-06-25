package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    @Query(value = """
        SELECT *
        FROM zones z
        WHERE ST_Covers(
            z.polygon,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
        )
        LIMIT 1
        """,
            nativeQuery = true)
    Optional<Zone> findAreaContainingPoint(
            @Param("lon") double lon,
            @Param("lat") double lat);
}
