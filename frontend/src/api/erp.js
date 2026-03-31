import { request } from "./client";

/**
 * ERP 결과 조회용 API 모듈입니다.
 *
 * ERP 관련 호출을 별도 파일로 분리한 이유는 도메인 경계를 프론트에서도 유지하기
 * 위해서입니다. 나중에 ERP 저장, 재전송 같은 기능이 추가되면 이 파일에 같이
 * 모으는 방식으로 확장할 수 있습니다.
 */
export const erpApi = {
  /**
   * ERP 전송 대상 생산 결과 목록을 조회합니다.
   *
   * @returns {Promise<Array>} ERP 결과 목록
   */
  getResults: () => request("/erp/results")
};
