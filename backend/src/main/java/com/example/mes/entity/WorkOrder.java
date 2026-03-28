package com.example.mes.entity;

import com.example.mes.common.WorkOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_order")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_order_no", nullable = false, unique = true, length = 50)
    private String workOrderNo;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "planned_qty", nullable = false)
    private Integer plannedQty;

    @Column(name = "line_no", nullable = false, length = 50)
    private String lineNo;

    @Column(name = "equipment_id", nullable = false, length = 50)
    private String equipmentId;

    @Column(name = "worker_id", nullable = false, length = 50)
    private String workerId;

    @Column(name = "daily_prod_code", unique = true, length = 50)
    private String dailyProdCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected WorkOrder() {
    }

    public WorkOrder(
            String workOrderNo,
            String productCode,
            String productName,
            Integer plannedQty,
            String lineNo,
            String equipmentId,
            String workerId
    ) {
        this.workOrderNo = workOrderNo;
        this.productCode = productCode;
        this.productName = productName;
        this.plannedQty = plannedQty;
        this.lineNo = lineNo;
        this.equipmentId = equipmentId;
        this.workerId = workerId;
        this.status = WorkOrderStatus.READY;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = WorkOrderStatus.READY;
        }
    }

    public void start(String dailyProdCode) {
        if (status != WorkOrderStatus.READY) {
            throw new IllegalStateException("작업 시작은 READY 상태에서만 가능합니다.");
        }
        this.dailyProdCode = dailyProdCode;
        status = WorkOrderStatus.IN_PROGRESS;
    }

    public void stop() {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("작업 중단은 IN_PROGRESS 상태에서만 가능합니다.");
        }
        status = WorkOrderStatus.STOPPED;
    }

    public void resume() {
        if (status != WorkOrderStatus.STOPPED) {
            throw new IllegalStateException("작업 재가동은 STOPPED 상태에서만 가능합니다.");
        }
        status = WorkOrderStatus.IN_PROGRESS;
    }

    public void complete() {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("작업 종료는 IN_PROGRESS 상태에서만 가능합니다.");
        }
        status = WorkOrderStatus.COMPLETED;
    }

    public Long getId() {
        return id;
    }

    public String getWorkOrderNo() {
        return workOrderNo;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getPlannedQty() {
        return plannedQty;
    }

    public String getLineNo() {
        return lineNo;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getDailyProdCode() {
        return dailyProdCode;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
