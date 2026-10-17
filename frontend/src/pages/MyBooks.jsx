import { BookPlus } from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Link } from 'react-router-dom';
import api, { errMsg } from '../api';
import BookCover from '../components/BookCover';
import Empty from '../components/Empty';
import Spinner from '../components/Spinner';
import StatusBadge from '../components/StatusBadge';

export default function MyBooks() {
  const [books, setBooks] = useState(null);

  const load = () => api.get('/books/my').then((res) => setBooks(res.data)).catch((e) => toast.error(errMsg(e)));
  useEffect(() => { load(); }, []);

  const remove = async (book) => {
    if (!window.confirm(`Remove "${book.title}"? Any pending requests for it will be declined.`)) return;
    try {
      await api.delete(`/books/${book.id}`);
      toast.success('Book removed');
      load();
    } catch (e) {
      toast.error(errMsg(e));
    }
  };

  if (books === null) return <Spinner />;

  return (
    <div className="container">
      <div className="section-head card">
        <h1>My books</h1>
        <Link to="/books/new" className="btn btn-cta"><BookPlus size={18} /> List a book</Link>
      </div>

      {books.length === 0 ? (
        <Empty title="You haven't listed any books" text="List a book you've finished reading to start swapping.">
          <Link to="/books/new" className="btn btn-cta">List your first book</Link>
        </Empty>
      ) : (
        <div className="table-list card">
          {books.map((b) => (
            <div className="row-item" key={b.id}>
              <Link to={`/books/${b.id}`}><BookCover book={b} className="cover-sm" /></Link>
              <div className="row-main">
                <Link to={`/books/${b.id}`}><strong>{b.title}</strong></Link>
                <span className="muted">{b.author} &middot; {b.condition}</span>
              </div>
              <StatusBadge status={b.status} />
              <div className="row-actions">
                {b.status === 'AVAILABLE' && <Link to={`/books/${b.id}/edit`} className="btn btn-ghost btn-sm">Edit</Link>}
                <button className="btn btn-ghost btn-sm danger" onClick={() => remove(b)}>Remove</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
