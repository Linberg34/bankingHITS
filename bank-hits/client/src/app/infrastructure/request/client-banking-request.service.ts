import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AccountsApiService, type AccountDto, type AccountOperationDto } from 'shared/entities/accounts';
import { CreditsApiService, type CreditDto } from 'shared/entities/credits';
import { TariffsApiService, type TariffDto } from 'shared/entities/tariffs';
import type { Account, Credit, CreditTariff, Transaction } from '../../core/models/client.types';

@Injectable({ providedIn: 'root' })
export class ClientBankingRequestService {
  private readonly accountsApi = inject(AccountsApiService);
  private readonly tariffsApi = inject(TariffsApiService);
  private readonly creditsApi = inject(CreditsApiService);

  getMyAccounts(): Observable<Account[]> {
    return this.accountsApi.getMyAccounts().pipe(map((items) => items.map(mapAccountDtoToAccount)));
  }

  getOperations(accountNumber: string): Observable<Transaction[]> {
    return this.accountsApi
      .getOperations(accountNumber)
      .pipe(map((operations) => operations.map(mapOperationDtoToTransaction)));
  }

  openCurrentAccount(): Observable<Account> {
    return this.accountsApi.getCurrentAccount().pipe(map(mapAccountDtoToAccount));
  }

  deposit(accountId: number, amount: number, description?: string): Observable<void> {
    return this.accountsApi.deposit(accountId, amount, description).pipe(map(() => void 0));
  }

  withdraw(accountId: number, amount: number, description?: string): Observable<void> {
    return this.accountsApi.withdraw(accountId, amount, description).pipe(map(() => void 0));
  }

  deleteAccount(accountNumber: string): Observable<void> {
    return this.accountsApi.deleteAccount(accountNumber).pipe(map(() => void 0));
  }

  getCreditTariffs(): Observable<CreditTariff[]> {
    return this.tariffsApi.getTariffs().pipe(map((items) => items.map(mapTariffDtoToTariff)));
  }

  getCreditsByClient(clientId: number | string): Observable<Credit[]> {
    return this.creditsApi.getCreditsByClientId(clientId).pipe(map((items) => items.map(mapCreditDtoToCredit)));
  }

  takeCredit(accountNumber: string, tariffId: number, amount: number): Observable<Credit> {
    return this.creditsApi.takeCredit({ accountNumber, tariffId, amount }).pipe(map(mapCreditDtoToCredit));
  }

  repayCreditFull(creditId: number): Observable<Credit> {
    return this.creditsApi.repayFull(creditId).pipe(map(mapCreditDtoToCredit));
  }

  repayCreditPartial(creditId: number, amount: number): Observable<Credit> {
    return this.creditsApi.repayPartial(creditId, { amount }).pipe(map(mapCreditDtoToCredit));
  }
}

function mapAccountDtoToAccount(dto: AccountDto): Account {
  const id = dto.id != null ? String(dto.id) : dto.accountNumber;
  const status = String(dto.status).toLowerCase() === 'active' ? 'active' : 'closed';

  return {
    id,
    clientId: String(dto.clientId),
    accountNumber: dto.accountNumber,
    balance: Number(dto.balance),
    currency: 'RUB',
    status,
    createdAt: dto.createdAt ?? '',
  };
}

function mapOperationDtoToTransaction(operation: AccountOperationDto): Transaction {
  const normalizedType = String(operation.operationType).toUpperCase();

  return {
    id: String(operation.id),
    accountId: String(operation.accountId),
    type: mapOperationType(normalizedType),
    amount: Math.abs(operation.amount),
    description: operation.description ?? '',
    createdAt: operation.createdAt ?? '',
  };
}

function mapOperationType(value: string): Transaction['type'] {
  if (value.includes('DEPOSIT')) {
    return 'deposit';
  }

  if (value.includes('WITHDRAW')) {
    return 'withdrawal';
  }

  if (value.includes('CREDIT') && value.includes('ISSUE')) {
    return 'credit_issue';
  }

  if (value.includes('CREDIT') && value.includes('PAYMENT')) {
    return 'credit_payment';
  }

  return 'deposit';
}

function mapTariffDtoToTariff(dto: TariffDto): CreditTariff {
  return {
    id: String(dto.id),
    name: dto.name,
    interestRate: dto.annualRate,
    createdAt: dto.createdAt,
    createdBy: '',
  };
}

function mapCreditDtoToCredit(dto: CreditDto): Credit {
  const status = mapCreditStatus(dto.status);
  const accountReference = dto.accountNumber ?? (dto.accountId != null ? String(dto.accountId) : '');

  return {
    id: String(dto.id),
    clientId: String(dto.clientId),
    accountId: accountReference,
    tariffId: dto.tariffName,
    amount: dto.principalAmount,
    remainingAmount: dto.remainingDebt,
    interestRate: dto.annualRate,
    status,
    issueDate: dto.issuedAt,
    nextPaymentDate: '',
    dailyPayment: 0,
  };
}

function mapCreditStatus(status: string): 'active' | 'paid' | 'overdue' {
  const normalized = String(status).toLowerCase();

  if (
    normalized.includes('paid') ||
    normalized.includes('погаш') ||
    normalized.includes('closed') ||
    normalized.includes('закрыт')
  ) {
    return 'paid';
  }

  if (normalized.includes('overdue') || normalized.includes('проср')) {
    return 'overdue';
  }

  return 'active';
}

