import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

/** Simple first-visit cookie banner. Choice is remembered in localStorage so it shows only once. */
export default function CookieBanner() {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    if (!localStorage.getItem('cookieConsent')) setVisible(true);
  }, []);

  const acceptAll = () => {
    localStorage.setItem('cookieConsent', 'accepted');
    localStorage.setItem('cookiePrefs', JSON.stringify({ essential: true, preferences: true, analytics: true }));
    setVisible(false);
  };

  const essentialOnly = () => {
    localStorage.setItem('cookieConsent', 'essential-only');
    localStorage.setItem('cookiePrefs', JSON.stringify({ essential: true, preferences: false, analytics: false }));
    setVisible(false);
  };

  if (!visible) return null;

  return (
    <div className="cookie-banner" role="dialog" aria-label="Cookie consent">
      <p>
        We use cookies to keep you logged in and to remember your preferences. See our{' '}
        <Link to="/cookies">Cookies Notice</Link> for details.
      </p>
      <div className="cookie-banner-actions">
        <button className="btn btn-ghost btn-sm" onClick={essentialOnly}>Essential only</button>
        <button className="btn btn-cta btn-sm" onClick={acceptAll}>Accept all</button>
      </div>
    </div>
  );
}
