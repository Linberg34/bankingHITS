import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { type AccountWithOwnerDto, type OperationDto } from 'shared/entities/accounts';
import { EmployeeAdminRequestService } from '../../../../app/infrastructure/request/employee-admin-request.service';

export interface AccountPageRecord {
  accountId?: string;
  client: string;
  accountNumber: string;
  balance: string;
  balanceValue: number;
  status: string;
}

export interface AccountOperationRecord {
  id: string;
  date: string;
  type: string;
  amount: string;
  description: string;
}

@Injectable({
  providedIn: 'root',
})
export class AccountsPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  loadAccounts(): Observable<AccountPageRecord[]> {
    return this.requestService.getAllAccounts().pipe(
      map((accounts) => accounts.map((account) => this.mapAccount(account)))
    );
  }

  loadOperations(accountNumber: string): Observable<AccountOperationRecord[]> {
    return this.requestService
      .getAccountOperations(accountNumber)
      .pipe(map((operations) => operations.map((operation) => this.mapOperation(operation))));
  }

  private mapAccount(account: AccountWithOwnerDto): AccountPageRecord {
    return {
      accountId: account.accountId,
      client: account.ownerFullName ?? account.clientName ?? `ID ${account.ownerId ?? account.clientId}`,
      accountNumber: account.accountNumber,
      balance: this.formatAmount(account.balance),
      balanceValue: account.balance,
      status: account.status === 'ACTIVE' ? 'Активен' : 'Закрыт',
    };
  }

  private mapOperation(op: OperationDto): AccountOperationRecord {
    return {
      id: op.operationId,
      date: this.formatDateTime(op.createdAt),
      type: this.mapOperationType(op.type),
      amount: this.formatOperationAmount(op.amount, op.type),
      description: op.description ?? '-',
    };
  }

  private mapOperationType(type: string): string {
    const map: Record<string, string> = {
      DEPOSIT: 'Пополнение',
      WITHDRAW: 'Снятие',
      TRANSFER_IN: 'Перевод (приход)',
      TRANSFER_OUT: 'Перевод (расход)',
      CREDIT_ISSUE: 'Выдача кредита',
      CREDIT_PAYMENT: 'Погашение кредита',
    };
    return map[type] ?? type;
  }

  private formatAmount(value: number): string {
    return new Intl.NumberFormat('ru-RU', {
      style: 'currency',
      currency: 'RUB',
      maximumFractionDigits: 2,
    }).format(value);
  }

  private formatOperationAmount(amount: number, type: string): string {
    const base = this.formatAmount(Math.abs(amount));
    if (type === 'WITHDRAW' || type === 'TRANSFER_OUT' || type === 'CREDIT_PAYMENT') {
      return `-${base}`;
    }
    return `+${base}`;
  }

  private formatDateTime(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${day}.${month}.${year} ${hours}:${minutes}`;
  }
}
