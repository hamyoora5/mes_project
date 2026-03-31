/**
 * 페이지 상단 제목과 설명을 렌더링하는 공통 헤더 컴포넌트입니다.
 *
 * 페이지마다 제목 구조가 달라지지 않도록 공통화했고, 현재 사용자가 어떤 모듈을
 * 보고 있는지 빠르게 이해할 수 있도록 eyebrow/title/description 조합을 씁니다.
 *
 * @param {{eyebrow: string, title: string, description: string}} props 헤더 내용
 * @returns {JSX.Element} 페이지 헤더
 */
export function PageHeader({ eyebrow, title, description }) {
  return (
    <header className="page-header">
      <p className="eyebrow">{eyebrow}</p>
      <h2>{title}</h2>
      <p className="page-description">{description}</p>
    </header>
  );
}
