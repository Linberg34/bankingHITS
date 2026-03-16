import { Injectable } from '@angular/core';
import { catchError, forkJoin, map, Observable, of } from 'rxjs';
import { type CreditDto } from 'shared/entities/credits';
import { type UserDto } from 'shared/entities/users';
import { EmployeeAdminRequestService } from '../../../../app/infrastructure/request/employee-admin-request.service';

export interface CreditRecord {
  id: string;
  clientName: string;
  account: string;
  tariff: string;
  amount: string;
  remaining: string;
  rate: string;
  status: string;
  issuedAt: string;
  termMonths: number;
  paidMonths: number;
  closedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class CreditsPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  loadCredits(): Observable<CreditRecord[]> {
    return forkJoin({
      credits: this.requestService.getCredits(),
      users: this.requestService.getUsers('ALL').pipe(catchError(() => of([] as UserDto[]))),
    }).pipe(map(({ credits, users }) => this.mapCredits(credits, users)));
  }

  private mapCredits(credits: CreditDto[], users: UserDto[]): CreditRecord[] {
    const userById = new Map<string, UserDto>();
    for (const user of users) {
      userById.set(String(user.id), user);
    }

    return credits.map((credit) => {
      const client = userById.get(String(credit.clientId));
      const issuedDate = this.formatDate(credit.issuedAt);
      const closedDate = credit.closedAt ? this.formatDate(credit.closedAt) : '-';
      const accountValue = credit.accountNumber ?? (credit.accountId != null ? String(credit.accountId) : '-');

      return {
        id: String(credit.id),
        clientName: client?.name ?? `ID ${credit.clientId}`,
        account: accountValue,
        tariff: credit.tariffName,
        amount: this.formatAmount(credit.principalAmount),
        remaining: this.formatAmount(credit.remainingDebt),
        rate: `${credit.annualRate}%`,
        status: this.mapStatus(credit.status),
        issuedAt: issuedDate,
        termMonths: 0,
        paidMonths: 0,
        closedAt: closedDate,
      };
    });
  }

  private mapStatus(status: string): string {
    const normalized = String(status).toUpperCase();
    if (normalized === 'ACTIVE') {
      return 'Активен';
    }
    if (normalized === 'CLOSED' || normalized === 'PAID') {
      return 'Погашен';
    }
    if (normalized === 'OVERDUE') {
      return 'Просрочен';
    }

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
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}

