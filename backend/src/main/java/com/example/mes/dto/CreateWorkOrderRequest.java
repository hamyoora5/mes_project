package com.example.mes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 작업지시 등록 요청 DTO입니다.
 *
 * @param workOrderNo 작업지시 번호
 * @param productCode 제품 코드
 * @param productName 제품명
 * @param plannedQty 계획 수량
 * @param lineNo 생산 라인 번호
 * @param equipmentId 설비 또는 작업장 식별자
 * @param workerId 작업자 식별자
 */
public record CreateWorkOrderRequest(
        @NotBlank(message = "작업지시번호는 필수입니다.")
        String workOrderNo,
        @NotBlank(message = "제품코드는 필수입니다.")
        String productCode,
        @NotBlank(message = "제품명은 필수입니다.")
        String productName,
        @NotNull(message = "계획수량은 필수입니다.")
        @Min(value = 1, message = "계획수량은 1 이상이어야 합니다.")
        Integer plannedQty,
        @NotBlank(message = "라인 번호는 필수입니다.")
        String lineNo,
        @NotBlank(message = "설비 ID는 필수입니다.")
        String equipmentId,
        @NotBlank(message = "작업자 ID는 필수입니다.")
        String workerId
) {
}
