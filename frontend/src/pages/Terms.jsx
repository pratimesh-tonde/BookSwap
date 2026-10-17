export default function Terms() {
  return (
    <div className="container narrow">
      <div className="card section legal-page">
        <h1>Terms of Use</h1>
        <p className="muted">Last updated: {new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' })}</p>

        <h2>1. About this platform</h2>
        <p>BookSwap is a student mini project that lets registered users list books and exchange them with other users, book-for-book. It does not handle payments, shipping or price valuation.</p>

        <h2>2. Your account</h2>
        <p>You are responsible for the accuracy of the books you list and for keeping your login details safe. Accounts that violate these terms may be blocked by an administrator.</p>

        <h2>3. Exchanges</h2>
        <p>Both users are responsible for arranging and verifying the physical handover of books. BookSwap only provides the listing, request and chat tools.</p>

        <h2>4. Content</h2>
        <p>Do not upload images or descriptions you don't have the right to use, or that are offensive or misleading.</p>
      </div>
    </div>
  );
}
