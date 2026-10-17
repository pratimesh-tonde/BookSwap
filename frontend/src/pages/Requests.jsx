import { MessageCircle } from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Link } from 'react-router-dom';
import api, { errMsg, imgSrc } from '../api';
import Empty from '../components/Empty';
import Modal from '../components/Modal';
import Spinner from '../components/Spinner';
import StarInput from '../components/StarInput';
import StatusBadge from '../components/StatusBadge';
import { timeAgo } from '../utils';

function Row({ ex, tab, onAccept, onReject, onReview }) {
  return (
    <div className="row-item exchange-row">
      <div className="swap-books">
        <img src={imgSrc(ex.requestedBookImage) || undefined} className={`cover-sm ${!ex.requestedBookImage ? 'cover-fallback-sm' : ''}`} alt="" />
        <span className="swap-arrow">for</span>
        <img src={imgSrc(ex.offeredBookImage) || undefined} className={`cover-sm ${!ex.offeredBookImage ? 'cover-fallback-sm' : ''}`} alt="" />
      </div>
      <div className="row-main">
        <strong>{ex.requestedBookTitle}</strong> <span className="muted">for</span> <strong>{ex.offeredBookTitle}</strong>
        <span className="muted">
          {tab === 'sent' ? `To ${ex.ownerName}` : `From ${ex.requesterName}`} &middot; {timeAgo(ex.createdAt)}
        </span>
      </div>
      <StatusBadge status={ex.status} />
      <div className="row-actions">
        {tab === 'received' && ex.status === 'PENDING' && (
          <>
            <button className="btn btn-cta btn-sm" onClick={() => onAccept(ex)}>Accept</button>
            <button className="btn btn-ghost btn-sm danger" onClick={() => onReject(ex)}>Reject</button>
          </>
        )}
        {ex.status === 'ACCEPTED' && (
          <>
            <Link to={`/chat/${ex.id}`} className="btn btn-blue btn-sm"><MessageCircle size={15} /> Chat</Link>
            {!ex.reviewedByMe && <button className="btn btn-ghost btn-sm" onClick={() => onReview(ex)}>Rate & review</button>}
          </>
        )}
      </div>
    </div>
  );
}

export default function Requests() {
  const [tab, setTab] = useState('received');
  const [sent, setSent] = useState(null);
  const [received, setReceived] = useState(null);
  const [reviewEx, setReviewEx] = useState(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [busy, setBusy] = useState(false);

  const load = () => {
    api.get('/exchanges/sent').then((res) => setSent(res.data)).catch((e) => toast.error(errMsg(e)));
    api.get('/exchanges/received').then((res) => setReceived(res.data)).catch((e) => toast.error(errMsg(e)));
  };
  useEffect(() => { load(); }, []);

  const accept = async (ex) => {
    try {
      await api.put(`/exchanges/${ex.id}/accept`);
      toast.success('Request accepted. Both books are marked exchanged.');
      load();
    } catch (e) {
      toast.error(errMsg(e));
    }
  };
  const reject = async (ex) => {
    try {
      await api.put(`/exchanges/${ex.id}/reject`);
      toast('Request rejected');
      load();
    } catch (e) {
      toast.error(errMsg(e));
    }
  };

  const openReview = (ex) => {
    setReviewEx(ex);
    setRating(5);
    setComment('');
  };

  const submitReview = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      await api.post('/reviews', { exchangeId: reviewEx.id, rating, comment });
      toast.success('Thanks for the review!');
      setReviewEx(null);
      load();
    } catch (err) {
      toast.error(errMsg(err));
    } finally {
      setBusy(false);
    }
  };

  const list = tab === 'sent' ? sent : received;
  const reviewedName = reviewEx ? (reviewEx.requesterId === reviewEx.ownerId ? '' : '') : '';

  return (
    <div className="container">
      <div className="card section">
        <h1>Exchange requests</h1>
        <div className="tabs" role="tablist">
          <button role="tab" aria-selected={tab === 'received'} className={tab === 'received' ? 'active' : ''} onClick={() => setTab('received')}>
            Received {received && `(${received.length})`}
          </button>
          <button role="tab" aria-selected={tab === 'sent'} className={tab === 'sent' ? 'active' : ''} onClick={() => setTab('sent')}>
            Sent {sent && `(${sent.length})`}
          </button>
        </div>

        {list === null ? (
          <Spinner />
        ) : list.length === 0 ? (
          <Empty title={tab === 'sent' ? 'No requests sent yet' : 'No requests received yet'} text="Browse books and offer a swap to get started." />
        ) : (
          <div className="table-list">
            {list.map((ex) => (
              <Row key={ex.id} ex={ex} tab={tab} onAccept={accept} onReject={reject} onReview={openReview} />
            ))}
          </div>
        )}
      </div>

      {reviewEx && (
        <Modal title="Rate this exchange" onClose={() => setReviewEx(null)}>
          <form onSubmit={submitReview} className="form-grid">
            <div className="stars-wrap">
              <StarInput value={rating} onChange={setRating} />
            </div>
            <label>Comment (optional)<textarea rows={3} value={comment} onChange={(e) => setComment(e.target.value)} /></label>
            <button className="btn btn-cta btn-block" type="submit" disabled={busy}>{busy ? 'Submitting...' : 'Submit review'}</button>
          </form>
        </Modal>
      )}
    </div>
  );
}
