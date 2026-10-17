import { Link2, Upload } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate, useParams } from 'react-router-dom';
import api, { errMsg, imgSrc } from '../api';
import Spinner from '../components/Spinner';
import { useCategories } from '../context/CategoryContext';
import { CONDITIONS } from '../utils';

const EMPTY = { title: '', author: '', categoryId: '', condition: 'Good', description: '', imageUrl: '' };

export default function BookForm() {
  const { id } = useParams();
  const editing = !!id;
  const navigate = useNavigate();
  const { categories } = useCategories();
  const fileInput = useRef(null);

  const [form, setForm] = useState(EMPTY);
  const [loading, setLoading] = useState(editing);
  const [uploading, setUploading] = useState(false);
  const [busy, setBusy] = useState(false);

  // "upload" = pick a file from the device, "link" = paste an image URL directly
  const [photoMode, setPhotoMode] = useState('upload');
  const [linkInput, setLinkInput] = useState('');
  const [linkBroken, setLinkBroken] = useState(false);

  useEffect(() => {
    if (!editing) return;
    api.get(`/books/${id}`)
      .then((res) => {
        const b = res.data;
        setForm({ title: b.title, author: b.author, categoryId: b.categoryId, condition: b.condition,
                   description: b.description || '', imageUrl: b.imageUrl || '' });
        if (b.imageUrl?.startsWith('http')) {
          setPhotoMode('link');
          setLinkInput(b.imageUrl);
        }
      })
      .catch((e) => toast.error(errMsg(e)))
      .finally(() => setLoading(false));
  }, [id, editing]);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const onFile = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      const fd = new FormData();
      fd.append('file', file);
      const res = await api.post('/files/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } });
      setForm((f) => ({ ...f, imageUrl: res.data.url }));
    } catch (err) {
      toast.error(errMsg(err, 'Could not upload the image'));
    } finally {
      setUploading(false);
    }
  };

  const switchMode = (mode) => {
    setPhotoMode(mode);
    setLinkBroken(false);
    if (mode === 'upload') {
      setLinkInput('');
      if (form.imageUrl.startsWith('http')) setForm((f) => ({ ...f, imageUrl: '' }));
    }
  };

  const onLinkChange = (e) => {
    const url = e.target.value;
    setLinkInput(url);
    setLinkBroken(false);
    setForm((f) => ({ ...f, imageUrl: url.trim() }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      if (editing) {
        await api.put(`/books/${id}`, form);
        toast.success('Book updated');
        navigate(`/books/${id}`);
      } else {
        const res = await api.post('/books', form);
        toast.success('Book listed');
        navigate(`/books/${res.data.id}`);
      }
    } catch (err) {
      toast.error(errMsg(err));
    } finally {
      setBusy(false);
    }
  };

  if (loading) return <Spinner />;

  return (
    <div className="container narrow">
      <div className="card section">
        <h1>{editing ? 'Edit book' : 'List a book for exchange'}</h1>
        <form onSubmit={onSubmit} className="form-grid">
          <label>Title<input required value={form.title} onChange={set('title')} /></label>
          <label>Author<input required value={form.author} onChange={set('author')} /></label>

          <label>
            Category
            <select required value={form.categoryId} onChange={set('categoryId')}>
              <option value="" disabled>Choose a category</option>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </label>

          <label>
            Condition
            <select required value={form.condition} onChange={set('condition')}>
              {CONDITIONS.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </label>

          <label className="span-2">
            Description (optional)
            <textarea rows={4} value={form.description} onChange={set('description')} placeholder="Edition, any notes about the book..." />
          </label>

          <div className="span-2 upload-block">
            <span className="upload-label">Photo (optional)</span>

            <div className="photo-mode-toggle" role="tablist">
              <button
                type="button"
                role="tab"
                aria-selected={photoMode === 'upload'}
                className={photoMode === 'upload' ? 'active' : ''}
                onClick={() => switchMode('upload')}
              >
                <Upload size={15} /> Upload from device
              </button>
              <button
                type="button"
                role="tab"
                aria-selected={photoMode === 'link'}
                className={photoMode === 'link' ? 'active' : ''}
                onClick={() => switchMode('link')}
              >
                <Link2 size={15} /> Paste image link
              </button>
            </div>

            {photoMode === 'upload' ? (
              <>
                <input ref={fileInput} type="file" accept="image/*" onChange={onFile} disabled={uploading} />
                {uploading && <span className="muted">Uploading...</span>}
              </>
            ) : (
              <input
                type="url"
                value={linkInput}
                onChange={onLinkChange}
                placeholder="https://example.com/book-cover.jpg"
              />
            )}

            {form.imageUrl && !linkBroken && (
              <img
                className="upload-preview"
                src={imgSrc(form.imageUrl)}
                alt="Book preview"
                onError={() => setLinkBroken(true)}
              />
            )}
            {linkBroken && <span className="muted small">Couldn't load an image from that link — check the URL.</span>}
          </div>

          <button className="btn btn-cta span-2" type="submit" disabled={busy || uploading}>
            {busy ? 'Saving...' : editing ? 'Save changes' : 'List this book'}
          </button>
        </form>
      </div>
    </div>
  );
}
