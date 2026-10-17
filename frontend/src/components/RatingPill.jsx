import { Star } from 'lucide-react';

/** Green rating pill,  Shows "New user" when there are no ratings yet. */
export default function RatingPill({ value, count }) {
  if (!count) return <span className="rating rating-new">New user</span>;
  return (
    <span className="rating" title={`${count} rating${count > 1 ? 's' : ''}`}>
      {value.toFixed(1)} <Star size={11} fill="currentColor" />
    </span>
  );
}
