package com.example.mes.dto;

import com.example.mes.common.QualityTestResult;
import com.example.mes.entity.QualityTest;

import java.time.LocalDateTime;

/**
 * 품질 검사 응답 DTO입니다.
 *
 * @param id 품질 검사 ID
 * @param testNo 품질 검사 번호
 * @param dailyProdCode 일일생산코드
 * @param result 품질 결과
 * @param testedAt 검사 시각
 * @param imagePath 검사 이미지 경로
 */
public record QualityTestResponse(
        Long id,
        String testNo,
        String dailyProdCode,
        QualityTestResult result,
        LocalDateTime testedAt,
        String imagePath
) {

    /**
     * 품질 검사 엔티티를 응답 DTO로 변환합니다.
     *
     * @param qualityTest 변환 대상 엔티티
     * @return 변환된 응답 DTO
     */
    public static QualityTestResponse from(QualityTest qualityTest) {
        return new QualityTestResponse(
                qualityTest.getId(),
                qualityTest.getTestNo(),
                qualityTest.getDailyProdCode(),
                qualityTest.getResult(),
                qualityTest.getTestedAt(),
                qualityTest.getImagePath()
        );
    }
}
