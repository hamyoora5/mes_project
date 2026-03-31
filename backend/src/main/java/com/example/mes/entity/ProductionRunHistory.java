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

/**
 * 작업지시 아래에서 실제로 생산이 돌았던 개별 구간을 저장하는 엔티티입니다.
 *
 * <p>왜 필요한가:
 * 작업지시 하나가 항상 한 번에 끝나지는 않기 때문입니다. 생산 도중 중단과 재가동이
 * 일어나면 "언제부터 언제까지 한 구간이었는지"를 별도로 보관해야 실제 가동 시간,
 * 중단 사유, 구간별 생산/불량 이력을 추적할 수 있습니다.
 *
 * <p>어떻게 쓰는가:
 * <ul>
 *   <li>작업 시작 시 run sequence 1로 생성됩니다.</li>
 *   <li>중단되면 현재 구간이 STOPPED로 닫힙니다.</li>
 *   <li>재가동되면 다음 run sequence 구간이 새로 생성됩니다.</li>
 *   <li>종료되면 마지막 구간이 COMPLETED로 닫힙니다.</li>
 * </ul>
 *
 * <p>어디에 쓰는가:
 * 생산 이력 조회 화면, 총 가동 시간 집계, 불량 이력 연결 기준으로 사용됩니다.
 */
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

    /**
     * 현재 진행 중인 생산 구간을 중단 상태로 종료합니다.
     *
     * @param stopReason 중단 사유
     */
    public void stop(String stopReason) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 생산 이력만 중단할 수 있습니다.");
        }
        this.status = WorkOrderStatus.STOPPED;
        this.stopReason = stopReason;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 현재 진행 중인 생산 구간을 완료 상태로 종료합니다.
     */
    public void complete() {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 생산 이력만 종료할 수 있습니다.");
        }
        this.status = WorkOrderStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 생산 수량을 현재 구간에 누적합니다.
     *
     * @param quantity 추가할 생산 수량
     */
    public void addProducedQty(int quantity) {
        if (status != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 생산 이력에만 생산량을 입력할 수 있습니다.");
        }
        this.producedQty += quantity;
    }

    /**
     * 불량 수량을 현재 구간에 누적합니다.
     *
     * @param quantity 추가할 불량 수량
     */
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

    /**
     * 샘플 데이터 적재용으로 이력 상태를 직접 설정합니다.
     *
     * @param startTime 시작 시각
     * @param endTime 종료 시각
     * @param status 실행 상태
     * @param stopReason 중단 사유
     * @param producedQty 생산 수량
     * @param defectQty 불량 수량
     */
    public void applySeedState(
            LocalDateTime startTime,
            LocalDateTime endTime,
            WorkOrderStatus status,
            String stopReason,
            int producedQty,
            int defectQty
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.stopReason = stopReason;
        this.producedQty = producedQty;
        this.defectQty = defectQty;
    }
}
