package com.example.mes.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 작업 중단 요청 DTO입니다.
 *
 * @param stopReason 중단 사유
 */
public record StopWorkOrderRequest(
        @NotBlank(message = "중단 사유는 필수입니다.")
        String stopReason
) {
}
