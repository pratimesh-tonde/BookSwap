export default function PrivacyPolicy() {
  return (
    <div className="container narrow">
      <div className="card section legal-page">
        <h1>Privacy Policy</h1>
        <p className="muted">Last updated: {new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' })}</p>

        <h2>1. What we collect</h2>
        <p>When you create an account we store your name, email, phone (optional) and city. When you list a book we store its details and, if you add one, its photo. When you use the chat we store your messages so the conversation can be reloaded.</p>

        <h2>2. How we use it</h2>
        <p>Your details are used to run the exchange service: showing your books to other users, connecting you with people you request a swap with, sending you notifications about your requests, and calculating your average rating from reviews.</p>

        <h2>3. What we never do</h2>
        <p>We do not sell your data to third parties and we do not show ads on this platform.</p>

        <h2>4. Cookies</h2>
        <p>We use a small number of cookies and browser storage to keep you logged in and to remember your preferences. See our <a href="/cookies">Cookies Notice</a> for details.</p>

        <h2>5. Your choices</h2>
        <p>You can update your profile details any time from the Profile page. To delete your account or data, contact the site admin.</p>
      </div>
    </div>
  );
}
