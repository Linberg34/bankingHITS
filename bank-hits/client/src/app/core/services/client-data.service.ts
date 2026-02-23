import { Injectable } from '@angular/core';
import type { Account, Credit, CreditTariff, Transaction } from '../models/client.types';
import {
  accounts,
  credits,
  creditTariffs,
  transactions,
} from '../data/mock-data';

@Injectable({ providedIn: 'root' })
export class ClientDataService {
  private readonly clientId = 'client-1';

  getClientId(): string {
    return this.clientId;
  }

  getAccounts(clientId?: string): Account[] {
    const id = clientId ?? this.clientId;
    return accounts.filter((acc) => acc.clientId === id);
  }

  getActiveAccounts(clientId?: string): Account[] {
    return this.getAccounts(clientId).filter((acc) => acc.status === 'active');
  }

  getCredits(clientId?: string): Credit[] {
    const id = clientId ?? this.clientId;
    return credits.filter((credit) => credit.clientId === id);
  }

  getCreditTariffs(): CreditTariff[] {
    return [...creditTariffs];
  }

  getTransactions(accountId: string): Transaction[] {
    return transactions.filter((tx) => tx.accountId === accountId);
  }

  getAccountById(id: string): Account | undefined {
    return accounts.find((a) => a.id === id);
  }

  getCreditTariffById(id: string): CreditTariff | undefined {
    return creditTariffs.find((t) => t.id === id);
  }
}
