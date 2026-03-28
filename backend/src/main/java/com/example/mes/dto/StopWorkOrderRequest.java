package com.example.mes.dto;

import jakarta.validation.constraints.NotBlank;

public record StopWorkOrderRequest(
        @NotBlank(message = "중단 사유는 필수입니다.")
        String stopReason
) {
}
