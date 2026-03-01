import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import { CreditDto, CreditId, UserId } from './credits-api.models';

@Injectable({
  providedIn: 'root',
})
export class CreditsApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  getAllCredits(): Observable<CreditDto[]> {
    return this.httpClient.get<CreditDto[]>(`${this.normalizedBaseUrl}/api/credits`);
  }

  getCreditById(id: CreditId): Observable<CreditDto> {
    return this.httpClient.get<CreditDto>(`${this.normalizedBaseUrl}/api/credits/${id}`);
  }

  getCreditsByClientId(clientId: UserId): Observable<CreditDto[]> {
    return this.httpClient.get<CreditDto[]>(`${this.normalizedBaseUrl}/api/credits/client/${clientId}`);
  }

  private get normalizedBaseUrl(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}
