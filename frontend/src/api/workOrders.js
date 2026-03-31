import { request } from "./client";

/**
 * 작업지시 및 생산 실행 흐름 관련 API 모듈입니다.
 *
 * 이 파일은 백엔드의 `/work-orders` 하위 기능을 프론트 관점의 명확한 함수 이름으로
 * 바꿔 주는 역할을 합니다. 페이지에서는 URL보다 "무슨 행동인지"에 집중하면 되도록
 * 설계했습니다.
 */
export const workOrdersApi = {
  /**
   * 전체 작업지시를 조회합니다.
   *
   * @returns {Promise<Array>} 작업지시 목록
   */
  getAll: () => request("/work-orders"),
  /**
   * 작업지시를 등록합니다.
   *
   * @param {Object} payload 등록 요청 데이터
   * @returns {Promise<Object>} 등록된 작업지시
   */
  create: (payload) =>
    request("/work-orders", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  /**
   * 작업을 시작하고 DP 코드를 발급받습니다.
   *
   * @param {number|string} id 작업지시 ID
   * @returns {Promise<Object>} 시작 처리된 작업지시
   */
  start: (id) => request(`/work-orders/${id}/start`, { method: "POST" }),
  /**
   * 생산 수량을 누적합니다.
   *
   * @param {number|string} id 작업지시 ID
   * @param {{producedQty: number}} payload 생산 수량 입력값
   * @returns {Promise<Object>} 갱신된 작업지시
   */
  recordProduction: (id, payload) =>
    request(`/work-orders/${id}/production`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  /**
   * 불량 수량과 유형을 등록합니다.
   *
   * @param {number|string} id 작업지시 ID
   * @param {{defectType: string, defectQty: number}} payload 불량 입력값
   * @returns {Promise<Object>} 갱신된 작업지시
   */
  registerDefect: (id, payload) =>
    request(`/work-orders/${id}/defects`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  /**
   * 작업을 중단합니다.
   *
   * @param {number|string} id 작업지시 ID
   * @param {{stopReason: string}} payload 중단 사유
   * @returns {Promise<Object>} 갱신된 작업지시
   */
  stop: (id, payload) =>
    request(`/work-orders/${id}/stop`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  /**
   * 작업을 재가동합니다.
   *
   * @param {number|string} id 작업지시 ID
   * @returns {Promise<Object>} 갱신된 작업지시
   */
  resume: (id) => request(`/work-orders/${id}/resume`, { method: "POST" }),
  /**
   * 작업을 종료하고 생산 결과를 집계합니다.
   *
   * @param {number|string} id 작업지시 ID
   * @returns {Promise<Object>} 완료된 작업지시
   */
  complete: (id) => request(`/work-orders/${id}/complete`, { method: "POST" }),
  /**
   * 특정 작업지시의 생산 이력을 조회합니다.
   *
   * @param {number|string} id 작업지시 ID
   * @returns {Promise<Array>} 생산 실행 이력 목록
   */
  getHistory: (id) => request(`/work-orders/${id}/history`)
};
