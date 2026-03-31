# API Spec

이 문서는 현재 프로젝트에서 구현된 API를 기능군 기준으로 빠르게 파악하기 위한
요약 명세입니다.

왜 필요한가:
- README는 설계와 배경 설명까지 함께 담고 있어서 길이가 깁니다.
- 실제로 프론트나 테스트 코드에서 필요한 건 "어떤 엔드포인트가 있는가"인 경우가 많습니다.
- 그래서 구현된 API를 별도로 목록화해 두면 빠르게 참조할 수 있습니다.

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
