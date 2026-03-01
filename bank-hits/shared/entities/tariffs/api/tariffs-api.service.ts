import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import { CreateTariffRequest, TariffDto } from './tariffs-api.models';

@Injectable({
  providedIn: 'root',
})
export class TariffsApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  getTariffs(): Observable<TariffDto[]> {
    return this.httpClient.get<TariffDto[]>(`${this.normalizedBaseUrl}/api/tariffs`);
  }

  createTariff(payload: CreateTariffRequest): Observable<TariffDto> {
    return this.httpClient.post<TariffDto>(`${this.normalizedBaseUrl}/api/tariffs`, payload);
  }

  private get normalizedBaseUrl(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}

