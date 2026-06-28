package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.AssignmentStatus;
import com.overcode250204.smartlogicticssystem.enums.DriverRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LinehaulTripDriverResponseDTO {

    private Long id;

    private DriverResponseDTO driver;

    private DriverRole role;

    private AssignmentStatus assignmentStatus;

    private LocalDateTime assignedAt;
}
