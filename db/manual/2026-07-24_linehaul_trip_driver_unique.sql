-- =============================================================================
-- Migration thủ công: UNIQUE(linehaul_id, driver_id) cho linehaul_trip_driver
-- =============================================================================
-- Dự án dùng Hibernate ddl-auto=update (KHÔNG có Flyway/Liquibase). ddl-auto
-- KHÔNG đáng tin cậy khi thêm unique constraint vào bảng đã tồn tại, nên với DB
-- hiện có phải chạy script này THỦ CÔNG. DB tạo mới sẽ được @UniqueConstraint
-- trên entity LinehaulTripDriver tự tạo.
--
-- CHẠY THEO THỨ TỰ. KHÔNG tự động xoá dữ liệu production.
-- =============================================================================

-- Bảng thật: linehaul_trip_driver
-- Cột FK thật: linehaul_id (-> linehaul_trips), driver_id (-> drivers)
-- Constraint đặt tên cố định: uq_ltd_trip_driver
--   (khớp LinehaulTripDriver.UQ_TRIP_DRIVER để error handler map 409 mà không lộ tên)

-- -----------------------------------------------------------------------------
-- BƯỚC 1 — AUDIT: tài xế bị gán trùng trong CÙNG một chuyến (phải rỗng trước khi
--           thêm constraint). Nếu có kết quả -> xử lý ở BƯỚC 3.
-- -----------------------------------------------------------------------------
SELECT linehaul_id, driver_id, COUNT(*) AS cnt
FROM linehaul_trip_driver
GROUP BY linehaul_id, driver_id
HAVING COUNT(*) > 1
ORDER BY linehaul_id, driver_id;

-- -----------------------------------------------------------------------------
-- BƯỚC 2 — AUDIT: chuyến có nhiều hơn một tài xế chính (MAIN).
-- -----------------------------------------------------------------------------
SELECT linehaul_id, COUNT(*) AS main_cnt
FROM linehaul_trip_driver
WHERE role = 'MAIN'
GROUP BY linehaul_id
HAVING COUNT(*) > 1
ORDER BY linehaul_id;

-- -----------------------------------------------------------------------------
-- BƯỚC 3 — CLEANUP CÓ KIỂM SOÁT (chỉ chạy nếu BƯỚC 1 có kết quả).
--   Chiến lược: giữ lại bản ghi cũ nhất (id nhỏ nhất) cho mỗi (linehaul_id,
--   driver_id), xoá các bản trùng còn lại. HÃY REVIEW danh sách trước khi xoá.
--
--   3a. Xem trước những dòng SẼ bị xoá:
--   SELECT t.*
--   FROM linehaul_trip_driver t
--   JOIN (
--       SELECT linehaul_id, driver_id, MIN(id) AS keep_id
--       FROM linehaul_trip_driver
--       GROUP BY linehaul_id, driver_id
--       HAVING COUNT(*) > 1
--   ) d ON t.linehaul_id = d.linehaul_id AND t.driver_id = d.driver_id
--   WHERE t.id <> d.keep_id;
--
--   3b. Sau khi review, thực hiện xoá trong transaction:
--   BEGIN;
--   DELETE FROM linehaul_trip_driver t
--   USING (
--       SELECT linehaul_id, driver_id, MIN(id) AS keep_id
--       FROM linehaul_trip_driver
--       GROUP BY linehaul_id, driver_id
--       HAVING COUNT(*) > 1
--   ) d
--   WHERE t.linehaul_id = d.linehaul_id
--     AND t.driver_id = d.driver_id
--     AND t.id <> d.keep_id;
--   -- kiểm tra lại BƯỚC 1 = rỗng rồi mới COMMIT;
--   COMMIT;
--
--   Với "nhiều MAIN" (BƯỚC 2): quyết định nghiệp vụ ai là MAIN, hạ phần còn lại
--   xuống ASSISTANT (KHÔNG xoá tài xế khỏi chuyến một cách mù quáng):
--   -- UPDATE linehaul_trip_driver SET role = 'ASSISTANT'
--   -- WHERE linehaul_id = :tripId AND id <> :chosenMainRowId AND role = 'MAIN';

-- -----------------------------------------------------------------------------
-- BƯỚC 4 — THÊM UNIQUE CONSTRAINT (chỉ khi BƯỚC 1 đã rỗng). Idempotent.
-- -----------------------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_ltd_trip_driver'
    ) THEN
        ALTER TABLE linehaul_trip_driver
            ADD CONSTRAINT uq_ltd_trip_driver UNIQUE (linehaul_id, driver_id);
    END IF;
END $$;

-- -----------------------------------------------------------------------------
-- BƯỚC 5 — XÁC MINH constraint đã tồn tại.
-- -----------------------------------------------------------------------------
SELECT conname, contype, pg_get_constraintdef(oid) AS def
FROM pg_constraint
WHERE conname = 'uq_ltd_trip_driver';
