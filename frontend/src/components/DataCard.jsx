export function DataCard({ title, value, hint }) {
  return (
    <section className="data-card">
      <p>{title}</p>
      <strong>{value}</strong>
      {hint ? <span>{hint}</span> : null}
    </section>
  );
}
