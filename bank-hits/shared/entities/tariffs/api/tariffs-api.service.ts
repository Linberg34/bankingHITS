import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import { CreateTariffRequest, CreateTariffResponse, TariffDto } from './tariffs-api.models';

@Injectable({
  providedIn: 'root',
})
export class TariffsApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  getTariffs(): Observable<TariffDto[]> {
    return this.httpClient.get<TariffDto[]>(`${this.base}/tariffs`);
  }

  createTariff(payload: CreateTariffRequest): Observable<CreateTariffResponse> {
    return this.httpClient.post<CreateTariffResponse>(`${this.base}/tariffs`, payload);
  }

  private get base(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}
