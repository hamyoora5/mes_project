/**
 * 날짜/시간 값을 한국어 로컬 형식 문자열로 변환합니다.
 *
 * API 응답의 ISO 날짜 문자열을 화면에서 읽기 쉬운 형식으로 바꾸기 위해 사용합니다.
 * 데이터가 없는 경우에는 빈 셀 대신 `-`를 보여줘서 누락 값이라는 점을 드러냅니다.
 *
 * @param {string|null|undefined} value 날짜 문자열
 * @returns {string} 화면 표시용 날짜 문자열
 */
export function formatDateTime(value) {
  if (!value) {
    return "-";
  }
  return new Intl.DateTimeFormat("ko-KR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(value));
}
