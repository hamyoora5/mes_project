package com.example.mes.dto;

import com.example.mes.common.ProductionResultStatus;
import com.example.mes.entity.ProductionResultMes;

import java.time.LocalDateTime;

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
