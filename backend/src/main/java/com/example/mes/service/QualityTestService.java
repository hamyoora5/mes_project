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

    public QualityTestResponse findByTestNo(String testNo) {
        QualityTest qualityTest = qualityTestRepository.findByTestNo(testNo)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "품질 검사를 찾을 수 없습니다."));
        return QualityTestResponse.from(qualityTest);
    }

    private ProductionResultMes getProductionResult(String dailyProdCode) {
        return productionResultMesRepository.findByDailyProdCode(dailyProdCode)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "생산 종료 결과를 찾을 수 없습니다."));
    }

    private QualityTestResult parseResult(String rawResult) {
        String normalized = rawResult.trim().toUpperCase();
        return switch (normalized) {
            case "PASS" -> QualityTestResult.PASS;
            case "FAIL" -> QualityTestResult.FAIL;
            default -> throw new BusinessException(HttpStatus.BAD_REQUEST, "AI 판정 결과는 PASS 또는 FAIL 이어야 합니다.");
        };
    }

    private String generateTestNo() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "QT" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + suffix;
    }
}
