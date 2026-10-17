import {
  Book, BookOpen, Calculator, Cpu, FlaskConical, GraduationCap, Landmark, Lightbulb, Smile,
} from 'lucide-react';

export const CONDITIONS = ['New', 'Like New', 'Good', 'Fair'];

export function iconForCategory(name = '') {
  const n = name.toLowerCase();
  if (/engineer|tech|comput|program/.test(n)) return Cpu;
  if (/math/.test(n)) return Calculator;
  if (/science|physics|chem|bio/.test(n)) return FlaskConical;
  if (/exam|compet|study|school|college/.test(n)) return GraduationCap;
  if (/novel|fiction/.test(n)) return BookOpen;
  if (/comic|manga|kid/.test(n)) return Smile;
  if (/self|motiv|business/.test(n)) return Lightbulb;
  if (/history|non|politic|biograph/.test(n)) return Landmark;
  return Book;
}

export function timeAgo(iso) {
  if (!iso) return '';
  const s = Math.floor((Date.now() - new Date(iso).getTime()) / 1000);
  if (s < 60) return 'just now';
  if (s < 3600) return `${Math.floor(s / 60)} min ago`;
  if (s < 86400) return `${Math.floor(s / 3600)} h ago`;
  if (s < 86400 * 7) return `${Math.floor(s / 86400)} d ago`;
  return new Date(iso).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

export const fmtTime = (iso) =>
  iso ? new Date(iso).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' }) : '';
