import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../../shared/api';
import type { TariffDto } from './tariffs-api.models';

@Injectable({ providedIn: 'root' })
export class TariffsApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  getTariffs(): Observable<TariffDto[]> {
    const url = `${this.normalizedBaseUrl}/api/tariffs`;
    return this.http.get<TariffDto[]>(url);
  }

  private get normalizedBaseUrl(): string {
    return String(this.baseUrl).replace(/\/+$/, '');
  }
}
