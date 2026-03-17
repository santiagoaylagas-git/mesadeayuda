/**
 * Hook de tickets para SOJUS
 * Acceso al TicketContext desde cualquier componente
 */
import { useContext } from 'react';
import { TicketContext } from '../store/ticketStore';

const useTickets = () => {
  const context = useContext(TicketContext);
  if (!context) {
    throw new Error('useTickets debe usarse dentro de un TicketProvider');
  }
  return context;
};

export default useTickets;
