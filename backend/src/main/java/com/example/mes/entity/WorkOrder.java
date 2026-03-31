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

/**
 * 작업지시 기본 정보를 보관하는 엔티티입니다.
 *
 * 이 엔티티는 생산 흐름의 출발점입니다. 프론트에서 작업지시를 등록하면 가장 먼저
 * 이 테이블에 저장되고, 이후 생산 시작/중단/재가동/종료 흐름도 모두 이 상태값을
 * 기준으로 동작합니다.
 *
 * 왜 별도 엔티티로 두는가:
 * - 생산 이력과 품질 이력의 기준 축이 됩니다.
 * - 작업지시 자체의 상태와 실행 구간(run history)을 분리할 수 있습니다.
 * - DP 코드처럼 작업 전체 단위에 귀속되는 값을 함께 관리할 수 있습니다.
 */
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

    /**
     * READY 상태의 작업을 시작 상태로 전환하고 DP 코드를 연결합니다.
     *
     * DP 코드를 작업 시작 시점에 생성하는 이유는 이후 생산량 입력, 품질 검사 등록,
     * 바코드 연동이 모두 같은 기준 식별자를 참조할 수 있게 하기 위해서입니다.
     *
     * @param dailyProdCode 시작과 함께 부여할 일일생산코드
     */
    public void start(String dailyProdCode) {
        if (status != WorkOrderStatus.READY) {
            throw new IllegalStateException("작업 시작은 READY 상태에서만 가능합니다.");
        }
        this.dailyProdCode = dailyProdCode;
        status = WorkOrderStatus.IN_PROGRESS;
    }

    /**
     * 진행 중인 작업을 중단 상태로 전환합니다.
     */
    public void stop() {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("작업 중단은 IN_PROGRESS 상태에서만 가능합니다.");
        }
        status = WorkOrderStatus.STOPPED;
    }

    /**
     * 중단된 작업을 다시 진행 상태로 전환합니다.
     */
    public void resume() {
        if (status != WorkOrderStatus.STOPPED) {
            throw new IllegalStateException("작업 재가동은 STOPPED 상태에서만 가능합니다.");
        }
        status = WorkOrderStatus.IN_PROGRESS;
    }

    /**
     * 진행 중인 작업을 완료 상태로 전환합니다.
     *
     * 실제 생산 결과 집계는 서비스 계층에서 수행하고, 이 메서드는 상태 전이 규칙만
     * 담당합니다. 상태 전이 규칙을 엔티티 안에 두면 잘못된 상태 변경을 한 곳에서
     * 막을 수 있습니다.
     */
    public void complete() {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("작업 종료는 IN_PROGRESS 상태에서만 가능합니다.");
        }
        status = WorkOrderStatus.COMPLETED;
    }

    /**
     * 샘플 데이터 적재 시 상태, DP 코드, 생성 시각을 직접 주입합니다.
     *
     * @param status 적용할 상태
     * @param dailyProdCode 적용할 DP 코드
     * @param createdAt 생성 시각
     */
    public void applySeedState(WorkOrderStatus status, String dailyProdCode, LocalDateTime createdAt) {
        this.status = status;
        this.dailyProdCode = dailyProdCode;
        this.createdAt = createdAt;
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
