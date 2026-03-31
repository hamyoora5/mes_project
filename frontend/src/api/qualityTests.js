import { request } from "./client";

/**
 * 품질 검사 관련 API 모듈입니다.
 *
 * 품질 등록, AI 판정 반영, QT 조회를 같은 도메인 파일에 모아 두면 페이지 코드에서
 * "품질 흐름"만 보고 읽을 수 있다는 장점이 있습니다.
 */
export const qualityTestsApi = {
  /**
   * DP 코드를 기준으로 품질 검사 대상을 생성합니다.
   *
   * @param {{dailyProdCode: string, imagePath?: string}} payload 등록 요청 데이터
   * @returns {Promise<Object>} 생성된 품질 검사 정보
   */
  create: (payload) =>
    request("/quality-tests", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  /**
   * QT 번호에 AI 판정 결과를 반영합니다.
   *
   * @param {string} testNo 품질 검사 번호
   * @param {{result: string}} payload PASS/FAIL 결과
   * @returns {Promise<Object>} 갱신된 품질 검사 정보
   */
  applyAiResult: (testNo, payload) =>
    request(`/quality-tests/${testNo}/ai-result`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  /**
   * QT 번호로 품질 검사 결과를 조회합니다.
   *
   * @param {string} testNo 품질 검사 번호
   * @returns {Promise<Object>} 품질 검사 조회 결과
   */
  getByTestNo: (testNo) => request(`/quality-tests/${testNo}`)
};
