package com.example.mes.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateQualityTestRequest(
        @NotBlank(message = "일일생산코드는 필수입니다.")
        String dailyProdCode,
        String imagePath
) {
}
