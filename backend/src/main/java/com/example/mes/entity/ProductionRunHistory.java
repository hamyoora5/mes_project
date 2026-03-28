package com.example.mes.entity;

import com.example.mes.common.WorkOrderStatus;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "production_run_history")
public class ProductionRunHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(name = "run_seq", nullable = false)
    private Integer runSeq;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderStatus status;

    @Column(name = "stop_reason", length = 255)
    private String stopReason;

    @Column(name = "produced_qty", nullable = false)
    private Integer producedQty;

    @Column(name = "defect_qty", nullable = false)
    private Integer defectQty;

    protected ProductionRunHistory() {
    }

    public ProductionRunHistory(WorkOrder workOrder, Integer runSeq) {
        this.workOrder = workOrder;
        this.runSeq = runSeq;
        this.status = WorkOrderStatus.IN_PROGRESS;
        this.producedQty = 0;
        this.defectQty = 0;
    }

    @PrePersist
    void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (status == null) {
            status = WorkOrderStatus.IN_PROGRESS;
        }
        if (producedQty == null) {
            producedQty = 0;
        }
        if (defectQty == null) {
            defectQty = 0;
        }
    }

    public void stop(String stopReason) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 생산 이력만 중단할 수 있습니다.");
        }
        this.status = WorkOrderStatus.STOPPED;
        this.stopReason = stopReason;
        this.endTime = LocalDateTime.now();
    }

    public void complete() {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 생산 이력만 종료할 수 있습니다.");
        }
        this.status = WorkOrderStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    public void addProducedQty(int quantity) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 생산 이력에만 생산량을 입력할 수 있습니다.");
        }
        this.producedQty += quantity;
    }

    public void addDefectQty(int quantity) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 생산 이력에만 불량을 등록할 수 있습니다.");
        }
        this.defectQty += quantity;
    }

    public Long getId() {
        return id;
    }

    public Integer getRunSeq() {
        return runSeq;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public String getStopReason() {
        return stopReason;
    }

    public Integer getProducedQty() {
        return producedQty;
    }

    public Integer getDefectQty() {
        return defectQty;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
