package com.example.mes.dto;

import com.example.mes.common.WorkOrderStatus;
import com.example.mes.entity.WorkOrder;

import java.time.LocalDateTime;

/**
 * 작업지시 조회/상태 응답 DTO입니다.
 *
 * @param id 작업지시 ID
 * @param workOrderNo 작업지시 번호
 * @param productCode 제품 코드
 * @param productName 제품명
 * @param plannedQty 계획 수량
 * @param lineNo 생산 라인 번호
 * @param equipmentId 설비 식별자
 * @param workerId 작업자 식별자
 * @param dailyProdCode 일일생산코드
 * @param status 현재 상태
 * @param createdAt 생성 시각
 */
public record WorkOrderResponse(
        Long id,
        String workOrderNo,
        String productCode,
        String productName,
        Integer plannedQty,
        String lineNo,
        String equipmentId,
        String workerId,
        String dailyProdCode,
        WorkOrderStatus status,
        LocalDateTime createdAt
) {

    /**
     * 작업지시 엔티티를 응답 DTO로 변환합니다.
     *
     * @param workOrder 변환 대상 엔티티
     * @return 변환된 응답 DTO
     */
    public static WorkOrderResponse from(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getWorkOrderNo(),
                workOrder.getProductCode(),
                workOrder.getProductName(),
                workOrder.getPlannedQty(),
                workOrder.getLineNo(),
                workOrder.getEquipmentId(),
                workOrder.getWorkerId(),
                workOrder.getDailyProdCode(),
                workOrder.getStatus(),
                workOrder.getCreatedAt()
        );
    }
}
