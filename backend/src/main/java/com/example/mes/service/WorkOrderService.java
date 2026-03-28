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

    public List<WorkOrderResponse> findAll() {
        return workOrderRepository.findAll().stream()
                .map(WorkOrderResponse::from)
                .toList();
    }

    public List<ProductionRunHistoryResponse> getHistory(Long id) {
        WorkOrder workOrder = getWorkOrder(id);
        return productionRunHistoryRepository.findAllByWorkOrderOrderByRunSeqAsc(workOrder).stream()
                .map(ProductionRunHistoryResponse::from)
                .toList();
    }

    public List<ErpResultResponse> getErpResults() {
        return productionResultMesRepository.findAllByOrderByCompletedAtDesc().stream()
                .map(ErpResultResponse::from)
                .toList();
    }

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

    private WorkOrder getWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "작업지시를 찾을 수 없습니다."));
    }

    private ProductionRunHistory getCurrentRun(WorkOrder workOrder) {
        return productionRunHistoryRepository.findTopByWorkOrderOrderByRunSeqDesc(workOrder)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "진행 중인 생산 이력이 없습니다."));
    }

    private ProductionRunHistory getInProgressRun(WorkOrder workOrder) {
        ProductionRunHistory currentRun = getCurrentRun(workOrder);
        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "작업이 진행 중일 때만 처리할 수 있습니다.");
        }
        return currentRun;
    }

    private String generateDailyProdCode(Long workOrderId) {
        return "DP" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + String.format("%03d", workOrderId);
    }

    private String getDailyProdCode(WorkOrder workOrder) {
        if (workOrder.getDailyProdCode() == null || workOrder.getDailyProdCode().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "일일생산코드가 생성되지 않았습니다.");
        }
        return workOrder.getDailyProdCode();
    }
}
