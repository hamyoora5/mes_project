# ERD Summary

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
