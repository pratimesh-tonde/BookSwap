import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Link } from 'react-router-dom';
import api, { errMsg } from '../api';
import BookCard from '../components/BookCard';
import Empty from '../components/Empty';
import Spinner from '../components/Spinner';

export default function Wishlist() {
  const [books, setBooks] = useState(null);

  useEffect(() => {
    api.get('/wishlist').then((res) => setBooks(res.data)).catch((e) => toast.error(errMsg(e)));
  }, []);

  if (books === null) return <Spinner />;

  return (
    <div className="container">
      <div className="section-head card"><h1>My wishlist</h1></div>
      {books.length === 0 ? (
        <Empty title="Your wishlist is empty" text="Save books you're interested in to find them again easily.">
          <Link to="/books" className="btn btn-cta">Browse books</Link>
        </Empty>
      ) : (
        <div className="grid grid-4">{books.map((b) => <BookCard key={b.id} book={b} />)}</div>
      )}
    </div>
  );
}
