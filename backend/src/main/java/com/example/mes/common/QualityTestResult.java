package com.example.mes.common;

/**
 * 품질 검사 자체의 결과 상태를 정의하는 enum입니다.
 *
 * <p>MES 생산 결과 상태와 비슷해 보이지만 의미가 다릅니다.
 * 이 enum은 `quality_test` 레코드의 직접 결과를 나타내고,
 * `ProductionResultStatus`는 그 결과가 생산 집계 결과에 반영된 상태를 나타냅니다.
 *
 * <p>어디에 쓰는가:
 * 품질 검사 생성 시 기본값 `WAIT`, AI 판정 반영 후 `PASS` 또는 `FAIL`로 저장됩니다.
 */
public enum QualityTestResult {
    WAIT,
    PASS,
    FAIL
}
