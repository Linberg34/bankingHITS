import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { type CreditSummaryDto } from 'shared/entities/credits';
import { EmployeeAdminRequestService } from '../../../../app/infrastructure/request/employee-admin-request.service';

export interface CreditRecord {
  id: string;
  clientId: string;
  account: string;
  tariff: string;
  amount: string;
  remaining: string;
  rate: string;
  status: string;
  nextPayment: string;
}

@Injectable({
  providedIn: 'root',
})
export class CreditsPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  loadCredits(): Observable<CreditRecord[]> {
    return this.requestService.getCredits().pipe(
      map((credits) => credits.map((credit) => this.mapCredit(credit)))
    );
  }

  private mapCredit(credit: CreditSummaryDto): CreditRecord {
    return {
      id: credit.creditId,
      clientId: '-',
      account: credit.accountNumber,
      tariff: credit.tariffName,
      amount: this.formatAmount(credit.amount),
      remaining: this.formatAmount(credit.remainingDebt),
      rate: `${credit.interestRate}%`,
      status: this.mapStatus(credit.status),
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
