package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinehaulTripDriverRepository extends JpaRepository<LinehaulTripDriver, Long> {

    /**
     * Các assignment còn hiệu lực của một tài xế ở các chuyến KHÁC.
     * Chuyến linehaul chưa có start/end time lúc phân công (PREPARING), nên
     * "chồng lấn" được xác định bằng: tài xế đang tham gia một chuyến khác
     * ở trạng thái active (PREPARING / CAN_START / EN_ROUTE).
     * excludeTripId cho phép bỏ qua chính chuyến đang cập nhật.
     */
    @Query("""
            SELECT td FROM LinehaulTripDriver td
            WHERE td.driver.driverId = :driverId
            AND td.linehaulTrip.status IN :statuses
            AND (:excludeTripId IS NULL OR td.linehaulTrip.linehaulId <> :excludeTripId)
            """)
    List<LinehaulTripDriver> findActiveAssignmentsForDriver(
            @Param("driverId") Long driverId,
            @Param("statuses") List<LinehaulTripStatus> statuses,
            @Param("excludeTripId") Long excludeTripId);

    /**
     * Danh sách driverId đang được phân công vào một chuyến linehaul active.
     * Dùng để loại các tài xế này khỏi dropdown chọn tài xế — phản ánh đúng cùng
     * điều kiện mà validation tạo/sửa chuyến áp dụng (findActiveAssignmentsForDriver),
     * nhờ đó dropdown và validation không còn lệch nhau.
     */
    @Query("""
            SELECT DISTINCT td.driver.driverId FROM LinehaulTripDriver td
            WHERE td.linehaulTrip.status IN :statuses
            """)
    List<Long> findDriverIdsWithActiveAssignments(
            @Param("statuses") List<LinehaulTripStatus> statuses);
}
