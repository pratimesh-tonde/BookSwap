import { Heart, MapPin } from 'lucide-react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useWishlist } from '../context/WishlistContext';
import BookCover from './BookCover';
import RatingPill from './RatingPill';

export default function BookCard({ book }) {
  const { user } = useAuth();
  const { ids, toggle } = useWishlist();
  const navigate = useNavigate();
  const location = useLocation();
  const saved = ids.has(book.id);

  const onHeart = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!user) return navigate('/login', { state: { from: location } });
    toggle(book.id);
  };

  return (
    <Link to={`/books/${book.id}`} className="book-card card">
      <button className={`heart ${saved ? 'saved' : ''}`} onClick={onHeart} aria-label={saved ? 'Remove from wishlist' : 'Save to wishlist'}>
        <Heart size={18} fill={saved ? 'currentColor' : 'none'} />
      </button>
      <div className="book-card-img">
        <BookCover book={book} />
      </div>
      <div className="book-card-body">
        <h3 className="book-title" title={book.title}>{book.title}</h3>
        <p className="book-author">{book.author}</p>
        <div className="book-meta">
          <RatingPill value={book.ownerRating} count={book.ownerRatingCount} />
          <span className="cond">{book.condition}</span>
        </div>
        <p className="book-city"><MapPin size={13} /> {book.ownerCity || 'Unknown city'}</p>
        <p className="swap-tag">Swap for a book of yours</p>
      </div>
    </Link>
  );
}
