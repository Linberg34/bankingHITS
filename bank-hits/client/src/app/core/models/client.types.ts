export interface User {
  id: string;
  name: string;
  email: string;
  role: 'client' | 'employee';
  blocked: boolean;
  createdAt: string;
}

export interface Account {
  id: string;
  clientId: string;
  accountNumber: string;
  balance: number;
  currency: string;
  status: 'active' | 'closed';
  createdAt: string;
  closedAt?: string;
}

export interface Transaction {
  id: string;
  accountId: string;
  type: 'deposit' | 'withdrawal' | 'credit_issue' | 'credit_payment';
  amount: number;
  description: string;
  createdAt: string;
}

export interface CreditTariff {
  id: string;
  name: string;
  interestRate: number;
  createdAt: string;
  createdBy: string;
}

export interface Credit {
  id: string;
  clientId: string;
  accountId: string;
  tariffId: string;
  amount: number;
  remainingAmount: number;
  interestRate: number;
  status: 'active' | 'paid' | 'overdue';
  issueDate: string;
  nextPaymentDate: string;
  dailyPayment: number;
}
