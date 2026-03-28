package com.example.mes.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplyAiResultRequest(
        @NotBlank(message = "AI 판정 결과는 필수입니다.")
        String result
) {
}
