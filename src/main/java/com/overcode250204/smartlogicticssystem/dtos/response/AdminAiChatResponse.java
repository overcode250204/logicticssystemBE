package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAiChatResponse {

    private String answer;

    @Builder.Default
    private String severity = "LOW";

    @Builder.Default
    private List<String> insights = new ArrayList<>();

    @Builder.Default
    private List<String> recommendedActions = new ArrayList<>();

    @Builder.Default
    private List<String> usedData = new ArrayList<>();

    private double confidence;

    private boolean fallback;
}
