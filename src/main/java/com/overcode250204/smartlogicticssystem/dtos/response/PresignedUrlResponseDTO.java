package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponseDTO {
    private String url;
    private Long expiresInSeconds;
}
