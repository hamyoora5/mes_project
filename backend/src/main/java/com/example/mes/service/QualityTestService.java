package com.example.mes.service;

import com.example.mes.common.QualityTestResult;
import com.example.mes.dto.ApplyAiResultRequest;
import com.example.mes.dto.CreateQualityTestRequest;
import com.example.mes.dto.QualityTestResponse;
import com.example.mes.entity.ProductionResultMes;
import com.example.mes.entity.QualityTest;
import com.example.mes.exception.BusinessException;
import com.example.mes.repository.ProductionResultMesRepository;
import com.example.mes.repository.QualityTestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 품질 검사 등록과 AI 판정 결과 반영을 담당하는 서비스입니다.
 */
@Service
@Transactional(readOnly = true)
public class QualityTestService {

    private final QualityTestRepository qualityTestRepository;
    private final ProductionResultMesRepository productionResultMesRepository;

    public QualityTestService(
            QualityTestRepository qualityTestRepository,
            ProductionResultMesRepository productionResultMesRepository
    ) {
        this.qualityTestRepository = qualityTestRepository;
        this.productionResultMesRepository = productionResultMesRepository;
    }

    /**
     * 일일생산코드를 기준으로 품질 검사 대상을 생성합니다.
     *
     * @param request 품질 검사 등록 요청
     * @return 생성된 품질 검사 응답
     */
    @Transactional
    public QualityTestResponse create(CreateQualityTestRequest request) {
        ProductionResultMes productionResult = getProductionResult(request.dailyProdCode());

        String testNo = generateTestNo();
        while (qualityTestRepository.existsByTestNo(testNo)) {
            testNo = generateTestNo();
        }

        QualityTest qualityTest = new QualityTest(testNo, productionResult.getDailyProdCode(), request.imagePath());
        return QualityTestResponse.from(qualityTestRepository.save(qualityTest));
    }

    /**
     * 품질 검사에 AI 판정 결과를 반영하고 생산 결과 상태를 갱신합니다.
     *
     * @param testNo 품질 검사 번호
     * @param request AI 판정 결과 요청
     * @return 결과가 반영된 품질 검사 응답
     */
    @Transactional
    public QualityTestResponse applyAiResult(String testNo, ApplyAiResultRequest request) {
        QualityTest qualityTest = qualityTestRepository.findByTestNo(testNo)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "품질 검사를 찾을 수 없습니다."));

        QualityTestResult result = parseResult(request.result());
        qualityTest.applyAiResult(result);

        ProductionResultMes productionResult = getProductionResult(qualityTest.getDailyProdCode());
        if (result == QualityTestResult.PASS) {
            productionResult.markPass();
        } else {
            productionResult.markDefect();
        }

        return QualityTestResponse.from(qualityTest);
    }

    /**
     * 품질 검사 번호로 검사 결과를 조회합니다.
     *
     * @param testNo 품질 검사 번호
     * @return 품질 검사 응답
     */
    public QualityTestResponse findByTestNo(String testNo) {
        QualityTest qualityTest = qualityTestRepository.findByTestNo(testNo)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "품질 검사를 찾을 수 없습니다."));
        return QualityTestResponse.from(qualityTest);
    }

    /**
     * 일일생산코드로 MES 생산 결과를 조회합니다.
     *
     * @param dailyProdCode 일일생산코드
     * @return 생산 결과 엔티티
     */
    private ProductionResultMes getProductionResult(String dailyProdCode) {
        return productionResultMesRepository.findByDailyProdCode(dailyProdCode)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "생산 종료 결과를 찾을 수 없습니다."));
    }

    /**
     * 문자열 형태의 AI 판정 값을 내부 enum으로 변환합니다.
     *
     * @param rawResult 외부 입력 결과 문자열
     * @return 내부 품질 결과 enum
     */
    private QualityTestResult parseResult(String rawResult) {
        String normalized = rawResult.trim().toUpperCase();
        return switch (normalized) {
            case "PASS" -> QualityTestResult.PASS;
            case "FAIL" -> QualityTestResult.FAIL;
            default -> throw new BusinessException(HttpStatus.BAD_REQUEST, "AI 판정 결과는 PASS 또는 FAIL 이어야 합니다.");
        };
    }

    /**
     * 샘플 QT 번호를 생성합니다.
     *
     * @return 품질 검사 번호
     */
    private String generateTestNo() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "QT" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + suffix;
    }
}
