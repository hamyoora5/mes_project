package com.example.mes.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 품질 검사 등록 요청 DTO입니다.
 *
 * @param dailyProdCode 품질 검사 대상 일일생산코드
 * @param imagePath 검사 이미지 경로
 */
public record CreateQualityTestRequest(
        @NotBlank(message = "일일생산코드는 필수입니다.")
        String dailyProdCode,
        String imagePath
) {
}
