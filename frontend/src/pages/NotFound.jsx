import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="container">
      <div className="empty card">
        <h1>404</h1>
        <p>This page does not exist.</p>
        <Link to="/" className="btn btn-blue">Go home</Link>
      </div>
    </div>
  );
}
