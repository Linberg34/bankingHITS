import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import {
  CreditListResponse,
  CreditDetailResponse,
  CreditRatingResponse,
  TakeCreditRequest,
  TakeCreditResponse,
  RepayCreditRequest,
  RepayCreditResponse,
} from './credits-api.models';

@Injectable({
  providedIn: 'root',
})
export class CreditsApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  /** Список кредитов текущего клиента */
  getMyCredits(): Observable<CreditListResponse> {
    return this.httpClient.get<CreditListResponse>(`${this.base}/credits`);
  }

  /** Детали кредита */
  getCreditDetail(creditId: string): Observable<CreditDetailResponse> {
    return this.httpClient.get<CreditDetailResponse>(`${this.base}/credits/${creditId}`);
  }

  /** Кредитный рейтинг */
  getCreditRating(): Observable<CreditRatingResponse> {
    return this.httpClient.get<CreditRatingResponse>(`${this.base}/credits/rating`);
  }

  /** Взять кредит */
  takeCredit(request: TakeCreditRequest): Observable<TakeCreditResponse> {
    return this.httpClient.post<TakeCreditResponse>(`${this.base}/credits`, request);
  }

  /** Погасить кредит (частично или полностью) */
  repayCredit(creditId: string, request: RepayCreditRequest): Observable<RepayCreditResponse> {
    return this.httpClient.post<RepayCreditResponse>(
      `${this.base}/credits/${creditId}/repay`,
      request
    );
  }

  private get base(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}
