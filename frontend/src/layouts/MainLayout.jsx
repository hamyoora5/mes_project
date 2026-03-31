import { NavLink } from "react-router-dom";

const items = [
  { to: "/work-orders", label: "작업지시" },
  { to: "/production", label: "생산 진행" },
  { to: "/quality", label: "품질 검사" },
  { to: "/erp", label: "ERP 결과" }
];

/**
 * 앱 전체의 공통 레이아웃입니다.
 *
 * 왼쪽에는 주요 업무 모듈 네비게이션을 두고, 오른쪽에는 현재 라우트의 페이지를
 * 렌더링합니다. 이 레이아웃을 쓰는 이유는 MES 화면 특성상 여러 기능을 오가더라도
 * 탐색 구조가 항상 유지되어야 하기 때문입니다.
 *
 * @param {{children: React.ReactNode}} props 현재 라우트 페이지
 * @returns {JSX.Element} 사이드바와 콘텐츠 영역을 포함한 레이아웃
 */
export function MainLayout({ children }) {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <p className="eyebrow">MES + QMS</p>
          <h1>Factory Flow</h1>
        </div>
        <nav className="nav">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>
      <main className="content">{children}</main>
    </div>
  );
}
