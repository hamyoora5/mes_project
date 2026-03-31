package com.example.mes.service;

import com.example.mes.common.WorkOrderStatus;
import com.example.mes.dto.CreateWorkOrderRequest;
import com.example.mes.dto.ErpResultResponse;
import com.example.mes.dto.ProductionRunHistoryResponse;
import com.example.mes.dto.RecordProductionRequest;
import com.example.mes.dto.RegisterDefectRequest;
import com.example.mes.dto.StopWorkOrderRequest;
import com.example.mes.dto.WorkOrderResponse;
import com.example.mes.entity.DefectHistory;
import com.example.mes.entity.ProductionRunHistory;
import com.example.mes.entity.ProductionResultMes;
import com.example.mes.entity.WorkOrder;
import com.example.mes.exception.BusinessException;
import com.example.mes.repository.DefectHistoryRepository;
import com.example.mes.repository.ProductionResultMesRepository;
import com.example.mes.repository.ProductionRunHistoryRepository;
import com.example.mes.repository.WorkOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 작업지시와 생산 실행 흐름의 핵심 비즈니스 로직을 처리합니다.
 *
 * <p>작업 시작, 생산량 입력, 불량 등록, 중단/재가동, 종료,
 * 생산 이력 조회, ERP 조회까지 한 곳에서 관리합니다.
 */
@Service
@Transactional(readOnly = true)
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final DefectHistoryRepository defectHistoryRepository;
    private final ProductionRunHistoryRepository productionRunHistoryRepository;
    private final ProductionResultMesRepository productionResultMesRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            DefectHistoryRepository defectHistoryRepository,
            ProductionRunHistoryRepository productionRunHistoryRepository,
            ProductionResultMesRepository productionResultMesRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.defectHistoryRepository = defectHistoryRepository;
        this.productionRunHistoryRepository = productionRunHistoryRepository;
        this.productionResultMesRepository = productionResultMesRepository;
    }

    /**
     * 작업지시를 신규 등록합니다.
     *
     * @param request 작업지시 등록 요청
     * @return 저장된 작업지시 응답
     */
    @Transactional
    public WorkOrderResponse create(CreateWorkOrderRequest request) {
        if (workOrderRepository.existsByWorkOrderNo(request.workOrderNo())) {
            throw new BusinessException(HttpStatus.CONFLICT, "이미 존재하는 작업지시번호입니다.");
        }

        WorkOrder workOrder = new WorkOrder(
                request.workOrderNo(),
                request.productCode(),
                request.productName(),
                request.plannedQty(),
                request.lineNo(),
                request.equipmentId(),
                request.workerId()
        );

        return WorkOrderResponse.from(workOrderRepository.save(workOrder));
    }

    /**
     * 전체 작업지시를 조회합니다.
     *
     * @return 작업지시 목록
     */
    public List<WorkOrderResponse> findAll() {
        return workOrderRepository.findAll().stream()
                .map(WorkOrderResponse::from)
                .toList();
    }

    /**
     * 특정 작업지시의 생산 실행 이력을 조회합니다.
     *
     * @param id 작업지시 ID
     * @return 실행 이력 목록
     */
    public List<ProductionRunHistoryResponse> getHistory(Long id) {
        WorkOrder workOrder = getWorkOrder(id);
        return productionRunHistoryRepository.findAllByWorkOrderOrderByRunSeqAsc(workOrder).stream()
                .map(ProductionRunHistoryResponse::from)
                .toList();
    }

    /**
     * ERP 조회용 생산 결과 목록을 반환합니다.
     *
     * @return 완료된 생산 결과 목록
     */
    public List<ErpResultResponse> getErpResults() {
        return productionResultMesRepository.findAllByOrderByCompletedAtDesc().stream()
                .map(ErpResultResponse::from)
                .toList();
    }

    /**
     * 작업을 시작하고 첫 생산 이력을 생성합니다.
     *
     * @param id 작업지시 ID
     * @return 시작 상태가 반영된 작업지시
     */
    @Transactional
    public WorkOrderResponse start(Long id) {
        WorkOrder workOrder = getWorkOrder(id);

        try {
            workOrder.start(generateDailyProdCode(workOrder.getId()));
        } catch (IllegalStateException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        int nextRunSeq = (int) productionRunHistoryRepository.countByWorkOrder(workOrder) + 1;
        productionRunHistoryRepository.save(new ProductionRunHistory(workOrder, nextRunSeq));

        return WorkOrderResponse.from(workOrder);
    }

    /**
     * 진행 중인 작업을 중단합니다.
     *
     * @param id 작업지시 ID
     * @param request 중단 사유 요청
     * @return 상태가 반영된 작업지시
     */
    @Transactional
    public WorkOrderResponse stop(Long id, StopWorkOrderRequest request) {
        WorkOrder workOrder = getWorkOrder(id);
        ProductionRunHistory currentRun = getInProgressRun(workOrder);

        try {
            workOrder.stop();
            currentRun.stop(request.stopReason());
        } catch (IllegalStateException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        return WorkOrderResponse.from(workOrder);
    }

    /**
     * 중단된 작업을 재가동하고 새로운 실행 구간을 생성합니다.
     *
     * @param id 작업지시 ID
     * @return 상태가 반영된 작업지시
     */
    @Transactional
    public WorkOrderResponse resume(Long id) {
        WorkOrder workOrder = getWorkOrder(id);

        try {
            workOrder.resume();
        } catch (IllegalStateException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        int nextRunSeq = (int) productionRunHistoryRepository.countByWorkOrder(workOrder) + 1;
        productionRunHistoryRepository.save(new ProductionRunHistory(workOrder, nextRunSeq));

        return WorkOrderResponse.from(workOrder);
    }

    /**
     * 진행 중인 작업을 종료하고 MES 생산 결과를 집계합니다.
     *
     * @param id 작업지시 ID
     * @return 완료 상태가 반영된 작업지시
     */
    @Transactional
    public WorkOrderResponse complete(Long id) {
        WorkOrder workOrder = getWorkOrder(id);
        ProductionRunHistory currentRun = getInProgressRun(workOrder);

        try {
            workOrder.complete();
            currentRun.complete();
        } catch (IllegalStateException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        List<ProductionRunHistory> histories = productionRunHistoryRepository.findAllByWorkOrderOrderByRunSeqAsc(workOrder);
        int totalQty = histories.stream().mapToInt(ProductionRunHistory::getProducedQty).sum();
        int defectQty = histories.stream().mapToInt(ProductionRunHistory::getDefectQty).sum();
        int totalRunTimeMin = histories.stream()
                .filter(history -> history.getEndTime() != null)
                .mapToInt(history -> (int) Duration.between(history.getStartTime(), history.getEndTime()).toMinutes())
                .sum();

        productionResultMesRepository.save(new ProductionResultMes(
                workOrder,
                getDailyProdCode(workOrder),
                totalQty,
                Math.max(totalQty - defectQty, 0),
                defectQty,
                totalRunTimeMin,
                LocalDateTime.now()
        ));

        return WorkOrderResponse.from(workOrder);
    }

    /**
     * 진행 중인 실행 이력에 생산 수량을 누적합니다.
     *
     * @param id 작업지시 ID
     * @param request 생산량 입력 요청
     * @return 상태가 반영된 작업지시
     */
    @Transactional
    public WorkOrderResponse recordProduction(Long id, RecordProductionRequest request) {
        WorkOrder workOrder = getWorkOrder(id);
        ProductionRunHistory currentRun = getInProgressRun(workOrder);

        try {
            currentRun.addProducedQty(request.producedQty());
        } catch (IllegalStateException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        return WorkOrderResponse.from(workOrder);
    }

    /**
     * 진행 중인 실행 이력에 불량 수량을 누적하고 불량 이력을 저장합니다.
     *
     * @param id 작업지시 ID
     * @param request 불량 등록 요청
     * @return 상태가 반영된 작업지시
     */
    @Transactional
    public WorkOrderResponse registerDefect(Long id, RegisterDefectRequest request) {
        WorkOrder workOrder = getWorkOrder(id);
        ProductionRunHistory currentRun = getInProgressRun(workOrder);

        try {
            currentRun.addDefectQty(request.defectQty());
        } catch (IllegalStateException exception) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        defectHistoryRepository.save(new DefectHistory(
                workOrder,
                currentRun,
                request.defectType(),
                request.defectQty()
        ));

        return WorkOrderResponse.from(workOrder);
    }

    /**
     * 작업지시 ID로 엔티티를 조회합니다.
     *
     * @param id 작업지시 ID
     * @return 조회된 작업지시 엔티티
     */
    private WorkOrder getWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "작업지시를 찾을 수 없습니다."));
    }

    /**
     * 가장 최근 실행 이력을 조회합니다.
     *
     * @param workOrder 작업지시 엔티티
     * @return 가장 마지막 실행 이력
     */
    private ProductionRunHistory getCurrentRun(WorkOrder workOrder) {
        return productionRunHistoryRepository.findTopByWorkOrderOrderByRunSeqDesc(workOrder)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "진행 중인 생산 이력이 없습니다."));
    }

    /**
     * 현재 작업이 실제로 진행 중인지 확인한 뒤 실행 이력을 반환합니다.
     *
     * @param workOrder 작업지시 엔티티
     * @return 진행 중인 실행 이력
     */
    private ProductionRunHistory getInProgressRun(WorkOrder workOrder) {
        ProductionRunHistory currentRun = getCurrentRun(workOrder);
        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "작업이 진행 중일 때만 처리할 수 있습니다.");
        }
        return currentRun;
    }

    /**
     * 작업지시 ID를 기반으로 일일생산코드를 생성합니다.
     *
     * @param workOrderId 작업지시 ID
     * @return 생성된 일일생산코드
     */
    private String generateDailyProdCode(Long workOrderId) {
        return "DP" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + String.format("%03d", workOrderId);
    }

    /**
     * 작업지시에 발급된 일일생산코드를 반환합니다.
     *
     * @param workOrder 작업지시 엔티티
     * @return 기존에 발급된 일일생산코드
     */
    private String getDailyProdCode(WorkOrder workOrder) {
        if (workOrder.getDailyProdCode() == null || workOrder.getDailyProdCode().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "일일생산코드가 생성되지 않았습니다.");
        }
        return workOrder.getDailyProdCode();
    }
}
