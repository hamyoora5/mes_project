export function SectionCard({ title, children }) {
  return (
    <section className="section-card">
      <div className="section-card-title">{title}</div>
      {children}
    </section>
  );
}
