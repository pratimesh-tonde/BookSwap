import { ArrowLeftRight, BookPlus, MessageCircle } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api';
import BookCard from '../components/BookCard';
import Spinner from '../components/Spinner';
import { useCategories } from '../context/CategoryContext';

const SPINE_COLORS = ['#ffe500', '#fb641b', '#ffffff', '#ff9f00', '#c8e6c9', '#ffccbc', '#bbdefb', '#f8bbd0', '#d1c4e9'];
const SPINE_HEIGHTS = [176, 206, 158, 192, 168, 210, 160, 188, 172];

export default function Home() {
  const { categories } = useCategories();
  const [books, setBooks] = useState(null);

  useEffect(() => {
    api.get('/books/search', { params: { size: 12 } })
      .then((res) => setBooks(res.data.content))
      .catch(() => setBooks([]));
  }, []);

  return (
    <div className="container">
      <section className="hero card">
        <div className="hero-text">
          <h1>Finished a book? Swap it for one you haven't read.</h1>
          <p>
            List the books gathering dust, find the ones you need, and exchange them with students in your city.
            No payment, no courier. Just chat and meet.
          </p>
          <div className="hero-actions">
            <Link to="/books" className="btn btn-cta">Browse books</Link>
            <Link to="/books/new" className="btn btn-ghost-light">List a book</Link>
          </div>
        </div>
        {categories.length > 0 && (
          <div className="shelf" aria-label="Browse by category">
            {categories.slice(0, 9).map((c, i) => (
              <Link
                key={c.id}
                to={`/books?category=${c.id}`}
                className="spine"
                style={{ height: SPINE_HEIGHTS[i % SPINE_HEIGHTS.length], background: SPINE_COLORS[i % SPINE_COLORS.length] }}
              >
                {c.name}
              </Link>
            ))}
          </div>
        )}
      </section>

      <section className="card section">
        <div className="section-head">
          <h2>Recently listed</h2>
          <Link to="/books" className="btn btn-blue btn-sm">View all</Link>
        </div>
        {books === null ? (
          <Spinner />
        ) : books.length === 0 ? (
          <p className="muted pad">No books are listed yet. Be the first one to list a book.</p>
        ) : (
          <div className="grid">{books.map((b) => <BookCard key={b.id} book={b} />)}</div>
        )}
      </section>

      <section className="card section">
        <div className="section-head"><h2>How swapping works</h2></div>
        <ol className="steps">
          <li>
            <span className="step-icon"><BookPlus size={26} /></span>
            <div><h3>List a book you've finished</h3><p>Add its title, condition and a photo. It takes under a minute.</p></div>
          </li>
          <li>
            <span className="step-icon"><ArrowLeftRight size={26} /></span>
            <div><h3>Request a swap</h3><p>Pick a book you want and offer one of yours. The owner accepts or declines.</p></div>
          </li>
          <li>
            <span className="step-icon"><MessageCircle size={26} /></span>
            <div><h3>Chat and hand over</h3><p>Talk in the built-in chat, meet, swap, and rate each other.</p></div>
          </li>
        </ol>
      </section>
    </div>
  );
}
