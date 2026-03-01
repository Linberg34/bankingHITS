import type { AccountDto, AccountOperationDto } from 'shared/entities/accounts';
import type { Account, Transaction } from '../models/client.types';

type AccountDtoWithId = AccountDto & { id?: number };

export function mapAccountDtoToAccount(dto: AccountDtoWithId): Account {
  const id = dto.id != null ? String(dto.id) : dto.accountNumber;
  const status = String(dto.status).toLowerCase() === 'active' ? 'active' : 'closed';
  return {
    id,
    clientId: String(dto.clientId),
    accountNumber: dto.accountNumber,
    balance: Number(dto.balance),
    currency: 'RUB',
    status,
    createdAt: (dto as { createdAt?: string }).createdAt ?? '',
  };
}

export function mapOperationDtoToTransaction(op: AccountOperationDto): Transaction {
  const type = mapOperationType(op.operationType);
  return {
    id: String(op.id),
    accountId: String(op.accountId),
    type,
    amount: Math.abs(op.amount),
    description: op.description ?? '',
    createdAt: op.createdAt ?? '',
  };
}

function mapOperationType(operationType: string): Transaction['type'] {
  const n = String(operationType).toUpperCase();
  if (n.includes('DEPOSIT')) return 'deposit';
  if (n.includes('WITHDRAW')) return 'withdrawal';
  if (n.includes('CREDIT') && n.includes('ISSUE')) return 'credit_issue';
  if (n.includes('CREDIT') && n.includes('PAYMENT')) return 'credit_payment';
  return 'deposit';
}
