const LABELS = {
  PENDING: 'Pending',
  ACCEPTED: 'Accepted',
  REJECTED: 'Rejected',
  AVAILABLE: 'Available',
  EXCHANGED: 'Exchanged',
};

export default function StatusBadge({ status }) {
  return <span className={`badge badge-${status.toLowerCase()}`}>{LABELS[status] || status}</span>;
}
