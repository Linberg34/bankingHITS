import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import { ClientSettingsDto, UpdateSettingsRequest } from './settings-api.models';

@Injectable({
  providedIn: 'root',
})
export class SettingsApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  getSettings(): Observable<ClientSettingsDto> {
    return this.httpClient.get<ClientSettingsDto>(`${this.base}/settings`);
  }

  updateSettings(request: UpdateSettingsRequest): Observable<ClientSettingsDto> {
    return this.httpClient.put<ClientSettingsDto>(`${this.base}/settings`, request);
  }

  private get base(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}
