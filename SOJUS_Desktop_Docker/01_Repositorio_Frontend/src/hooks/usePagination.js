/**
 * Hook de paginación para SOJUS
 * Manejo genérico de paginación infinita con FlatList
 */
import { useState, useCallback } from 'react';

/**
 * @param {Function} fetchFn - Función que recibe { page, size } y retorna datos paginados
 * @param {number} pageSize - Tamaño de página (default: 20)
 */
const usePagination = (fetchFn, pageSize = 20) => {
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [total, setTotal] = useState(0);

  /** Cargar primera página (reset) */
  const refresh = useCallback(async (extraParams = {}) => {
    setIsRefreshing(true);
    try {
      const response = await fetchFn({ page: 0, size: pageSize, ...extraParams });
      const result = response.data;

      if (result.content) {
        // Respuesta paginada de Spring Boot
        setData(result.content);
        setHasMore(!result.last);
        setTotal(result.totalElements);
      } else if (Array.isArray(result)) {
        // Respuesta simple (array)
        setData(result);
        setHasMore(false);
        setTotal(result.length);
      }
      setPage(0);
    } catch (error) {
      throw error;
    } finally {
      setIsRefreshing(false);
      setIsLoading(false);
    }
  }, [fetchFn, pageSize]);

  /** Cargar siguiente página */
  const loadMore = useCallback(async (extraParams = {}) => {
    if (!hasMore || isLoading) return;
    setIsLoading(true);
    try {
      const nextPage = page + 1;
      const response = await fetchFn({ page: nextPage, size: pageSize, ...extraParams });
      const result = response.data;

      if (result.content) {
        setData((prev) => [...prev, ...result.content]);
        setHasMore(!result.last);
        setTotal(result.totalElements);
      }
      setPage(nextPage);
    } catch (error) {
      // No lanzar error en loadMore, es no-crítico
    } finally {
      setIsLoading(false);
    }
  }, [fetchFn, pageSize, page, hasMore, isLoading]);

  return {
    data,
    page,
    hasMore,
    isLoading,
    isRefreshing,
    total,
    refresh,
    loadMore,
    setData,
  };
};

export default usePagination;
