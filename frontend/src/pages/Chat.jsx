import { Client } from '@stomp/stompjs';
import { Send } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import toast from 'react-hot-toast';
import { Link, useParams } from 'react-router-dom';
import api, { errMsg, WS_URL } from '../api';
import BookCover from '../components/BookCover';
import Spinner from '../components/Spinner';
import { useAuth } from '../context/AuthContext';
import { fmtTime } from '../utils';

export default function Chat() {
  const { id } = useParams();
  const { user } = useAuth();
  const [exchange, setExchange] = useState(null);
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState('');
  const [connected, setConnected] = useState(false);
  const clientRef = useRef(null);
  const bottomRef = useRef(null);

  useEffect(() => {
    let sub;
    api.get(`/exchanges/${id}`).then((res) => setExchange(res.data)).catch((e) => toast.error(errMsg(e)));
    api.get(`/messages/${id}`).then((res) => setMessages(res.data)).catch((e) => toast.error(errMsg(e)));

    const client = new Client({
      brokerURL: WS_URL,
      connectHeaders: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      reconnectDelay: 4000,
      onConnect: () => {
        setConnected(true);
        sub = client.subscribe(`/topic/exchange/${id}`, (frame) => {
          const msg = JSON.parse(frame.body);
          setMessages((prev) => [...prev, msg]);
        });
      },
      onWebSocketError: () => setConnected(false),
      onStompError: () => toast.error('Chat connection error'),
    });
    client.activate();
    clientRef.current = client;

    return () => {
      sub?.unsubscribe();
      client.deactivate();
    };
  }, [id]);

  useEffect(() => bottomRef.current?.scrollIntoView({ behavior: 'smooth' }), [messages.length]);

  const send = (e) => {
    e.preventDefault();
    const clean = text.trim();
    if (!clean || !clientRef.current?.connected) return;
    clientRef.current.publish({ destination: `/app/chat/${id}`, body: JSON.stringify({ text: clean }) });
    setText('');
  };

  if (!exchange) return <Spinner />;
  const otherName = exchange.requesterId === user.id ? exchange.ownerName : exchange.requesterName;

  return (
    <div className="container narrow">
      <div className="card chat-card">
        <div className="chat-head">
          <div>
            <h2>Chat with {otherName}</h2>
            <span className="muted small">
              {exchange.requestedBookTitle} for {exchange.offeredBookTitle} &middot; {connected ? 'Connected' : 'Connecting...'}
            </span>
          </div>
          <Link to="/requests" className="btn btn-ghost btn-sm">Back to requests</Link>
        </div>

        <div className="chat-body">
          {messages.length === 0 && <p className="muted pad">Say hello and arrange where to meet.</p>}
          {messages.map((m) => (
            <div key={m.id || m.sentAt + m.text} className={`bubble ${m.senderId === user.id ? 'mine' : ''}`}>
              <p>{m.text}</p>
              <span>{fmtTime(m.sentAt)}</span>
            </div>
          ))}
          <div ref={bottomRef} />
        </div>

        <form className="chat-input" onSubmit={send}>
          <input value={text} onChange={(e) => setText(e.target.value)} placeholder="Type a message..." maxLength={1000} />
          <button className="btn btn-cta" type="submit" disabled={!text.trim() || !connected} aria-label="Send"><Send size={18} /></button>
        </form>
      </div>
    </div>
  );
}
