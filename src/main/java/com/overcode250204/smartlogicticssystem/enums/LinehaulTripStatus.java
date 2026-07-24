package com.overcode250204.smartlogicticssystem.enums;

import java.util.List;

public enum LinehaulTripStatus {
    PREPARING,CAN_START ,EN_ROUTE, ARRIVED, CANCELLED;

    /**
     * Các trạng thái coi là "đang hoạt động" của một chuyến linehaul: tài xế đang
     * gắn với chuyến ở các trạng thái này thì KHÔNG thể phân công sang chuyến khác.
     * Là nguồn sự thật chung cho: (1) validation khi tạo/sửa/xuất bến chuyến và
     * (2) bộ lọc danh sách tài xế khả dụng cho dropdown — để hai nơi không lệch nhau.
     */
    public static final List<LinehaulTripStatus> ACTIVE_STATUSES =
            List.of(PREPARING, CAN_START, EN_ROUTE);
}
