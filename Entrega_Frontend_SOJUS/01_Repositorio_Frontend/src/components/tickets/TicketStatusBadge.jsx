/**
 * TicketStatusBadge — Badge de estado de ticket SOJUS
 */
import React from 'react';
import Badge from '../common/Badge';
import { colors } from '../../theme/colors';
import { TICKET_STATUS } from '../../utils/constants';

export default function TicketStatusBadge({ status, size = 'md' }) {
  const info = TICKET_STATUS[status] || {};
  const statusColors = colors.status[info.color] || {};

  return (
    <Badge
      label={info.label || status}
      color={statusColors.text}
      bgColor={statusColors.bg}
      borderColor={statusColors.border}
      size={size}
    />
  );
}
