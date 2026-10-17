import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useSearchParams } from 'react-router-dom';
import api, { errMsg } from '../api';
import BookCard from '../components/BookCard';
import Empty from '../components/Empty';
import Pagination from '../components/Pagination';
import Spinner from '../components/Spinner';
import { useCategories } from '../context/CategoryContext';
import { CONDITIONS } from '../utils';

export default function Books() {
  const { categories } = useCategories();
  const [sp, setSp] = useSearchParams();
  const q = sp.get('q') || '';
  const category = sp.get('category') || '';
  const condition = sp.get('condition') || '';
  const city = sp.get('city') || '';
  const page = parseInt(sp.get('page') || '0', 10);

  const [data, setData] = useState(null);
  const [cityInput, setCityInput] = useState(city);

  useEffect(() => setCityInput(city), [city]);

  useEffect(() => {
    setData(null);
    api
      .get('/books/search', { params: { q, category, condition, city, page, size: 12 } })
      .then((res) => setData(res.data))
      .catch((e) => {
        toast.error(errMsg(e));
        setData({ content: [], totalElements: 0, totalPages: 0 });
      });
    window.scrollTo({ top: 0 });
  }, [q, category, condition, city, page]);

  const setParam = (key, value) => {
    const next = new URLSearchParams(sp);
    if (value) next.set(key, value);
    else next.delete(key);
    if (key !== 'page') next.delete('page');
    setSp(next);
  };

  const hasFilters = q || category || condition || city;
  const categoryName = categories.find((c) => c.id === category)?.name;

  return (
    <div className="container listing">
      <aside className="filters card" aria-label="Filters">
        <div className="filters-head">
          <h2>Filters</h2>
          {hasFilters && <button className="link-btn" onClick={() => setSp({})}>Clear all</button>}
        </div>

        <fieldset>
          <legend>Category</legend>
          <label className="opt"><input type="radio" name="cat" checked={!category} onChange={() => setParam('category', '')} /> All categories</label>
          {categories.map((c) => (
            <label className="opt" key={c.id}>
              <input type="radio" name="cat" checked={category === c.id} onChange={() => setParam('category', c.id)} /> {c.name}
            </label>
          ))}
        </fieldset>

        <fieldset>
          <legend>Condition</legend>
          <label className="opt"><input type="radio" name="cond" checked={!condition} onChange={() => setParam('condition', '')} /> Any condition</label>
          {CONDITIONS.map((c) => (
            <label className="opt" key={c}>
              <input type="radio" name="cond" checked={condition === c} onChange={() => setParam('condition', c)} /> {c}
            </label>
          ))}
        </fieldset>

        <fieldset>
          <legend>City</legend>
          <form
            className="city-form"
            onSubmit={(e) => {
              e.preventDefault();
              setParam('city', cityInput.trim());
            }}
          >
            <input value={cityInput} onChange={(e) => setCityInput(e.target.value)} placeholder="e.g. Virar" aria-label="City" />
            <button className="btn btn-blue btn-sm" type="submit">Apply</button>
          </form>
        </fieldset>
      </aside>

      <section className="results">
        <div className="results-head card">
          <h1>
            {q ? `Results for "${q}"` : categoryName || 'All books'}
          </h1>
          <span className="muted">
            {data ? `${data.totalElements} book${data.totalElements === 1 ? '' : 's'} available, newest first` : 'Searching...'}
          </span>
        </div>

        {data === null ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <Empty title="No books found" text="Try a different keyword or clear some filters." />
        ) : (
          <>
            <div className="grid grid-4">{data.content.map((b) => <BookCard key={b.id} book={b} />)}</div>
            <Pagination page={data.page} totalPages={data.totalPages} onChange={(p) => setParam('page', p ? String(p) : '')} />
          </>
        )}
      </section>
    </div>
  );
}
