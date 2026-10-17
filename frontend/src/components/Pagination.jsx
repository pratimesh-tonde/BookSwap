export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null;
  const start = Math.max(0, Math.min(page - 2, totalPages - 5));
  const pages = Array.from({ length: Math.min(5, totalPages) }, (_, i) => start + i);
  return (
    <nav className="pagination card" aria-label="Pagination">
      <span className="muted">Page {page + 1} of {totalPages}</span>
      <div>
        {page > 0 && <button onClick={() => onChange(page - 1)}>Previous</button>}
        {pages.map((p) => (
          <button key={p} className={p === page ? 'active' : ''} onClick={() => onChange(p)} aria-current={p === page ? 'page' : undefined}>
            {p + 1}
          </button>
        ))}
        {page < totalPages - 1 && <button onClick={() => onChange(page + 1)}>Next</button>}
      </div>
    </nav>
  );
}
