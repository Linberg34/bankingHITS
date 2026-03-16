import { Injectable, inject } from '@angular/core';
import { Observable, map, tap, BehaviorSubject, of, switchMap } from 'rxjs';
import type { Account, Credit, CreditTariff, Transaction } from '../models/client.types';
import { AccountsApiService } from 'shared/entities/accounts';
import { AuthApiService } from './auth-api.service';
import { TariffsApiService } from '../api/tariffs-api.service';
import { CreditsApiService } from '../api/credits-api.service';
import type { TariffDto } from '../api/tariffs-api.models';
import type { CreditDto } from '../api/credits-api.models';
import { mapAccountDtoToAccount, mapOperationDtoToTransaction } from '../api/accounts-api.mappers';

@Injectable({ providedIn: 'root' })
export class ClientDataService {
  private readonly accountsApi = inject(AccountsApiService);
  private readonly authApi = inject(AuthApiService);
  private readonly tariffsApi = inject(TariffsApiService);
  private readonly creditsApi = inject(CreditsApiService);

  private readonly accountsCache$ = new BehaviorSubject<Account[]>([]);
  private readonly creditsCache$ = new BehaviorSubject<Credit[]>([]);
  private readonly tariffsCache$ = new BehaviorSubject<CreditTariff[]>([]);

  get accountsSnapshot(): Account[] {
    return this.accountsCache$.value;
  }

  loadAccounts(): Observable<Account[]> {
    return this.accountsApi.getMyAccounts().pipe(
      map((dtos) => dtos.map((dto) => mapAccountDtoToAccount(dto as { id?: number } & typeof dto))),
      tap((accounts) => this.accountsCache$.next(accounts))
    );
  }

  getActiveAccounts(): Observable<Account[]> {
    return this.accountsCache$.pipe(
      map((accounts) => accounts.filter((a) => a.status === 'active'))
    );
  }

  getAccountById(id: string): Account | undefined {
    return this.accountsSnapshot.find((a) => a.id === id || a.accountNumber === id);
  }

  loadOperations(accountNumber: string): Observable<Transaction[]> {
    return this.accountsApi
      .getOperations(accountNumber)
      .pipe(map((ops) => ops.map(mapOperationDtoToTransaction)));
  }

  getTransactions(accountId: string): Observable<Transaction[]> {
    const account = this.getAccountById(accountId);
    if (!account) return of([]);
    return this.loadOperations(account.accountNumber);
  }

  loadTariffs(): Observable<CreditTariff[]> {
    return this.tariffsApi.getTariffs().pipe(
      map((dtos) => dtos.map(mapTariffDtoToTariff)),
      tap((tariffs) => this.tariffsCache$.next(tariffs))
    );
  }

  getCreditTariffs(): Observable<CreditTariff[]> {
    return this.tariffsCache$.asObservable();
  }

  getCreditTariffById(id: string): CreditTariff | undefined {
    return this.tariffsCache$.value.find((t) => t.id === id);
  }

  loadCredits(clientId: number): Observable<Credit[]> {
    return this.creditsApi.getCreditsByClient(clientId).pipe(
      map((dtos) => dtos.map(mapCreditDtoToCredit)),
      tap((credits) => this.creditsCache$.next(credits))
    );
  }

  getCredits(): Observable<Credit[]> {
    return this.creditsCache$.asObservable();
  }

  createAccount(clientId: string, accountNumber: string): Observable<Account> {
    return this.accountsApi
      .createAccount({
        clientId,
        accountNumber,
        balance: 0,
        status: 'ACTIVE',
      })
      .pipe(
        map((dto) => mapAccountDtoToAccount(dto as { id?: number } & typeof dto)),
        tap((account) => this.accountsCache$.next([...this.accountsCache$.value, account]))
      );
  }

  openCurrentAccount(): Observable<Account> {
    return this.accountsApi.getCurrentAccount().pipe(
      map((dto) => mapAccountDtoToAccount(dto as { id?: number } & typeof dto)),
      tap((account) => this.accountsCache$.next([...this.accountsCache$.value, account]))
    );
  }

  deposit(accountId: string, amount: number, description?: string): Observable<void> {
    const id = accountId as unknown as number;
    return this.accountsApi.deposit(id, amount, description).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => undefined as void)
    );
  }

  withdraw(accountId: string, amount: number, description?: string): Observable<void> {
    const id = accountId as unknown as number;
    return this.accountsApi.withdraw(id, amount, description).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => undefined as void)
    );
  }

  deleteAccount(accountId: string): Observable<void> {
    return this.accountsApi.deleteAccount(accountId).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => undefined as void)
    );
  }

  takeCredit(accountNumber: string, tariffId: number, amount: number): Observable<Credit> {
    return this.creditsApi.takeCredit(accountNumber, tariffId, amount).pipe(map(mapCreditDtoToCredit));
  }

  repayCreditFull(creditId: number): Observable<Credit> {
    return this.creditsApi.repayFull(creditId).pipe(map(mapCreditDtoToCredit));
  }

  repayCreditPartial(creditId: number, amount: number): Observable<Credit> {
    return this.creditsApi.repayPartial(creditId, amount).pipe(map(mapCreditDtoToCredit));
  }
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
  return {
    id: String(dto.id),
    clientId: String(dto.clientId),
    accountId: String(dto.accountNumber),
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
  const s = String(status).toLowerCase();
  if (s.includes('paid') || s.includes('погаш') || s.includes('closed') || s.includes('закрыт')) return 'paid';
  if (s.includes('overdue') || s.includes('проср')) return 'overdue';
  return 'active';
}
