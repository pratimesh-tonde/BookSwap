import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';

const KEY = 'cookiePrefs';
const DEFAULTS = { essential: true, preferences: true, analytics: false };

function load() {
  try {
    return { ...DEFAULTS, ...JSON.parse(localStorage.getItem(KEY) || '{}') };
  } catch {
    return DEFAULTS;
  }
}

export default function Cookies() {
  const [prefs, setPrefs] = useState(load);

  
  useEffect(() => {
    if (window.location.hash === '#preferences') {
      document.getElementById('preferences')?.scrollIntoView({ behavior: 'smooth' });
    }
  }, []);

  const toggle = (key) => setPrefs((p) => ({ ...p, [key]: !p[key] }));

  const save = () => {
    localStorage.setItem(KEY, JSON.stringify(prefs));
    localStorage.setItem('cookieConsent', 'saved');
    toast.success('Cookie preferences saved');
  };

  return (
    <div className="container narrow">
      <div className="card section legal-page">
        <h1>Cookies Notice</h1>
        <p className="muted">Last updated: {new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' })}</p>

        <h2>What are cookies?</h2>
        <p>Cookies and similar browser storage are small pieces of data a site keeps on your device so it can remember things between visits.</p>

        <h2>What we use</h2>
        <ul className="legal-list">
          <li><strong>Essential</strong> — keeps you logged in (your session token) and lets the site work at all. Cannot be turned off.</li>
          <li><strong>Preferences</strong> — remembers small choices like your last search filters.</li>
          <li><strong>Analytics</strong> — off by default; would only be used to see which pages are visited most, never to identify you personally.</li>
        </ul>

        <h2 id="preferences">Your cookie preferences</h2>
        <div className="cookie-prefs">
          <label className="cookie-row">
            <span>
              <strong>Essential</strong>
              <small>Always on — required for login and core features.</small>
            </span>
            <input type="checkbox" checked disabled />
          </label>
          <label className="cookie-row">
            <span>
              <strong>Preferences</strong>
              <small>Remembers filters and small settings.</small>
            </span>
            <input type="checkbox" checked={prefs.preferences} onChange={() => toggle('preferences')} />
          </label>
          <label className="cookie-row">
            <span>
              <strong>Analytics</strong>
              <small>Helps us see which pages are used most.</small>
            </span>
            <input type="checkbox" checked={prefs.analytics} onChange={() => toggle('analytics')} />
          </label>
        </div>
        <button className="btn btn-cta" onClick={save}>Save preferences</button>
      </div>
    </div>
  );
}
