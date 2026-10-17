import { PackageOpen } from 'lucide-react';

export default function Empty({ title, text, children }) {
  return (
    <div className="empty card">
      <PackageOpen size={56} strokeWidth={1.2} />
      <h3>{title}</h3>
      {text && <p>{text}</p>}
      {children}
    </div>
  );
}
