package com.example.mes.dto;

import com.example.mes.common.WorkOrderStatus;
import com.example.mes.entity.ProductionRunHistory;

import java.time.LocalDateTime;

/**
 * 생산 실행 이력 응답 DTO입니다.
 *
 * @param id 이력 ID
 * @param runSeq 실행 순번
 * @param startTime 시작 시각
 * @param endTime 종료 시각
 * @param status 상태
 * @param stopReason 중단 사유
 * @param producedQty 생산 수량
 * @param defectQty 불량 수량
 */
public record ProductionRunHistoryResponse(
        Long id,
        Integer runSeq,
        LocalDateTime startTime,
        LocalDateTime endTime,
        WorkOrderStatus status,
        String stopReason,
        Integer producedQty,
        Integer defectQty
) {

    /**
     * 생산 실행 이력 엔티티를 응답 DTO로 변환합니다.
     *
     * @param history 변환 대상 엔티티
     * @return 변환된 응답 DTO
     */
    public static ProductionRunHistoryResponse from(ProductionRunHistory history) {
        return new ProductionRunHistoryResponse(
                history.getId(),
                history.getRunSeq(),
                history.getStartTime(),
                history.getEndTime(),
                history.getStatus(),
                history.getStopReason(),
                history.getProducedQty(),
                history.getDefectQty()
        );
    }
}
