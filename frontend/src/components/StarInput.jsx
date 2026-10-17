import { Star } from 'lucide-react';

export default function StarInput({ value, onChange }) {
  return (
    <div className="stars" role="radiogroup" aria-label="Rating">
      {[1, 2, 3, 4, 5].map((n) => (
        <button
          type="button"
          key={n}
          role="radio"
          aria-checked={value === n}
          aria-label={`${n} star${n > 1 ? 's' : ''}`}
          onClick={() => onChange(n)}
          className={n <= value ? 'on' : ''}
        >
          <Star size={30} fill={n <= value ? 'currentColor' : 'none'} />
        </button>
      ))}
    </div>
  );
}
