package com.overcode250204.smartlogicticssystem.dtos.response.shift;


import lombok.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftResponseDTO {
    private Long id;

    private String workStatus ;
}
