import { Injectable, inject } from '@angular/core';
import { catchError, forkJoin, map, Observable, of } from 'rxjs';
import { type AccountDto, type AccountOperationDto } from 'shared/entities/accounts';
import { type UserDto } from 'shared/entities/users';
import { EmployeeAdminApiService } from '../../data-access/api/employee-admin-api.service';

export interface AccountPageRecord {
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

@Injectable({ providedIn: 'root' })
export class EmployeeAccountsUseCasesService {
  private readonly api = inject(EmployeeAdminApiService);

  loadAccounts(): Observable<AccountPageRecord[]> {
    return forkJoin({
      list: this.api.getAccountsList({ page: 0, size: 200, sort: ['id', 'desc'] }),
      users: this.api.getUsers('ALL').pipe(catchError(() => of([] as UserDto[]))),
    }).pipe(
      map(({ list, users }) => {
        const usersById = new Map<string, UserDto>();
        for (const user of users) {
          usersById.set(String(user.id), user);
        }

        return list.content.map((account) => this.mapAccount(account, usersById.get(String(account.clientId))));
      })
    );
  }

  loadOperations(accountNumber: string): Observable<AccountOperationRecord[]> {
    return this.api
      .getAccountOperations(accountNumber)
      .pipe(map((operations) => operations.map((operation) => this.mapOperation(operation))));
  }

  private mapAccount(account: AccountDto, client?: UserDto): AccountPageRecord {
    return {
      client: client?.name ?? `ID ${account.clientId}`,
      accountNumber: account.accountNumber,
      balance: this.formatAmount(account.balance),
      balanceValue: account.balance,
      status: this.mapStatus(account.status),
    };
  }

  private mapOperation(operation: AccountOperationDto): AccountOperationRecord {
    return {
      id: String(operation.id),
      date: this.formatDateTime(operation.createdAt),
      type: this.mapOperationType(operation.operationType),
      amount: this.formatOperationAmount(operation.amount, operation.operationType),
      description: operation.description || '-',
    };
  }

  private mapStatus(status: string): string {
    const normalized = String(status).toUpperCase();
    if (normalized === 'ACTIVE') {
      return 'Активен';
    }
    if (normalized === 'INACTIVE') {
      return 'Неактивен';
    }
    if (normalized === 'BANNED' || normalized === 'BLOCKED') {
      return 'Заблокирован';
    }

    return status;
  }

  private mapOperationType(operationType: string): string {
    const normalized = String(operationType).toUpperCase();
    if (normalized.includes('DEPOSIT')) {
      return 'Пополнение';
    }
    if (normalized.includes('WITHDRAW')) {
      return 'Снятие';
    }

    return operationType;
  }

  private formatAmount(value: number): string {
    return new Intl.NumberFormat('ru-RU', {
      style: 'currency',
      currency: 'RUB',
      maximumFractionDigits: 2,
    }).format(value);
  }

  private formatOperationAmount(amount: number, operationType: string): string {
    const base = this.formatAmount(Math.abs(amount));
    const normalized = String(operationType).toUpperCase();
    if (normalized.includes('WITHDRAW')) {
      return `-${base}`;
    }
    if (normalized.includes('DEPOSIT')) {
      return `+${base}`;
    }

    return base;
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
