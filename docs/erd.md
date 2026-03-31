# ERD Summary

이 문서는 현재 구현된 엔티티 관계를 짧게 확인하기 위한 요약 ERD 메모입니다.

상세 ERD 툴 산출물이 아니라 텍스트 요약으로 유지하는 이유는 다음과 같습니다.
- 코드와 문서를 같이 수정할 때 빠르게 동기화하기 쉽습니다.
- 현재 단계에서는 관계 수가 많지 않아 텍스트만으로도 충분히 파악 가능합니다.
- 포트폴리오/설계 설명 시 핵심 관계를 빠르게 전달할 수 있습니다.

## Tables

- `work_order`
- `production_run_history`
- `defect_history`
- `production_result_mes`
- `quality_test`

## Key Relations

- `production_run_history.work_order_id -> work_order.id`
- `defect_history.work_order_id -> work_order.id`
- `defect_history.run_history_id -> production_run_history.id`
- `production_result_mes.work_order_id -> work_order.id`
- `quality_test.daily_prod_code -> production_result_mes.daily_prod_code`
