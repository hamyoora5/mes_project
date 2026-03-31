package com.example.mes.dto;

import com.example.mes.common.ProductionResultStatus;
import com.example.mes.entity.ProductionResultMes;

import java.time.LocalDateTime;

/**
 * ERP 조회용 생산 결과 응답 DTO입니다.
 *
 * @param workOrderNo 작업지시 번호
 * @param productCode 제품 코드
 * @param dailyProdCode 일일생산코드
 * @param totalQty 총 생산 수량
 * @param goodQty 양품 수량
 * @param defectQty 불량 수량
 * @param totalRunTimeMin 총 가동 시간(분)
 * @param status 생산 결과 상태
 * @param completedAt 완료 시각
 */
public record ErpResultResponse(
        String workOrderNo,
        String productCode,
        String dailyProdCode,
        Integer totalQty,
        Integer goodQty,
        Integer defectQty,
        Integer totalRunTimeMin,
        ProductionResultStatus status,
        LocalDateTime completedAt
) {

    /**
     * MES 생산 결과 엔티티를 ERP 응답 DTO로 변환합니다.
     *
     * @param result 변환 대상 엔티티
     * @return 변환된 ERP 응답 DTO
     */
    public static ErpResultResponse from(ProductionResultMes result) {
        return new ErpResultResponse(
                result.getWorkOrder().getWorkOrderNo(),
                result.getWorkOrder().getProductCode(),
                result.getDailyProdCode(),
                result.getTotalQty(),
                result.getGoodQty(),
                result.getDefectQty(),
                result.getTotalRunTimeMin(),
                result.getStatus(),
                result.getCompletedAt()
        );
    }
}
