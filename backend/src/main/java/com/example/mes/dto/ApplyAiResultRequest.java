package com.example.mes.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 판정 결과 반영 요청 DTO입니다.
 *
 * @param result 외부 AI가 전달한 결과 문자열
 */
public record ApplyAiResultRequest(
        @NotBlank(message = "AI 판정 결과는 필수입니다.")
        String result
) {
}
