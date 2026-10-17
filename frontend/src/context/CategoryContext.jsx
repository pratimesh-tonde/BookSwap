import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import api from '../api';

const CategoryContext = createContext({ categories: [], reload: () => {} });

export function CategoryProvider({ children }) {
  const [categories, setCategories] = useState([]);
  const reload = useCallback(() => {
    api.get('/categories').then((res) => setCategories(res.data)).catch(() => {});
  }, []);
  useEffect(reload, [reload]);
  return <CategoryContext.Provider value={{ categories, reload }}>{children}</CategoryContext.Provider>;
}

export const useCategories = () => useContext(CategoryContext);
