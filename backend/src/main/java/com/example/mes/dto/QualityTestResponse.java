package com.example.mes.dto;

import com.example.mes.common.QualityTestResult;
import com.example.mes.entity.QualityTest;

import java.time.LocalDateTime;

public record QualityTestResponse(
        Long id,
        String testNo,
        String dailyProdCode,
        QualityTestResult result,
        LocalDateTime testedAt,
        String imagePath
) {

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
