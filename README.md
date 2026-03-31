# MES + QMS AI 품질 자동 판정 설계

작업지시부터 생산실적, 불량, 중단/재가동, ERP 연동, AI 기반 미생물 자동 판정(판정 결과 가정)까지 포함한 MES 통합 설계 문서입니다.

---

## 프로젝트 한눈에 보기

| 항목 | 내용 |
| --- | --- |
| 주제 | ERP 작업지시 기반 생산 실행 + AI 품질 판정 연계 |
| 목적 | 생산, 품질, 실적, 추적성(Traceability) 통합 관리 |
| 핵심 상태 | `READY`, `IN_PROGRESS`, `STOPPED`, `COMPLETED`, `WAIT_QC`, `PASS`, `DEFECT` |
| 주요 연동 | MES ↔ ERP, 가상 품질 판정 데이터, PostgreSQL, Redis(선택) |
| 핵심 가치 | 생산 현장 실행과 품질 판정을 하나의 흐름으로 자동화 |

## 통합 개요

| 영역 | 설명 |
| --- | --- |
| 생산 관리 | ERP 작업지시를 받아 생산 시작, 진행, 중단, 재가동, 종료까지 관리 |
| 품질 관리 | 불량 등록, 품질 검사, AI 기반 자동 판정 수행 |
| 실적 처리 | 생산 종료 후 실적 집계 및 ERP 전달 |
| 상태 자동화 | AI 품질 결과에 따라 생산 상태 자동 변경 |
| 추적성 | 작업지시, 생산 이력, 불량, 품질 검사 결과를 연결해 추적 가능 |

## 전체 흐름

`ERP 작업지시 생성 → MES 작업지시 수신 → 작업 시작 + 일일생산코드(DP) 생성 → 생산 진행 → 불량 발생 / 작업 중단 / 재가동 → 작업 종료 → 생산실적 집계 → 실험번호(QT) 생성 및 바코드 라벨 부착 → 실험물 관리 → 가상 AI 판정 → 적합 결과 도출 → quality_test.result 반영 → production.status 반영 → ERP 저장`

## 시스템 구성

| 구분 | 포트 | 설명 | URL / 실행 |
| --- | --- | --- | --- |
| Backend (Spring Boot) | `8002` | MES API 서버 | `http://localhost:8002` |
| Frontend (React) | `3001` | MES 웹 UI | `http://localhost:3001` |
| Database (PostgreSQL) | `25432` | MES 전용 DB | Docker `mes_db` |
| Redis | `6380` | 캐시 / 실시간 처리 | Docker `mes_redis` |
| AI Server | 별도 | 미생물 이미지 분석 및 품질 판정 | 외부/내부 추론 서버 |

## 기술 스택

| 구분 | 기술 | 사용 목적 |
| --- | --- | --- |
| Backend | Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Validation | MES API, 상태 전이, 생산/품질 로직 처리 |
| Frontend | React 18, React Router, Vite | 현장 MES 화면, 작업 시작/생산/품질 입력 UI |
| Database | PostgreSQL 15 | 작업지시, 생산 이력, 품질 검사, ERP 조회 데이터 저장 |
| Cache / Realtime 준비 | Redis 7 | 향후 캐시, 실시간 처리, 이벤트 확장 대비 |
| Infra | Docker Compose | 로컬 DB/Redis 실행 환경 구성 |
| Build / Run | Gradle Wrapper, npm | 백엔드/프론트 실행 및 빌드 |

## 모듈 설명

### 백엔드 모듈

| 모듈 | 역할 | 왜 필요한가 |
| --- | --- | --- |
| `config` | CORS, 샘플 데이터 초기화, 환경 설정 | 프론트 연동과 로컬 실행을 안정적으로 맞추기 위해 필요 |
| `common` | 공통 응답, 상태 enum | 여러 계층에서 같은 규칙과 응답 구조를 공유하기 위해 필요 |
| `controller` | HTTP 요청 수신 진입점 | 프론트/외부 시스템 요청을 도메인 로직으로 연결 |
| `dto` | 요청/응답 데이터 구조 | 엔티티와 외부 요청 형식을 분리해 API를 안정적으로 유지 |
| `entity` | DB 테이블 매핑 | 작업지시, 생산 이력, 품질 검사 같은 핵심 도메인 데이터 저장 |
| `repository` | DB 접근 처리 | 엔티티 조회/저장을 역할별로 분리 |
| `service` | 핵심 비즈니스 로직 | 상태 전이, 생산 집계, AI 결과 반영 같은 실제 처리 로직 담당 |
| `exception` | 예외와 오류 응답 처리 | 비즈니스 오류를 일관된 API 응답으로 전달 |

### 프론트엔드 모듈

| 모듈 | 역할 | 어디에 쓰는가 |
| --- | --- | --- |
| `api` | 백엔드 호출 함수 | 작업지시/생산/품질/ERP API 호출 |
| `components` | 공통 UI 조각 | 카드, 헤더, 섹션 박스 재사용 |
| `pages` | 화면 단위 페이지 | 작업지시, 생산 진행, 품질 검사, ERP 결과 화면 |
| `layouts` | 공통 레이아웃 | 좌측 네비게이션 + 본문 구조 유지 |
| `routes` | 라우팅 설정 | 페이지 이동 경로 관리 |
| `hooks` | 공통 비동기 로딩 로직 | 조회 화면의 loading/error/data 처리 |
| `utils` | 공통 유틸 함수 | 날짜 포맷 같은 반복 로직 처리 |
| `styles` | 전역 스타일 | 전체 MES UI 톤과 레이아웃 정의 |

## 데이터가 들어오는 위치

현재 구현은 `MES 화면 또는 HTTP 요청으로 데이터가 들어온다`는 가정 위에서 동작합니다.

즉 지금 데이터 입력 지점은 다음과 같습니다.

| 입력 주체 | 실제 입력 위치 | 설명 |
| --- | --- | --- |
| 현장 작업자 | 프론트 `작업지시`, `생산 진행` 화면 | 작업 등록, 작업 시작, 생산량, 불량, 중단/재가동/종료 입력 |
| 품질 담당 / 테스트 흐름 | 프론트 `품질 검사` 화면 | 품질 검사 생성, AI 결과 반영 |
| 외부 시스템/도구 | 백엔드 REST API | 같은 동작을 API 직접 호출로도 수행 가능 |

현재는 아래 연동은 `가정`만 있고 별도 실연동 어댑터는 아직 없습니다.

- ERP가 자동으로 작업지시를 push 하는 수신 인터페이스
- 설비/PLC/센서가 자동으로 생산 데이터를 보내는 인터페이스
- AI 서버가 webhook/event로 결과를 자동 전달하는 인터페이스

즉, 지금 구현 범위는 `데이터가 MES에 들어온 이후부터 처리하는 내부 흐름`입니다.

## 현재 구현 범위

### 구현 완료

- 작업지시 등록 / 조회
- 작업 시작 시 `daily_prod_code` 생성
- 생산량 입력 / 불량 등록
- 작업 중단 / 재가동 / 종료
- 생산 이력 저장 및 조회
- 생산 종료 후 `production_result_mes` 집계
- 품질 검사 등록
- AI 결과 반영 및 생산 결과 상태 자동 변경
- ERP 조회용 결과 목록 제공
- 프론트 화면에서 주요 흐름 직접 조작 가능

### 아직 미구현

- ERP 실연동 수신 / 송신
- PLC, 설비, 센서 자동 수집
- 실제 AI 서버 연동
- 인증 / 권한 관리
- 운영용 마이그레이션 체계(Flyway/Liquibase 등)

## 테스트 방법

지금 프로젝트는 아래 순서로 테스트할 수 있습니다.

1. `docker compose up -d`
2. `backend`에서 `gradlew.bat bootRun`
3. `frontend`에서 `npm install`
4. `frontend`에서 `npm run dev`
5. 브라우저에서 `http://localhost:3001` 접속
6. `작업지시 등록 -> 작업 시작 -> 생산량 입력 -> 불량 등록 -> 작업 종료 -> 품질 등록 -> AI 반영 -> ERP 결과 조회` 순서로 확인

## Docker 설정

### `mes_db`

| 항목 | 값 |
| --- | --- |
| image | `postgres:15` |
| container_name | `mes_db` |
| POSTGRES_USER | `mes_user` |
| POSTGRES_PASSWORD | `mes_pass` |
| POSTGRES_DB | `mes_db` |
| port | `25432:5432` |
| volume | `./postgres-data:/var/lib/postgresql/data` |

### `mes_redis`

| 항목 | 값 |
| --- | --- |
| image | `redis:7` |
| container_name | `mes_redis` |
| port | `6380:6379` |

## Spring Boot 설정

```yaml
server:
  port: 8002

spring:
  datasource:
    url: jdbc:postgresql://localhost:25432/mes_db
    username: mes_user
    password: mes_pass
  jpa:
    ddl-auto: update
    show-sql: true
```

## 기능 구성

| 기능 영역 | 주요 내용 |
| --- | --- |
| 작업지시 관리 | 작업지시 등록, 제품/설비/작업자 관리, 상태 관리 |
| 생산 관리 | 작업 시작/종료, 생산량 입력, 작업시간 계산 |
| 중단/재가동 관리 | 중단 사유 입력, 중단/재가동 시간 기록, 생산 이력 구간 저장 |
| 불량 관리 | 불량 수량 입력, 유형 관리, 발생 시각 기록, 불량률 계산 |
| 품질 검사 관리 | 품질 샘플 등록, 검사 결과 저장, 검사 시각 관리 |
| AI 품질 판정 연계 | 가상 판정 데이터 수신, `PASS` / `FAIL` 결과 반영 |
| 상태 자동 반영 | AI 결과에 따라 품질 결과 및 생산 상태 자동 업데이트 |
| ERP 연동 | 생산 종료 후 실적 집계, ERP 테이블 저장, 데이터 흐름 처리 |
| 모니터링 | 생산 상태, 설비 상태, 생산량, 불량률, 품질 결과, 중단 이력 조회 |

## 데이터 흐름 구조

| 단계 | 처리 내용 |
| --- | --- |
| 작업지시 수신 | ERP 작업지시를 MES가 수신 |
| 생산 실행 | 작업 시작 시 `daily_prod_code` 생성 후 생산량, 불량, 중단 이력 관리 |
| 생산 종료 | 생산 완료 후 생산 실적 집계 및 품질 검사 대상 확정 |
| 품질 검사 | 샘플 채취 후 품질 검사 대상 생성 |
| AI 판정 결과 수신 | 가상 데이터 기반 품질 판정 결과 수신 |
| 품질 반영 | `quality_test.result` 업데이트 |
| 생산 반영 | `production_result.status` 자동 업데이트 |
| 최종 집계 | MES 결과와 ERP 전달용 데이터를 저장 |

## 상태 전이 로직

### 작업지시 상태

| 이벤트 | 변경 상태 |
| --- | --- |
| 작업 시작 | `READY → IN_PROGRESS` |
| 작업 중단 | `IN_PROGRESS → STOPPED` |
| 작업 재가동 | `STOPPED → IN_PROGRESS` |
| 작업 종료 | `IN_PROGRESS → COMPLETED` |

### 품질 / 생산 상태

| AI 판정 결과 | 품질 데이터 반영 | 생산 데이터 반영 |
| --- | --- | --- |
| `PASS` | `quality_test.result = PASS` | `production_result.status = PASS` |
| `FAIL` | `quality_test.result = FAIL` | `production_result.status = DEFECT` |

대기 상태: 품질 판정 전 생산 결과는 `WAIT_QC` 상태로 관리합니다.

## 핵심 처리 로직

| 순서 | 처리 내용 |
| --- | --- |
| 1 | ERP 작업지시 등록 |
| 2 | 작업 시작 시 생산 이력 생성 및 `daily_prod_code` 발급 |
| 3 | 생산량 / 불량 / 중단 이력 실시간 기록 |
| 4 | 작업 종료 후 생산 실적 집계 및 `production_result_mes` 저장 |
| 5 | `daily_prod_code` 기준 품질 검사 데이터 생성 |
| 6 | AI 결과 수신 |
| 7 | `test_no` 기준 품질 데이터 조회 및 결과 업데이트 |
| 8 | `daily_prod_code` 기준 생산 결과 조회 |
| 9 | 품질 결과에 따라 생산 상태 변경 |
| 10 | MES/ERP 반영용 데이터 저장 |

## 화면 구성

| 화면 | 표시 / 기능 항목 |
| --- | --- |
| 작업지시 등록 | 작업지시번호, 제품코드, 제품명, 계획수량, 설비, 작업자 |
| 생산 진행 | 작업지시번호, 상태, 생산량, 불량수량, 진행률, `작업 시작`, `생산 입력`, `불량 등록`, `작업 중단`, `재가동`, `작업 종료` |
| 불량 등록 | 불량유형, 불량수량, 발생시간, 비고 |
| 품질 검사 화면 | 품질번호, 일일생산코드, 바코드 연계 정보, 검사결과, 검사시각, AI 판정 결과 확인 |
| 생산 이력 조회 | 시작시간, 종료시간, 상태, 생산수량, 중단 사유 |
| ERP 전송 결과 | 총 생산량, 양품수량, 불량수량, 작업시간 |

## DB 설계

### `work_order`

| 컬럼 | 타입 / 제약 |
| --- | --- |
| `id` | `SERIAL PRIMARY KEY` |
| `work_order_no` | `VARCHAR(50) UNIQUE` |
| `product_code` | `VARCHAR(50)` |
| `product_name` | `VARCHAR(100)` |
| `planned_qty` | `INT` |
| `line_no` | `VARCHAR(50)` |
| `equipment_id` | `VARCHAR(50)` |
| `worker_id` | `VARCHAR(50)` |
| `daily_prod_code` | `VARCHAR(50) UNIQUE` |
| `status` | `VARCHAR(20)` |
| `created_at` | `TIMESTAMP` |

### `production_run_history`

| 컬럼 | 타입 / 제약 |
| --- | --- |
| `id` | `SERIAL PRIMARY KEY` |
| `work_order_id` | `INT` |
| `run_seq` | `INT` |
| `start_time` | `TIMESTAMP` |
| `end_time` | `TIMESTAMP` |
| `status` | `VARCHAR(20)` |
| `stop_reason` | `VARCHAR(255)` |
| `produced_qty` | `INT` |
| `defect_qty` | `INT` |

### `defect_history`

| 컬럼 | 타입 / 제약 |
| --- | --- |
| `id` | `SERIAL PRIMARY KEY` |
| `work_order_id` | `INT` |
| `run_history_id` | `INT` |
| `defect_type` | `VARCHAR(50)` |
| `defect_qty` | `INT` |
| `occurred_at` | `TIMESTAMP` |

### `production_result_mes`

| 컬럼 | 타입 / 제약 |
| --- | --- |
| `id` | `SERIAL PRIMARY KEY` |
| `work_order_id` | `INT` |
| `daily_prod_code` | `VARCHAR(50)` |
| `total_qty` | `INT` |
| `good_qty` | `INT` |
| `defect_qty` | `INT` |
| `status` | `VARCHAR(20)` |
| `total_run_time_min` | `INT` |
| `completed_at` | `TIMESTAMP` |

### `quality_test`

| 컬럼 | 타입 / 제약 |
| --- | --- |
| `id` | `SERIAL PRIMARY KEY` |
| `test_no` | `VARCHAR(50) UNIQUE` |
| `daily_prod_code` | `VARCHAR(50)` |
| `result` | `VARCHAR(20)` |
| `tested_at` | `TIMESTAMP` |
| `image_path` | `VARCHAR(255)` |

예시: `QT20260328001` / `daily_prod_code: DP20260328001` / `result: PASS`

### `production_result_erp`

| 컬럼 | 타입 / 제약 |
| --- | --- |
| `id` | `SERIAL PRIMARY KEY` |
| `work_order_no` | `VARCHAR(50)` |
| `product_code` | `VARCHAR(50)` |
| `total_qty` | `INT` |
| `good_qty` | `INT` |
| `defect_qty` | `INT` |
| `total_run_time_min` | `INT` |

## API 설계

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `POST` | `/work-orders` | 작업지시 등록 |
| `GET` | `/work-orders` | 작업지시 목록 조회 |
| `POST` | `/work-orders/{id}/start` | 작업 시작 |
| `POST` | `/work-orders/{id}/production` | 생산량 입력 |
| `POST` | `/work-orders/{id}/defects` | 불량 등록 |
| `POST` | `/work-orders/{id}/stop` | 작업 중단 |
| `POST` | `/work-orders/{id}/resume` | 작업 재가동 |
| `POST` | `/work-orders/{id}/complete` | 작업 종료 |
| `GET` | `/work-orders/{id}/history` | 생산 이력 조회 |
| `POST` | `/quality-tests` | 품질 검사 등록 |
| `POST` | `/quality-tests/{testNo}/ai-result` | AI 판정 결과 반영 |
| `GET` | `/quality-tests/{testNo}` | 품질 검사 결과 조회 |
| `GET` | `/erp/results` | ERP 전송 결과 조회 |

## 현재 구현 순서

| 순서 | 구현 범위 | 상태 |
| --- | --- | --- |
| 1 | 작업지시 등록 / 조회 | 완료 |
| 2 | 작업 시작 시 `daily_prod_code` 생성 + 생산 이력 생성 | 완료 |
| 3 | 생산량 입력 / 불량 등록 | 완료 |
| 4 | 작업 중단 / 재가동 / 종료 | 완료 |
| 5 | 생산 종료 시 `production_result_mes` 집계 | 완료 |
| 6 | 품질 검사 등록 / AI 결과 반영 | 완료 |
| 7 | 생산 이력 조회 API | 완료 |
| 8 | ERP 결과 조회 API | 완료 |
| 9 | 프론트엔드 화면 및 인프라 구성 | 완료 |

## 서비스 로직 구조

`Controller → Service → Repository → DB`

생산 흐름:
`작업지시 조회 → 상태 변경 → 생산 이력 저장 → 실적 집계`

AI 품질 흐름:
`품질 데이터 조회 → AI 결과 반영 → 생산 데이터 조회 → 상태 변경 → 저장 처리`

## AI 연동 구조

| 단계 | 설명 |
| --- | --- |
| 바코드 연계 | 일일생산ID와 실험번호ID가 연결된 바코드 라벨이 실험물에 부착된다고 가정 |
| 가상 판정 시나리오 | 해당 실험물이 `CNN`, `ResNet`, `YOLO` 등으로 적합성 판정을 받는다고 가정 |
| 가상 판정 데이터 수신 | 가상 데이터가 품질 판정 결과를 전달 |
| 결과 형식 | `PASS` / `FAIL` 형태의 품질 결과 수신 |
| MES API 호출 | 수신된 품질 결과를 MES에 반영 |
| 상태 자동화 | 품질 결과에 따라 생산 상태를 자동 변경 |

구현 범위 안내: 본 프로젝트는 위 가정 시나리오를 기반으로, AI 모델 학습이나 추론 서버 구현 자체보다는 판정된 가상 데이터를 받아 MES 품질/생산 데이터에 반영하는 부분을 중심으로 설계합니다.

## 시스템 연동 구조

| 시스템 | 역할 |
| --- | --- |
| ERP | 작업지시 생성, 최종 실적 수신 |
| MES 서버 | 생산, 품질, 상태, 이력 관리 |
| AI 서버 | 이미지 분석 및 판정 결과 생성 |
| DB | 생산 / 불량 / 품질 / ERP 전송 데이터 저장 |

## 프로젝트 구조

```text
mes-project
├─ backend                         # Spring Boot 기반 MES 서버
│  ├─ build.gradle                # 백엔드 의존성 및 빌드 설정
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com.example.mes    # 백엔드 메인 패키지
│     │  │     ├─ config          # DB, CORS, JPA 등 환경 설정
│     │  │     ├─ common          # 공통 응답, 상수, 유틸
│     │  │     ├─ controller      # API 요청 진입점
│     │  │     ├─ dto             # 요청/응답 데이터 객체
│     │  │     ├─ entity          # DB 테이블 매핑 엔티티
│     │  │     ├─ repository      # DB 접근 처리
│     │  │     ├─ service         # 핵심 비즈니스 로직
│     │  │     ├─ exception       # 예외 처리
│     │  │     └─ ai              # AI 판정 결과 연동 영역
│     │  │        ├─ dto          # AI 연동용 데이터 객체
│     │  │        └─ service      # AI 결과 반영 서비스
│     │  └─ resources
│     │     ├─ application.yml    # 실행 환경 설정
│     │     ├─ db
│     │     │  └─ migration       # 테이블 생성/변경 스크립트
│     │     └─ static             # 정적 리소스
│     └─ test
│        └─ java                  # 테스트 코드
├─ frontend                        # React 기반 사용자 화면
│  ├─ package.json                # 프론트 의존성 및 스크립트
│  └─ src
│     ├─ api                      # 백엔드 호출 함수
│     ├─ components               # 재사용 UI 컴포넌트
│     ├─ pages                    # 화면 단위 페이지
│     ├─ layouts                  # 공통 레이아웃
│     ├─ routes                   # 라우팅 설정
│     ├─ hooks                    # 커스텀 훅
│     ├─ types                    # 타입 정의
│     ├─ utils                    # 공통 함수
│     ├─ styles                   # 스타일 파일
│     ├─ App.jsx                  # 앱 중심 컴포넌트
│     └─ main.jsx                 # React 시작 파일
├─ docs                            # API/ERD 등 설계 문서
│  ├─ api-spec.md                 # API 명세
│  └─ erd.md                      # DB 구조 문서
├─ infra                           # 실행 환경 관련 파일
│  ├─ docker                      # Docker 설정 파일
│  └─ sql
│     └─ init                     # 초기 SQL 파일
├─ .env                            # 환경변수 파일
└─ docker-compose.yml              # DB, Redis 컨테이너 실행 설정
```

## 실행

```bash
docker compose up -d

cd backend
gradlew.bat bootRun

cd ../frontend
npm install
npm run dev
```

## 주요 특징

| 구분 | 설명 |
| --- | --- |
| 자동화 | 생산 흐름 관리와 품질 판정 자동 수행 |
| 데이터 연동 | 생산 ↔ 품질 ↔ AI ↔ ERP 데이터 연결 |
| 실시간 처리 | AI 결과 즉시 반영, 상태 자동 업데이트 |
| Traceability | 작업지시, 생산코드, 품질번호 기준 이력 추적 가능 |
| 확장성 | ERP, IoT, 센서, 바코드 연계 가능 |

## 바코드 연동 (확장)

`일일생산ID(DP) + 실험번호ID(QT) 연동 바코드 라벨 부착 → 현장 스캔 → 품질 데이터 조회 → 생산 데이터 자동 연결`

## 포인트

| 포인트 | 설명 |
| --- | --- |
| 생산 흐름 구현 | 단순 CRUD가 아닌 실제 생산 프로세스 중심 설계 |
| AI 기반 품질 자동 판정 | 미생물 실험 이미지를 기반으로 자동 판정 수행 |
| MES 연동 자동화 | 품질 결과를 생산 데이터에 자동 반영 |
| ERP vs MES 역할 분리 | ERP는 계획/집계, MES는 실행/기록 담당 |
| Traceability 확보 | 작업지시, 생산코드, 품질결과 기반 추적 가능 |
| 스마트팩토리 구현 | AI + MES + ERP 통합 구조 설계 |

## 한줄 정리

AI가 품질을 판정한 후 MES가 생산과 품질 상태를 자동으로 연결해 관리하는 스마트팩토리형 통합 구조입니다.
