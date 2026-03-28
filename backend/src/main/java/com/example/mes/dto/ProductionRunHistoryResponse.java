package com.example.mes.dto;

import com.example.mes.common.WorkOrderStatus;
import com.example.mes.entity.ProductionRunHistory;

import java.time.LocalDateTime;

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
