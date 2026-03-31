/**
 * 폼, 표, 상세 정보 같은 콘텐츠를 담는 공통 섹션 카드 컴포넌트입니다.
 *
 * 페이지마다 박스 디자인을 반복하지 않기 위해 분리했습니다. 주로 업무 단위별로
 * "작업지시 등록", "생산 이력", "ERP 전송 결과"처럼 영역을 구분할 때 사용합니다.
 *
 * @param {{title: string, children: React.ReactNode}} props 섹션 제목과 본문
 * @returns {JSX.Element} 카드 형태의 섹션 컨테이너
 */
export function SectionCard({ title, children }) {
  return (
    <section className="section-card">
      <div className="section-card-title">{title}</div>
      {children}
    </section>
  );
}
