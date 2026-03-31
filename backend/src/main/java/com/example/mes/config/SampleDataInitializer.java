package com.example.mes.config;

import com.example.mes.common.ProductionResultStatus;
import com.example.mes.common.QualityTestResult;
import com.example.mes.common.WorkOrderStatus;
import com.example.mes.entity.ProductionResultMes;
import com.example.mes.entity.ProductionRunHistory;
import com.example.mes.entity.QualityTest;
import com.example.mes.entity.WorkOrder;
import com.example.mes.repository.ProductionResultMesRepository;
import com.example.mes.repository.ProductionRunHistoryRepository;
import com.example.mes.repository.QualityTestRepository;
import com.example.mes.repository.WorkOrderRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 애플리케이션 시작 시 샘플 데이터를 적재하는 초기화 컴포넌트입니다.
 *
 * <p>사용자가 제공한 생산 실적 예시를 현재 스키마에 맞게 변환해
 * 작업지시, 생산 이력, MES 결과, 품질 검사 데이터를 함께 생성합니다.
 * 이미 작업지시 데이터가 존재하면 중복 적재를 방지하기 위해 실행을 건너뜁니다.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class SampleDataInitializer implements ApplicationRunner {

    private final WorkOrderRepository workOrderRepository;
    private final ProductionRunHistoryRepository productionRunHistoryRepository;
    private final ProductionResultMesRepository productionResultMesRepository;
    private final QualityTestRepository qualityTestRepository;

    public SampleDataInitializer(
            WorkOrderRepository workOrderRepository,
            ProductionRunHistoryRepository productionRunHistoryRepository,
            ProductionResultMesRepository productionResultMesRepository,
            QualityTestRepository qualityTestRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.productionRunHistoryRepository = productionRunHistoryRepository;
        this.productionResultMesRepository = productionResultMesRepository;
        this.qualityTestRepository = qualityTestRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (workOrderRepository.count() > 0) {
            return;
        }

        seedCompletedOrders();
    }

    /**
     * 문서 및 화면 검증용 완료 생산 데이터를 일괄 생성합니다.
     */
    private void seedCompletedOrders() {
        List<SampleRow> rows = List.of(
                new SampleRow("OR2504481", "형상파우치", "HSP1", "SSBK00001", "글림_썬크림 SPF50", 90000, 90000, 10267, 367, 224, "DP20251224001", "QT20251224001", LocalDateTime.of(2025, 12, 24, 12, 22), LocalDateTime.of(2025, 12, 26, 10, 13), ProductionResultStatus.PASS, QualityTestResult.PASS),
                new SampleRow("OR2504489", "형상파우치", "HSP1", "SORB00002", "오로라뷰티 래디언스 크림", 110000, 110000, 12994, 625, 186, "DP20251226002", "QT20251226002", LocalDateTime.of(2025, 12, 26, 10, 33), LocalDateTime.of(2025, 12, 29, 12, 4), ProductionResultStatus.DEFECT, QualityTestResult.FAIL),
                new SampleRow("OR2504497", "형상파우치", "HSP1", "SOBL00001", "온빛레이_썬샤인 SPF50", 100000, 100000, 11669, 500, 325, "DP20251229003", "QT20251229003", LocalDateTime.of(2025, 12, 29, 12, 24), LocalDateTime.of(2025, 12, 30, 14, 9), ProductionResultStatus.PASS, QualityTestResult.PASS),
                new SampleRow("OR2504513", "형상파우치", "HSP1", "SACH00010", "온더 바디로션", 100000, 100000, 10474, 1754, 636, "DP20251230004", "QT20251230004", LocalDateTime.of(2025, 12, 30, 14, 29), LocalDateTime.of(2026, 1, 5, 18, 19), ProductionResultStatus.PASS, QualityTestResult.PASS),
                new SampleRow("OR2504553", "형상파우치", "HSP1", "SNPR00005", "그린티 토너", 70000, 70000, 9057, 286, 68, "DP20260120005", "QT20260120005", LocalDateTime.of(2026, 1, 20, 11, 0), LocalDateTime.of(2026, 1, 20, 16, 54), ProductionResultStatus.DEFECT, QualityTestResult.FAIL),
                new SampleRow("OR2504561", "형상파우치", "HSP1", "SSLC00001", "리페어 헤어팩", 70000, 70000, 9563, 1228, 339, "DP20260105006", "QT20260105006", LocalDateTime.of(2026, 1, 5, 18, 39), LocalDateTime.of(2026, 1, 8, 8, 46), ProductionResultStatus.PASS, QualityTestResult.PASS)
        );

        for (SampleRow row : rows) {
            WorkOrder workOrder = new WorkOrder(
                    row.workOrderNo(),
                    row.productCode(),
                    row.productName(),
                    row.plannedQty(),
                    row.lineNo(),
                    row.workCenter(),
                    "WORKER-01"
            );
            workOrder.applySeedState(WorkOrderStatus.COMPLETED, row.dailyProdCode(), row.startTime());
            workOrderRepository.save(workOrder);

            ProductionRunHistory history = new ProductionRunHistory(workOrder, 1);
            history.applySeedState(
                    row.startTime(),
                    row.endTime(),
                    WorkOrderStatus.COMPLETED,
                    row.stopMinutes() > 0 ? "샘플 데이터 기준 비가동 시간 포함" : null,
                    row.producedQty(),
                    row.defectQty()
            );
            productionRunHistoryRepository.save(history);

            ProductionResultMes result = new ProductionResultMes(
                    workOrder,
                    row.dailyProdCode(),
                    row.producedQty(),
                    Math.max(row.producedQty() - row.defectQty(), 0),
                    row.defectQty(),
                    row.runMinutes(),
                    row.endTime()
            );
            result.applySeedStatus(row.resultStatus());
            productionResultMesRepository.save(result);

            QualityTest qualityTest = new QualityTest(row.testNo(), row.dailyProdCode(), "/samples/" + row.productCode() + ".jpg");
            qualityTest.applySeedResult(row.testResult(), row.endTime().plusHours(2));
            qualityTestRepository.save(qualityTest);
        }
    }

    /**
     * 외부 생산 실적 데이터를 현재 프로젝트 스키마에 맞게 정리한 내부 표현입니다.
     *
     * @param workOrderNo 생산지시번호
     * @param workCenter 작업장
     * @param lineNo 생산라인
     * @param productCode 제품코드
     * @param productName 제품명
     * @param plannedQty 지시수량
     * @param producedQty 생산수량
     * @param defectQty 불량수량
     * @param runMinutes 가동시간(분)
     * @param stopMinutes 부동시간(분)
     * @param dailyProdCode 샘플 일일생산코드
     * @param testNo 샘플 품질검사번호
     * @param startTime 작업 시작 시각
     * @param endTime 작업 종료 시각
     * @param resultStatus MES 생산 결과 상태
     * @param testResult 품질 판정 결과
     */
    private record SampleRow(
            String workOrderNo,
            String workCenter,
            String lineNo,
            String productCode,
            String productName,
            int plannedQty,
            int producedQty,
            int defectQty,
            int runMinutes,
            int stopMinutes,
            String dailyProdCode,
            String testNo,
            LocalDateTime startTime,
            LocalDateTime endTime,
            ProductionResultStatus resultStatus,
            QualityTestResult testResult
    ) {
    }
}
