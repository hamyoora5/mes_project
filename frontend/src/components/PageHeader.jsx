export function PageHeader({ eyebrow, title, description }) {
  return (
    <header className="page-header">
      <p className="eyebrow">{eyebrow}</p>
      <h2>{title}</h2>
      <p className="page-description">{description}</p>
    </header>
  );
}
