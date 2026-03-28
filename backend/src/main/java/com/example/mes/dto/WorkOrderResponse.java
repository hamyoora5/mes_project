package com.example.mes.dto;

import com.example.mes.common.WorkOrderStatus;
import com.example.mes.entity.WorkOrder;

import java.time.LocalDateTime;

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
