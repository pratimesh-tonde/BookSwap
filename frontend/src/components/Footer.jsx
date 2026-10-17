import { ArrowUp } from 'lucide-react';
import { Link } from 'react-router-dom';

const COLUMNS = [
  {
    title: 'Get to know us',
    links: [
      { label: 'About BookSwap', to: '/' },
      { label: 'How it works', to: '/' },
      { label: 'Careers', to: '/' },
      { label: 'Press releases', to: '/' },
    ],
  },
  {
    title: 'Connect with us',
    links: [
      { label: 'Facebook', to: '/' },
      { label: 'Twitter', to: '/' },
      { label: 'Instagram', to: '/' },
    ],
  },
  {
    title: 'Make money with us',
    links: [
      { label: 'List a book', to: '/books/new' },
      { label: 'Become a top reviewer', to: '/' },
      { label: 'Refer a friend', to: '/' },
    ],
  },
  {
    title: 'Let us help you',
    links: [
      { label: 'My requests', to: '/requests' },
      { label: 'My wishlist', to: '/wishlist' },
      { label: 'Notifications', to: '/notifications' },
      { label: 'Help center', to: '/' },
    ],
  },
];

const DEVELOPER_URL = 'https://pratimesh-4ywzeysk.manus.space';

export default function Footer() {
  const year = new Date().getFullYear();

  const handleBackToTop = () => {
    document.documentElement.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    });
  };

  return (
    <footer className="footer">
      <button className="back-to-top" onClick={handleBackToTop}>
        <ArrowUp size={15} /> Back to top
      </button>

      <div className="footer-main">
        <div className="container footer-cols">
          {COLUMNS.map((col) => (
            <div className="footer-col" key={col.title}>
              <h4>{col.title}</h4>
              <nav aria-label={col.title}>
                {col.links.map((l) => (
                  <Link key={l.label} to={l.to}>{l.label}</Link>
                ))}
              </nav>
            </div>
          ))}
        </div>
      </div>

      <div className="footer-divider" />

      <div className="container footer-bottom">
        <Link to="/" className="footer-logo">BookSwap</Link>
        <p className="footer-tagline">Exchange books, don't buy them.</p>

        <nav className="footer-legal" aria-label="Legal">
          <Link to="/privacy-policy">Privacy Policy</Link>
          <span aria-hidden="true">|</span>
          <Link to="/terms">Terms of Use</Link>
          <span aria-hidden="true">|</span>
          <Link to="/cookies">Cookies Notice</Link>
          <span aria-hidden="true">|</span>
          <Link to="/cookies#preferences">Cookie Preferences</Link>
        </nav>

        <p className="footer-copy">&copy; {year} BookSwap. All rights reserved.</p>
        <p className="footer-copy" style={{ marginBlockStart: '5px' }}>
          Designed and Developed by{' '}
          <a href={DEVELOPER_URL} target="_blank" rel="noopener noreferrer" className="footer-credit-link">
            Pratimesh Tonde
          </a>{' '}
          ❤️| Powered by <strong>AIX Tech Ltd.</strong>
        </p>
      </div>
    </footer>
  );
}