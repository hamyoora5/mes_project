import { NavLink } from "react-router-dom";

const items = [
  { to: "/work-orders", label: "작업지시" },
  { to: "/production", label: "생산 진행" },
  { to: "/quality", label: "품질 검사" },
  { to: "/erp", label: "ERP 결과" }
];

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
