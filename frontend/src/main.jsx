import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import App from './App';
import { AuthProvider } from './context/AuthContext';
import { CategoryProvider } from './context/CategoryContext';
import { WishlistProvider } from './context/WishlistContext';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <CategoryProvider>
          <WishlistProvider>
            <App />
            <Toaster position="bottom-center" toastOptions={{ style: { fontSize: 14, borderRadius: 2 } }} />
          </WishlistProvider>
        </CategoryProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
