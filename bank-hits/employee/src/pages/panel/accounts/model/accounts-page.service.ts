import { Injectable } from '@angular/core';
import { forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { AccountsApiService, AccountOperationDto, type AccountDto } from 'shared/entities/accounts';
import { UsersApiService, type UserDto } from 'shared/entities/users';

export interface AccountPageRecord {
  client: string;
  accountNumber: string;
  balance: string;
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
  constructor(
    private readonly accountsApiService: AccountsApiService,
    private readonly usersApiService: UsersApiService
  ) {}

  loadAccounts(): Observable<AccountPageRecord[]> {
    return this.usersApiService.getUsers('CLIENTS').pipe(
      switchMap((clients) => {
        if (!clients.length) {
          return of([] as AccountPageRecord[]);
        }

        return forkJoin(
          clients.map((client) =>
            this.accountsApiService
              .getAccountsByUserId(client.id)
              .pipe(map((accounts) => this.mapAccounts(client, accounts)))
          )
        ).pipe(map((groups) => groups.flat()));
      })
    );
  }

  loadOperations(accountNumber: string): Observable<AccountOperationRecord[]> {
    return this.accountsApiService
      .getOperations(accountNumber)
      .pipe(map((operations) => operations.map((operation) => this.mapOperation(operation))));
  }

  private mapAccounts(client: UserDto, accounts: AccountDto[]): AccountPageRecord[] {
    return accounts.map((account) => ({
      client: client.name,
      accountNumber: account.accountNumber,
      balance: this.formatAmount(account.balance),
      status: this.mapStatus(account.status),
    }));
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

