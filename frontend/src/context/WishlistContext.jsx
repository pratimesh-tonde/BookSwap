import { createContext, useContext, useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import api, { errMsg } from '../api';
import { useAuth } from './AuthContext';

const WishlistContext = createContext({ ids: new Set(), toggle: async () => {} });

export function WishlistProvider({ children }) {
  const { user } = useAuth();
  const [ids, setIds] = useState(new Set());

  useEffect(() => {
    if (!user) {
      setIds(new Set());
      return;
    }
    api.get('/wishlist').then((res) => setIds(new Set(res.data.map((b) => b.id)))).catch(() => {});
  }, [user?.id]);

  const toggle = async (bookId) => {
    const has = ids.has(bookId);
    try {
      if (has) await api.delete(`/wishlist/${bookId}`);
      else await api.post(`/wishlist/${bookId}`);
      setIds((prev) => {
        const next = new Set(prev);
        if (has) next.delete(bookId);
        else next.add(bookId);
        return next;
      });
      toast.success(has ? 'Removed from wishlist' : 'Saved to wishlist');
    } catch (e) {
      toast.error(errMsg(e));
    }
  };

  return <WishlistContext.Provider value={{ ids, toggle }}>{children}</WishlistContext.Provider>;
}

export const useWishlist = () => useContext(WishlistContext);
