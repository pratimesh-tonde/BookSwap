import { imgSrc } from '../api';

const PALETTES = [
  ['#2874f0', '#1a4fb8'], ['#e64a19', '#a83a12'], ['#00897b', '#00665c'], ['#8e24aa', '#651a7d'],
  ['#f9a825', '#b57a00'], ['#3949ab', '#26307a'], ['#d81b60', '#a0134a'], ['#43a047', '#2e7031'],
];

function hash(str = '') {
  let h = 0;
  for (let i = 0; i < str.length; i++) h = (h * 31 + str.charCodeAt(i)) >>> 0;
  return h;
}

/** Shows the uploaded photo, or a colourful generated cover when the owner did not add one. */
export default function BookCover({ book, className = '' }) {
  const src = imgSrc(book.imageUrl);
  if (src) return <img className={`cover ${className}`} src={src} alt={book.title} loading="lazy" />;
  const [a, b] = PALETTES[hash(book.title) % PALETTES.length];
  return (
    <div className={`cover cover-fallback ${className}`} style={{ background: `linear-gradient(160deg, ${a}, ${b})` }} role="img" aria-label={book.title}>
      <span className="cover-title">{book.title}</span>
      <span className="cover-author">{book.author}</span>
    </div>
  );
}
