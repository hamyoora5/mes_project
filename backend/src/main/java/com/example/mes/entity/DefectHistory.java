package com.example.mes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "defect_history")
public class DefectHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_history_id", nullable = false)
    private ProductionRunHistory runHistory;

    @Column(name = "defect_type", nullable = false, length = 50)
    private String defectType;

    @Column(name = "defect_qty", nullable = false)
    private Integer defectQty;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    protected DefectHistory() {
    }

    public DefectHistory(
            WorkOrder workOrder,
            ProductionRunHistory runHistory,
            String defectType,
            Integer defectQty
    ) {
        this.workOrder = workOrder;
        this.runHistory = runHistory;
        this.defectType = defectType;
        this.defectQty = defectQty;
    }

    @PrePersist
    void prePersist() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}
