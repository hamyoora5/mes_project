package com.example.mes.common;

/**
 * 작업지시와 생산 실행 이력에서 공통으로 사용하는 상태 enum입니다.
 *
 * <p>이 enum은 "생산이 지금 어떤 단계에 있는가"를 나타냅니다.
 * 작업 시작 전에는 `READY`, 생산 중에는 `IN_PROGRESS`, 일시 중단이면 `STOPPED`,
 * 모든 생산 활동이 끝나면 `COMPLETED`를 사용합니다.
 *
 * <p>어디에 쓰는가:
 * `work_order.status`, `production_run_history.status`에 공통으로 사용됩니다.
 */
public enum WorkOrderStatus {
    READY,
    IN_PROGRESS,
    STOPPED,
    COMPLETED
}
