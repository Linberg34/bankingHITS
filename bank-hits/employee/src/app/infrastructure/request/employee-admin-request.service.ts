import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { API_BASE_URL } from 'shared/api';
import { TariffsApiService, type TariffDto, type CreateTariffResponse } from 'shared/entities/tariffs';
import { AccountsApiService, type AccountWithOwnerDto, type OperationDto } from 'shared/entities/accounts';
import { AuthApiService } from 'shared/entities/auth';

export interface ClientSummaryDto {
  userId: string;
  name: string;
  email: string;
  status: 'ACTIVE' | 'BLOCKED';
  accountCount: number;
  activeCreditCount: number;
}

export interface ClientPageResponse {
  content: ClientSummaryDto[];
  page: number;
  size: number;
  totalElements: number;
}

export interface EmployeeCreditSummaryDto {
  creditId: string;
  accountNumber: string;
  tariffName: string;
  amount: number;
  remainingDebt: number;
  interestRate: number;
  status: string;
  nextPaymentAt: string | null;
  issuedAt: string;
}

export interface EmployeeCreditListResponse {
  credits: EmployeeCreditSummaryDto[];
}

export interface UserStatusResponse {
  userId: string;
  status: 'ACTIVE' | 'BLOCKED';
}

@Injectable({ providedIn: 'root' })
export class EmployeeAdminRequestService {
  private readonly http = inject(HttpClient);
  private readonly accountsApi = inject(AccountsApiService);
  private readonly tariffsApi = inject(TariffsApiService);
  private readonly authApi = inject(AuthApiService);
  private readonly baseUrl = inject(API_BASE_URL);

  private get base(): string {
    return (this.baseUrl as string).replace(/\/+$/, '');
  }

  clearAuth(): void {
    this.authApi.clearAuth();
  }

  // ─── Clients ───────────────────────────────────────────────────────────────

  getClients(page = 0, size = 50): Observable<ClientSummaryDto[]> {
    return this.http
      .get<ClientPageResponse>(`${this.base}/clients`, { params: { page, size } })
      .pipe(map((resp) => resp.content));
  }

  blockClient(clientId: string): Observable<UserStatusResponse> {
    return this.http.post<UserStatusResponse>(`${this.base}/clients/${clientId}/block`, null);
  }

  unblockClient(clientId: string): Observable<UserStatusResponse> {
    return this.http.post<UserStatusResponse>(`${this.base}/clients/${clientId}/unblock`, null);
  }

  createClient(name: string, email: string, password: string): Observable<void> {
    return this.http.post<void>(`${this.base}/clients`, { name, email, password });
  }

  createEmployee(name: string, email: string, password: string): Observable<void> {
    return this.http.post<void>(`${this.base}/employees`, { name, email, password });
  }

  // ─── Credits ───────────────────────────────────────────────────────────────

  getClientCredits(clientId: string): Observable<EmployeeCreditSummaryDto[]> {
    return this.http
      .get<EmployeeCreditListResponse>(`${this.base}/clients/${clientId}/credits`)
      .pipe(map((resp) => resp.credits));
  }

  // ─── Accounts ──────────────────────────────────────────────────────────────

  getAllAccounts(): Observable<AccountWithOwnerDto[]> {
    return this.accountsApi.getAllAccounts().pipe(map((resp) => resp.content ?? resp.accounts ?? []));
  }

  getAccountOperations(accountNumber: string): Observable<OperationDto[]> {
    return this.accountsApi.getOperations(accountNumber).pipe(map((resp) => resp.content));
  }

  // ─── Tariffs ───────────────────────────────────────────────────────────────

  getTariffs(): Observable<TariffDto[]> {
    return this.tariffsApi.getTariffs();
  }

  createTariff(name: string, annualRate: number): Observable<CreateTariffResponse> {
    return this.tariffsApi.createTariff({ name, annualRate });
  }
}
