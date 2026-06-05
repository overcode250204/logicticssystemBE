package com.overcode250204.smartlogicticssystem.dtos.request.delivery;

import lombok.Data;

@Data
public class IncidentReportDTO {
    private String note;       // Lý do shipper nhập
    private String imageUrl;   // Ảnh minh chứng nếu có
}
