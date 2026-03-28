# API Spec

## Work Orders

- `POST /work-orders`
- `GET /work-orders`
- `POST /work-orders/{id}/start`
- `POST /work-orders/{id}/production`
- `POST /work-orders/{id}/defects`
- `POST /work-orders/{id}/stop`
- `POST /work-orders/{id}/resume`
- `POST /work-orders/{id}/complete`
- `GET /work-orders/{id}/history`

## Quality Tests

- `POST /quality-tests`
- `POST /quality-tests/{testNo}/ai-result`
- `GET /quality-tests/{testNo}`

## ERP

- `GET /erp/results`
