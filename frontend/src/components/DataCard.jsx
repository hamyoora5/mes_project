/**
 * 대시보드 숫자 요약 카드입니다.
 *
 * 화면 상단에서 전체 작업 수, 진행 중 작업 수처럼 핵심 숫자를 빠르게 보여주기 위해
 * 사용합니다. 반복되는 카드 UI를 분리해 두면 시각적 일관성을 유지하기 쉽습니다.
 *
 * @param {{title: string, value: string|number, hint?: string}} props 카드 표시 값
 * @returns {JSX.Element} 요약 카드 UI
 */
export function DataCard({ title, value, hint }) {
  return (
    <section className="data-card">
      <p>{title}</p>
      <strong>{value}</strong>
      {hint ? <span>{hint}</span> : null}
    </section>
  );
}
