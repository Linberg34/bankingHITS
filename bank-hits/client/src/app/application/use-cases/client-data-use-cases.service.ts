import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, map, switchMap, tap } from 'rxjs';
import { ClientBankingRequestService } from '../../infrastructure/request/client-banking-request.service';
import type { Account, Credit, CreditTariff, Transaction } from '../../core/models/client.types';

@Injectable({ providedIn: 'root' })
export class ClientDataUseCasesService {
  private readonly request = inject(ClientBankingRequestService);

  private readonly accountsCache$ = new BehaviorSubject<Account[]>([]);
  private readonly creditsCache$ = new BehaviorSubject<Credit[]>([]);
  private readonly tariffsCache$ = new BehaviorSubject<CreditTariff[]>([]);

  get accountsSnapshot(): Account[] {
    return this.accountsCache$.value;
  }

  loadAccounts(): Observable<Account[]> {
    return this.request.getMyAccounts().pipe(tap((items) => this.accountsCache$.next(items)));
  }

  getActiveAccounts(): Observable<Account[]> {
    return this.accountsCache$.pipe(map((items) => items.filter((item) => item.status === 'active')));
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

  loadCredits(clientId: number): Observable<Credit[]> {
    return this.request.getCreditsByClient(clientId).pipe(tap((items) => this.creditsCache$.next(items)));
  }

  getCredits(): Observable<Credit[]> {
    return this.creditsCache$.asObservable();
  }

  openCurrentAccount(): Observable<Account> {
    return this.request.openCurrentAccount().pipe(
      tap((account) => this.accountsCache$.next([...this.accountsCache$.value, account]))
    );
  }

  deposit(accountId: string, amount: number, description?: string): Observable<void> {
    const parsedId = Number(accountId);
    return this.request.deposit(parsedId, amount, description).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => void 0)
    );
  }

  withdraw(accountId: string, amount: number, description?: string): Observable<void> {
    const parsedId = Number(accountId);
    return this.request.withdraw(parsedId, amount, description).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => void 0)
    );
  }

  deleteAccount(accountNumber: string): Observable<void> {
    return this.request.deleteAccount(accountNumber).pipe(
      switchMap(() => this.loadAccounts()),
      map(() => void 0)
    );
  }

  takeCredit(accountNumber: string, tariffId: number, amount: number): Observable<Credit> {
    return this.request.takeCredit(accountNumber, tariffId, amount);
  }

  repayCreditFull(creditId: number): Observable<Credit> {
    return this.request.repayCreditFull(creditId);
  }

  repayCreditPartial(creditId: number, amount: number): Observable<Credit> {
    return this.request.repayCreditPartial(creditId, amount);
  }
}

