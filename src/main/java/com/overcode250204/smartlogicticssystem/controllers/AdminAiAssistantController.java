package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.AdminAiChatRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;
import com.overcode250204.smartlogicticssystem.services.IAdminAiAssistantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ai-assistant")
@RequiredArgsConstructor
public class AdminAiAssistantController extends BaseController {

    private final IAdminAiAssistantService adminAiAssistantService;

    @PostMapping("/chat")
    public ResponseEntity<BaseResponse<AdminAiChatResponse>> chat(
            @Valid @RequestBody AdminAiChatRequest request) {
        return success(
                adminAiAssistantService.chat(request),
                "Admin AI assistant response retrieved successfully"
        );
    }
}
