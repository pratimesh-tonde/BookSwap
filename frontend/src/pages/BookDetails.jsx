import { MapPin, Repeat } from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Link, useNavigate, useParams } from 'react-router-dom';
import api, { errMsg } from '../api';
import BookCover from '../components/BookCover';
import Empty from '../components/Empty';
import Modal from '../components/Modal';
import RatingPill from '../components/RatingPill';
import Spinner from '../components/Spinner';
import StatusBadge from '../components/StatusBadge';
import { useAuth } from '../context/AuthContext';
import { useWishlist } from '../context/WishlistContext';
import { timeAgo } from '../utils';

export default function BookDetails() {
  const { id } = useParams();
  const { user } = useAuth();
  const { ids, toggle } = useWishlist();
  const navigate = useNavigate();

  const [book, setBook] = useState(null);
  const [notFound, setNotFound] = useState(false);
  const [reviews, setReviews] = useState([]);
  const [swapOpen, setSwapOpen] = useState(false);
  const [myBooks, setMyBooks] = useState([]);
  const [offeredId, setOfferedId] = useState('');
  const [sending, setSending] = useState(false);

  const load = () => {
    api.get(`/books/${id}`)
      .then((res) => {
        setBook(res.data);
        api.get(`/users/${res.data.ownerId}/reviews`).then((r) => setReviews(r.data)).catch(() => {});
      })
      .catch(() => setNotFound(true));
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [id]);

  const openSwap = () => {
    if (!user) return navigate('/login', { state: { from: { pathname: `/books/${id}` } } });
    api.get('/books/my').then((res) => setMyBooks(res.data.filter((b) => b.status === 'AVAILABLE')));
    setOfferedId('');
    setSwapOpen(true);
  };

  const sendRequest = async (e) => {
    e.preventDefault();
    setSending(true);
    try {
      await api.post('/exchanges', { requestedBookId: id, offeredBookId: offeredId });
      toast.success('Exchange request sent!');
      setSwapOpen(false);
      navigate('/requests');
    } catch (err) {
      toast.error(errMsg(err));
    } finally {
      setSending(false);
    }
  };

  if (notFound) return <div className="container"><Empty title="Book not found" text="It may have been removed." /></div>;
  if (!book) return <Spinner />;

  const isOwner = user?.id === book.ownerId;
  const saved = ids.has(book.id);

  return (
    <div className="container narrow">
      <div className="card details">
        <div className="details-media">
          <BookCover book={book} className="cover-lg" />
        </div>
        <div className="details-body">
          <StatusBadge status={book.status} />
          <h1>{book.title}</h1>
          <p className="details-author">by {book.author}</p>
          <p className="cond-line">Condition: <strong>{book.condition}</strong> &middot; {book.categoryName}</p>

          <Link to="#owner" className="owner-line" onClick={(e) => e.preventDefault()}>
            <span>{isOwner ? 'You' : book.ownerName}</span>
            <RatingPill value={book.ownerRating} count={book.ownerRatingCount} />
          </Link>
          <p className="book-city"><MapPin size={14} /> {book.ownerCity} &middot; listed {timeAgo(book.createdAt)}</p>

          {book.description && <p className="details-desc">{book.description}</p>}

          <div className="details-actions">
            {isOwner ? (
              <>
                <Link to={`/books/${book.id}/edit`} className="btn btn-blue">Edit book</Link>
              </>
            ) : book.status === 'AVAILABLE' ? (
              <button className="btn btn-cta" onClick={openSwap}><Repeat size={18} /> Request exchange</button>
            ) : (
              <button className="btn btn-disabled" disabled>Already exchanged</button>
            )}
            {!isOwner && (
              <button className={`btn ${saved ? 'btn-blue' : 'btn-ghost'}`} onClick={() => (user ? toggle(book.id) : navigate('/login'))}>
                {saved ? 'Saved to wishlist' : 'Save to wishlist'}
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="card section">
        <h2>Reviews for {isOwner ? 'you' : book.ownerName}</h2>
        {reviews.length === 0 ? (
          <p className="muted">No reviews yet.</p>
        ) : (
          <ul className="review-list">
            {reviews.map((r) => (
              <li key={r.id}>
                <div className="review-top"><strong>{r.reviewerName}</strong><RatingPill value={r.rating} count={1} /></div>
                {r.comment && <p>{r.comment}</p>}
                <span className="muted small">{timeAgo(r.createdAt)}</span>
              </li>
            ))}
          </ul>
        )}
      </div>

      {swapOpen && (
        <Modal title="Choose a book to offer" onClose={() => setSwapOpen(false)}>
          {myBooks.length === 0 ? (
            <div>
              <p>You need at least one available book of your own to offer in exchange.</p>
              <Link to="/books/new" className="btn btn-cta">List a book</Link>
            </div>
          ) : (
            <form onSubmit={sendRequest} className="swap-form">
              <p className="muted">In exchange for "{book.title}", offer:</p>
              <div className="swap-options">
                {myBooks.map((b) => (
                  <label key={b.id} className={`swap-option ${offeredId === b.id ? 'selected' : ''}`}>
                    <input type="radio" name="offer" value={b.id} checked={offeredId === b.id} onChange={() => setOfferedId(b.id)} />
                    <BookCover book={b} className="cover-sm" />
                    <span>{b.title}</span>
                  </label>
                ))}
              </div>
              <button className="btn btn-cta btn-block" type="submit" disabled={!offeredId || sending}>
                {sending ? 'Sending...' : 'Send exchange request'}
              </button>
            </form>
          )}
        </Modal>
      )}
    </div>
  );
}
