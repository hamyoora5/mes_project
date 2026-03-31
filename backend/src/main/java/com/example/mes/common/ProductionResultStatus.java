package com.example.mes.common;

/**
 * 생산 집계 결과의 최종 상태를 정의하는 enum입니다.
 *
 * <p>왜 작업지시 상태와 분리했는가:
 * 작업지시는 생산 실행 관점의 상태이고, 생산 결과는 품질 판정이 반영된 결과 상태이기
 * 때문입니다. 예를 들어 작업은 이미 `COMPLETED`여도, 생산 결과는 품질 판정 전까지
 * `WAIT_QC`일 수 있습니다.
 *
 * <p>어디에 쓰는가:
 * `production_result_mes.status` 컬럼에 저장되며, 품질 검사 결과 반영 시 갱신됩니다.
 */
public enum ProductionResultStatus {
    WAIT_QC,
    PASS,
    DEFECT
}
