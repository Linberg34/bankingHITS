import { Injectable } from '@angular/core';
import { Observable, forkJoin, map, of, switchMap } from 'rxjs';
import { EmployeeAdminRequestService, type ClientSummaryDto, type EmployeeCreditSummaryDto } from '../../../../app/infrastructure/request/employee-admin-request.service';

export interface CreditRecord {
  id: string;
  clientId: string;
  clientName: string;
  account: string;
  tariff: string;
  amount: string;
  remaining: string;
  rate: string;
  status: string;
  issuedAt: string;
  nextPayment: string;
}

export interface ClientOption {
  id: string;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class CreditsPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  loadClients(): Observable<ClientOption[]> {
    return this.requestService.getClients().pipe(
      map((clients) => clients.map((c) => ({ id: c.userId, name: c.name })))
    );
  }

  loadCreditsByClient(clientId: string, clientName: string): Observable<CreditRecord[]> {
    return this.requestService.getClientCredits(clientId).pipe(
      map((credits) => credits.map((credit) => this.mapCredit(credit, clientId, clientName)))
    );
  }

  loadAllCredits(clients: ClientOption[]): Observable<CreditRecord[]> {
    if (!clients.length) return of([]);
    return forkJoin(
      clients.map((c) => this.loadCreditsByClient(c.id, c.name))
    ).pipe(map((arrays) => arrays.flat()));
  }

  private mapCredit(credit: EmployeeCreditSummaryDto, clientId: string, clientName: string): CreditRecord {
    return {
      id: credit.creditId,
      clientId,
      clientName,
      account: credit.accountNumber,
      tariff: credit.tariffName,
      amount: this.formatAmount(credit.amount),
      remaining: this.formatAmount(credit.remainingDebt),
      rate: `${credit.interestRate}%`,
      status: this.mapStatus(credit.status),
      issuedAt: credit.issuedAt ? this.formatDate(credit.issuedAt) : '-',
      nextPayment: credit.nextPaymentAt ? this.formatDate(credit.nextPaymentAt) : '-',
    };
  }

  private mapStatus(status: string): string {
    if (status === 'ACTIVE') return 'Активен';
    if (status === 'CLOSED') return 'Погашен';
    if (status === 'OVERDUE') return 'Просрочен';
    return status;
  }

  private formatAmount(value: number): string {
    return new Intl.NumberFormat('ru-RU', {
      style: 'currency',
      currency: 'RUB',
      maximumFractionDigits: 2,
    }).format(value);
  }

  private formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}
