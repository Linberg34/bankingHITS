export interface UserRecord {
  name: string;
  email: string;
  status: string;
  registeredAt: string;
}

export const CLIENT_USERS: UserRecord[] = [
  {
    name: 'Иван Петров',
    email: 'ivan@example.com',
    status: 'Активен',
    registeredAt: '01.01.2025',
  },
  {
    name: 'Мария Сидорова',
    email: 'maria@example.com',
    status: 'Активен',
    registeredAt: '05.01.2025',
  },
];

export const EMPLOYEE_USERS: UserRecord[] = [
  {
    name: 'Алексей Смирнов',
    email: 'admin@bank.com',
    status: 'Активен',
    registeredAt: '01.12.2024',
  },
];
