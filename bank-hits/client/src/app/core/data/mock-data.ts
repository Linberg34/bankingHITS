import type { User, Account, Transaction, CreditTariff, Credit } from '../models/client.types';

export const users: User[] = [
  {
    id: 'client-1',
    name: 'Иван Петров',
    email: 'ivan@example.com',
    role: 'client',
    blocked: false,
    createdAt: '2025-01-15T10:00:00Z',
  },
  {
    id: 'client-2',
    name: 'Мария Сидорова',
    email: 'maria@example.com',
    role: 'client',
    blocked: false,
    createdAt: '2025-02-01T12:00:00Z',
  },
  {
    id: 'employee-1',
    name: 'Алексей Иванов',
    email: 'alexey@bank.com',
    role: 'employee',
    blocked: false,
    createdAt: '2024-06-01T09:00:00Z',
  },
];

export const accounts: Account[] = [
  {
    id: 'acc-1',
    clientId: 'client-1',
    accountNumber: '40817810123456789012',
    balance: 150000,
    currency: 'RUB',
    status: 'active',
    createdAt: '2025-01-15T10:30:00Z',
  },
  {
    id: 'acc-2',
    clientId: 'client-1',
    accountNumber: '40817810987654321098',
    balance: 50000,
    currency: 'RUB',
    status: 'active',
    createdAt: '2025-02-10T14:00:00Z',
  },
  {
    id: 'acc-3',
    clientId: 'client-2',
    accountNumber: '40817810111222333444',
    balance: 300000,
    currency: 'RUB',
    status: 'active',
    createdAt: '2025-02-01T12:30:00Z',
  },
];

export const transactions: Transaction[] = [
  {
    id: 'tx-1',
    accountId: 'acc-1',
    type: 'deposit',
    amount: 100000,
    description: 'Пополнение счета',
    createdAt: '2025-01-20T11:00:00Z',
  },
  {
    id: 'tx-2',
    accountId: 'acc-1',
    type: 'withdrawal',
    amount: 20000,
    description: 'Снятие наличных',
    createdAt: '2025-01-25T15:30:00Z',
  },
  {
    id: 'tx-3',
    accountId: 'acc-1',
    type: 'credit_issue',
    amount: 50000,
    description: 'Выдача кредита',
    createdAt: '2025-02-01T10:00:00Z',
  },
  {
    id: 'tx-4',
    accountId: 'acc-1',
    type: 'credit_payment',
    amount: 5000,
    description: 'Оплата кредита',
    createdAt: '2025-02-05T09:00:00Z',
  },
  {
    id: 'tx-5',
    accountId: 'acc-3',
    type: 'deposit',
    amount: 300000,
    description: 'Пополнение счета',
    createdAt: '2025-02-02T10:00:00Z',
  },
];

export const creditTariffs: CreditTariff[] = [
  {
    id: 'tariff-1',
    name: 'Стандартный',
    interestRate: 12.5,
    createdAt: '2024-01-01T00:00:00Z',
    createdBy: 'employee-1',
  },
  {
    id: 'tariff-2',
    name: 'Премиум',
    interestRate: 8.0,
    createdAt: '2024-06-15T00:00:00Z',
    createdBy: 'employee-1',
  },
];

export const credits: Credit[] = [
  {
    id: 'credit-1',
    clientId: 'client-1',
    accountId: 'acc-1',
    tariffId: 'tariff-1',
    amount: 50000,
    remainingAmount: 45000,
    interestRate: 12.5,
    status: 'active',
    issueDate: '2025-02-01T10:00:00Z',
    nextPaymentDate: '2025-02-15T00:00:00Z',
    dailyPayment: 172,
  },
];
