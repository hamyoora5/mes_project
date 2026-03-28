package com.example.mes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDefectRequest(
        @NotBlank(message = "불량 유형은 필수입니다.")
        String defectType,
        @NotNull(message = "불량 수량은 필수입니다.")
        @Min(value = 1, message = "불량 수량은 1 이상이어야 합니다.")
        Integer defectQty
) {
}
