/**
 * TicketStore — Estado global de tickets para SOJUS
 * Context API + useReducer con paginación y filtros
 */
import React, { createContext, useReducer, useCallback } from 'react';
import { ticketService } from '../api';
import { getErrorMessage } from '../utils/errorHandler';

const initialState = {
  tickets: [],
  currentTicket: null,
  pagination: { page: 0, size: 20, total: 0, hasMore: true },
  filters: { status: null, priority: null, assignedTo: null, search: '' },
  isLoading: false,
  error: null,
  stats: { open: 0, inProgress: 0, resolved: 0, critical: 0 },
};

const TICKET_ACTIONS = {
  SET_LOADING: 'SET_LOADING',
  SET_TICKETS: 'SET_TICKETS',
  APPEND_TICKETS: 'APPEND_TICKETS',
  SET_CURRENT_TICKET: 'SET_CURRENT_TICKET',
  ADD_TICKET: 'ADD_TICKET',
  UPDATE_TICKET: 'UPDATE_TICKET',
  SET_FILTERS: 'SET_FILTERS',
  SET_STATS: 'SET_STATS',
  SET_ERROR: 'SET_ERROR',
  CLEAR_ERROR: 'CLEAR_ERROR',
};

const ticketReducer = (state, action) => {
  switch (action.type) {
    case TICKET_ACTIONS.SET_LOADING:
      return { ...state, isLoading: action.payload };
    case TICKET_ACTIONS.SET_TICKETS:
      return {
        ...state,
        tickets: action.payload.content || action.payload,
        pagination: {
          ...state.pagination,
          total: action.payload.totalElements || action.payload.length || 0,
          hasMore: action.payload.content
            ? !action.payload.last
            : false,
        },
        isLoading: false,
        error: null,
      };
    case TICKET_ACTIONS.APPEND_TICKETS:
      return {
        ...state,
        tickets: [...state.tickets, ...(action.payload.content || action.payload)],
        pagination: {
          ...state.pagination,
          page: state.pagination.page + 1,
          hasMore: action.payload.content ? !action.payload.last : false,
        },
        isLoading: false,
      };
    case TICKET_ACTIONS.SET_CURRENT_TICKET:
      return { ...state, currentTicket: action.payload, isLoading: false };
    case TICKET_ACTIONS.ADD_TICKET:
      return { ...state, tickets: [action.payload, ...state.tickets] };
    case TICKET_ACTIONS.UPDATE_TICKET:
      return {
        ...state,
        tickets: state.tickets.map((t) =>
          t.id === action.payload.id ? { ...t, ...action.payload } : t
        ),
        currentTicket:
          state.currentTicket?.id === action.payload.id
            ? { ...state.currentTicket, ...action.payload }
            : state.currentTicket,
      };
    case TICKET_ACTIONS.SET_FILTERS:
      return {
        ...state,
        filters: { ...state.filters, ...action.payload },
        pagination: { ...state.pagination, page: 0 },
      };
    case TICKET_ACTIONS.SET_STATS:
      return { ...state, stats: action.payload };
    case TICKET_ACTIONS.SET_ERROR:
      return { ...state, error: action.payload, isLoading: false };
    case TICKET_ACTIONS.CLEAR_ERROR:
      return { ...state, error: null };
    default:
      return state;
  }
};

export const TicketContext = createContext(null);

export const TicketProvider = ({ children }) => {
  const [state, dispatch] = useReducer(ticketReducer, initialState);

  /** Cargar tickets (primera página o reset) */
  const fetchTickets = useCallback(async (params = {}) => {
    dispatch({ type: TICKET_ACTIONS.SET_LOADING, payload: true });
    try {
      const response = await ticketService.getAll({
        ...state.filters,
        page: 0,
        size: state.pagination.size,
        ...params,
      });
      dispatch({ type: TICKET_ACTIONS.SET_TICKETS, payload: response.data });
    } catch (error) {
      dispatch({ type: TICKET_ACTIONS.SET_ERROR, payload: getErrorMessage(error) });
    }
  }, [state.filters, state.pagination.size]);

  /** Cargar más tickets (paginación infinita) */
  const fetchMoreTickets = useCallback(async () => {
    if (!state.pagination.hasMore || state.isLoading) return;
    dispatch({ type: TICKET_ACTIONS.SET_LOADING, payload: true });
    try {
      const response = await ticketService.getAll({
        ...state.filters,
        page: state.pagination.page + 1,
        size: state.pagination.size,
      });
      dispatch({ type: TICKET_ACTIONS.APPEND_TICKETS, payload: response.data });
    } catch (error) {
      dispatch({ type: TICKET_ACTIONS.SET_ERROR, payload: getErrorMessage(error) });
    }
  }, [state.filters, state.pagination, state.isLoading]);

  /** Cargar detalle de un ticket */
  const fetchTicket = useCallback(async (id) => {
    dispatch({ type: TICKET_ACTIONS.SET_LOADING, payload: true });
    try {
      const response = await ticketService.getById(id);
      dispatch({ type: TICKET_ACTIONS.SET_CURRENT_TICKET, payload: response.data });
      return response.data;
    } catch (error) {
      dispatch({ type: TICKET_ACTIONS.SET_ERROR, payload: getErrorMessage(error) });
      throw error;
    }
  }, []);

  /** Crear ticket */
  const createTicket = useCallback(async (data) => {
    const response = await ticketService.create(data);
    dispatch({ type: TICKET_ACTIONS.ADD_TICKET, payload: response.data });
    return response.data;
  }, []);

  /** Actualizar ticket */
  const updateTicket = useCallback(async (id, data) => {
    const response = await ticketService.update(id, data);
    dispatch({ type: TICKET_ACTIONS.UPDATE_TICKET, payload: response.data });
    return response.data;
  }, []);

  /** Cambiar estado */
  const changeStatus = useCallback(async (id, data) => {
    const response = await ticketService.changeStatus(id, data);
    dispatch({ type: TICKET_ACTIONS.UPDATE_TICKET, payload: response.data });
    return response.data;
  }, []);

  /** Asignar técnico */
  const assignTechnician = useCallback(async (id, data) => {
    const response = await ticketService.assign(id, data);
    dispatch({ type: TICKET_ACTIONS.UPDATE_TICKET, payload: response.data });
    return response.data;
  }, []);

  /** Establecer filtros */
  const setFilters = useCallback((filters) => {
    dispatch({ type: TICKET_ACTIONS.SET_FILTERS, payload: filters });
  }, []);

  /** Cargar estadísticas */
  const fetchStats = useCallback(async () => {
    try {
      const response = await ticketService.getStats();
      dispatch({ type: TICKET_ACTIONS.SET_STATS, payload: response.data });
    } catch (error) {
      // Las stats son no-críticas, no bloquear la UI
    }
  }, []);

  const value = {
    ...state,
    fetchTickets,
    fetchMoreTickets,
    fetchTicket,
    createTicket,
    updateTicket,
    changeStatus,
    assignTechnician,
    setFilters,
    fetchStats,
    clearError: () => dispatch({ type: TICKET_ACTIONS.CLEAR_ERROR }),
  };

  return (
    <TicketContext.Provider value={value}>
      {children}
    </TicketContext.Provider>
  );
};
