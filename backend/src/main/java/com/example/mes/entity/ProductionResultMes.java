package com.example.mes.entity;

import com.example.mes.common.ProductionResultStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "production_result_mes")
public class ProductionResultMes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(name = "daily_prod_code", nullable = false, unique = true, length = 50)
    private String dailyProdCode;

    @Column(name = "total_qty", nullable = false)
    private Integer totalQty;

    @Column(name = "good_qty", nullable = false)
    private Integer goodQty;

    @Column(name = "defect_qty", nullable = false)
    private Integer defectQty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductionResultStatus status;

    @Column(name = "total_run_time_min", nullable = false)
    private Integer totalRunTimeMin;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    protected ProductionResultMes() {
    }

    public ProductionResultMes(
            WorkOrder workOrder,
            String dailyProdCode,
            Integer totalQty,
            Integer goodQty,
            Integer defectQty,
            Integer totalRunTimeMin,
            LocalDateTime completedAt
    ) {
        this.workOrder = workOrder;
        this.dailyProdCode = dailyProdCode;
        this.totalQty = totalQty;
        this.goodQty = goodQty;
        this.defectQty = defectQty;
        this.status = ProductionResultStatus.WAIT_QC;
        this.totalRunTimeMin = totalRunTimeMin;
        this.completedAt = completedAt;
    }

    public void markPass() {
        this.status = ProductionResultStatus.PASS;
    }

    public void markDefect() {
        this.status = ProductionResultStatus.DEFECT;
    }

    public String getDailyProdCode() {
        return dailyProdCode;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public Integer getGoodQty() {
        return goodQty;
    }

    public Integer getDefectQty() {
        return defectQty;
    }

    public ProductionResultStatus getStatus() {
        return status;
    }

    public Integer getTotalRunTimeMin() {
        return totalRunTimeMin;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
