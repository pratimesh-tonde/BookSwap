import { Route, Routes } from 'react-router-dom';
import CookieBanner from './components/CookieBanner';
import Footer from './components/Footer';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Admin from './pages/Admin';
import BookDetails from './pages/BookDetails';
import BookForm from './pages/BookForm';
import Books from './pages/Books';
import Chat from './pages/Chat';
import Cookies from './pages/Cookies';
import Home from './pages/Home';
import Login from './pages/Login';
import MyBooks from './pages/MyBooks';
import NotFound from './pages/NotFound';
import Notifications from './pages/Notifications';
import PrivacyPolicy from './pages/PrivacyPolicy';
import Profile from './pages/Profile';
import Register from './pages/Register';
import Requests from './pages/Requests';
import Terms from './pages/Terms';
import Wishlist from './pages/Wishlist';

const guard = (el, admin = false) => <ProtectedRoute admin={admin}>{el}</ProtectedRoute>;

export default function App() {
  return (
    <>
      <Navbar />
      <main className="page">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/books" element={<Books />} />
          <Route path="/books/new" element={guard(<BookForm />)} />
          <Route path="/books/:id" element={<BookDetails />} />
          <Route path="/books/:id/edit" element={guard(<BookForm />)} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/my-books" element={guard(<MyBooks />)} />
          <Route path="/requests" element={guard(<Requests />)} />
          <Route path="/chat/:id" element={guard(<Chat />)} />
          <Route path="/wishlist" element={guard(<Wishlist />)} />
          <Route path="/notifications" element={guard(<Notifications />)} />
          <Route path="/profile" element={guard(<Profile />)} />
          <Route path="/admin" element={guard(<Admin />, true)} />
          <Route path="/privacy-policy" element={<PrivacyPolicy />} />
          <Route path="/terms" element={<Terms />} />
          <Route path="/cookies" element={<Cookies />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
      <Footer />
      <CookieBanner />
    </>
  );
}
