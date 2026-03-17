/**
 * Configuración de deep linking para SOJUS
 */
const linking = {
  prefixes: ['sojus://', 'https://sojus.poderjudicial.gob.ar'],
  config: {
    screens: {
      Auth: {
        screens: {
          Login: 'login',
          ChangePassword: 'change-password',
        },
      },
      Main: {
        screens: {
          DashboardTab: {
            screens: {
              Dashboard: 'dashboard',
            },
          },
          TicketsTab: {
            screens: {
              TicketList: 'tickets',
              TicketDetail: 'tickets/:id',
              CreateTicket: 'tickets/new',
              EditTicket: 'tickets/:id/edit',
              MyTickets: 'tickets/my',
              AssignTechnician: 'tickets/:id/assign',
              TicketHistory: 'tickets/:id/history',
            },
          },
          CatalogTab: {
            screens: {
              ServiceCatalog: 'catalog',
              FAQ: 'catalog/faq',
              ServiceRequest: 'catalog/request',
            },
          },
          AdminTab: {
            screens: {
              UserManagement: 'admin/users',
              RoleManagement: 'admin/roles',
              TerritorialStructure: 'admin/territorial',
              Reports: 'admin/reports',
            },
          },
        },
      },
    },
  },
};

export default linking;
