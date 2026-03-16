import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../../shared/api';
import type { CreditDto, RepayPartialRequest } from './credits-api.models';

@Injectable({ providedIn: 'root' })
export class CreditsApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  getCreditsByClient(clientId: number): Observable<CreditDto[]> {
    const url = `${this.normalizedBaseUrl}/api/credits/client/${clientId}`;
    return this.http.get<CreditDto[]>(url);
  }

  takeCredit(accountNumber: string, tariffId: number, amount: number): Observable<CreditDto> {
    const url = `${this.normalizedBaseUrl}/api/credits`;
    return this.http.post<CreditDto>(url, { accountNumber, tariffId, amount });
  }

  repayFull(creditId: number): Observable<CreditDto> {
    const url = `${this.normalizedBaseUrl}/api/credits/${creditId}/repay`;
    return this.http.post<CreditDto>(url, null);
  }

  repayPartial(creditId: number, amount: number): Observable<CreditDto> {
    const url = `${this.normalizedBaseUrl}/api/credits/${creditId}/repay/partial`;
    return this.http.post<CreditDto>(url, { amount } as RepayPartialRequest);
  }

  private get normalizedBaseUrl(): string {
    return String(this.baseUrl).replace(/\/+$/, '');
  }
}
