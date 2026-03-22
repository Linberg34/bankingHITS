import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, map, switchMap, tap } from 'rxjs';
import { SettingsApiService } from 'shared/entities/settings';
import { ThemeModeService } from 'shared/frontend-core';
import { ClientBankingRequestService } from '../../infrastructure/request/client-banking-request.service';
import type { Account, Credit, CreditRating, CreditTariff, Transaction } from '../../core/models/client.types';

@Injectable({ providedIn: 'root' })
export class ClientDataUseCasesService {
  private readonly request = inject(ClientBankingRequestService);
  private readonly settingsApi = inject(SettingsApiService);
  private readonly themeMode = inject(ThemeModeService);

  private readonly accountsCache$ = new BehaviorSubject<Account[]>([]);
  private readonly creditsCache$ = new BehaviorSubject<Credit[]>([]);
  private readonly tariffsCache$ = new BehaviorSubject<CreditTariff[]>([]);
  private readonly hiddenAccountIds$ = new BehaviorSubject<Set<string>>(new Set());

  get accountsSnapshot(): Account[] {
    return this.accountsCache$.value;
  }

  get hiddenAccountIdsSnapshot(): Set<string> {
    return this.hiddenAccountIds$.value;
  }

  loadAccounts(): Observable<Account[]> {
    return this.request.getMyAccounts().pipe(tap((items) => this.accountsCache$.next(items)));
  }

  getActiveAccounts(): Observable<Account[]> {
    return this.accountsCache$.pipe(map((items) => items.filter((item) => item.status === 'active')));
  }

  getVisibleAccounts(): Observable<Account[]> {
    return this.accountsCache$.pipe(
      map((items) => items.filter(
        (item) => item.status === 'active' && !this.hiddenAccountIds$.value.has(item.uuid ?? item.accountNumber)
      ))
    );
  }

  getAccountById(id: string): Account | undefined {
    return this.accountsSnapshot.find((item) => item.id === id || item.accountNumber === id);
  }

  loadOperations(accountNumber: string): Observable<Transaction[]> {
    return this.request.getOperations(accountNumber);
  }

  loadTariffs(): Observable<CreditTariff[]> {
    return this.request.getCreditTariffs().pipe(tap((items) => this.tariffsCache$.next(items)));
  }

  getCreditTariffs(): Observable<CreditTariff[]> {
    return this.tariffsCache$.asObservable();
  }

  getCreditTariffById(id: string): CreditTariff | undefined {
    return this.tariffsCache$.value.find((item) => item.id === id);
  }

  loadCredits(): Observable<Credit[]> {
    return this.request.getMyCredits().pipe(tap((items) => this.creditsCache$.next(items)));
  }

  getCredits(): Observable<Credit[]> {
    return this.creditsCache$.asObservable();
  }

  getCreditRating(): Observable<CreditRating> {
    return this.request.getCreditRating();
  }

  openAccount(currency: 'RUB' | 'USD' | 'EUR' = 'RUB'): Observable<Account> {
    return this.request.openAccount(currency).pipe(
      tap((account) => this.accountsCache$.next([...this.accountsCache$.value, account]))
    );
  }

  deposit(accountNumber: string, amount: number): Observable<void> {
    return this.request.deposit(accountNumber, amount).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => void 0)
    );
  }

  withdraw(accountNumber: string, amount: number): Observable<void> {
    return this.request.withdraw(accountNumber, amount).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => void 0)
    );
  }

  transfer(fromAccountNumber: string, toAccountNumber: string, amount: number): Observable<void> {
    return this.request.transfer(fromAccountNumber, toAccountNumber, amount).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => void 0)
    );
  }

  closeAccount(accountNumber: string): Observable<void> {
    return this.request.closeAccount(accountNumber).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => void 0)
    );
  }

  takeCredit(accountNumber: string, tariffId: string, amount: number): Observable<Credit> {
    return this.request.takeCredit(accountNumber, tariffId, amount);
  }

  repayCreditFull(creditId: string, fullAmount: number): Observable<void> {
    return this.request.repayCreditFull(creditId, fullAmount).pipe(
      switchMap(() => this.loadCredits()),
      map(() => void 0)
    );
  }

  repayCreditPartial(creditId: string, amount: number): Observable<void> {
    return this.request.repayCreditPartial(creditId, amount).pipe(
      switchMap(() => this.loadCredits()),
      map(() => void 0)
    );
  }

  // ─── Hidden accounts ─────────────────────────────────────────────────────────

  getHiddenAccountIds(): Observable<Set<string>> {
    return this.hiddenAccountIds$.asObservable();
  }

  loadHiddenAccounts(): Observable<void> {
    return this.settingsApi.getSettings().pipe(
      tap((settings) => {
        this.hiddenAccountIds$.next(new Set(settings.hiddenAccountIds ?? []));
      }),
      map(() => void 0)
    );
  }

  isAccountHidden(account: Account): boolean {
    return this.hiddenAccountIds$.value.has(account.uuid ?? account.accountNumber);
  }

  toggleAccountHidden(account: Account): Observable<void> {
    const accountKey = account.uuid ?? account.accountNumber;
    const current = new Set(this.hiddenAccountIds$.value);
    if (current.has(accountKey)) {
      current.delete(accountKey);
    } else {
      current.add(accountKey);
    }
    this.hiddenAccountIds$.next(current);
    return this.settingsApi.updateSettings({
      theme: this.themeMode.mode === 'dark' ? 'DARK' : 'LIGHT',
      hiddenAccountIds: Array.from(current),
    }).pipe(map(() => void 0));
  }
}
