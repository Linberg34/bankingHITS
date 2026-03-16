import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import {
  AccountDto,
  AccountId,
  AccountListQuery,
  AccountListResponse,
  AccountOperationDto,
  AccountOperationResultDto,
  CreateAccountRequest,
  UserId,
} from './accounts-api.models';

@Injectable({
  providedIn: 'root',
})
export class AccountsApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  createAccount(payload: CreateAccountRequest): Observable<AccountDto> {
    return this.httpClient.post<AccountDto>(`${this.normalizedBaseUrl}/api/account`, payload);
  }

  withdraw(accountId: AccountId, amount: number, description?: string): Observable<AccountOperationResultDto> {
    return this.httpClient.post<AccountOperationResultDto>(
      `${this.normalizedBaseUrl}/api/account/${accountId}/withdraw`,
      null,
      { params: this.operationParams(amount, description) }
    );
  }

  deposit(accountId: AccountId, amount: number, description?: string): Observable<AccountOperationResultDto> {
    return this.httpClient.post<AccountOperationResultDto>(
      `${this.normalizedBaseUrl}/api/account/${accountId}/deposit`,
      null,
      { params: this.operationParams(amount, description) }
    );
  }

  getCurrentAccount(): Observable<AccountDto> {
    return this.httpClient.post<AccountDto>(`${this.normalizedBaseUrl}/api/account/current`, null);
  }

  getOperations(accountNumber: string): Observable<AccountOperationDto[]> {
    return this.httpClient.get<AccountOperationDto[]>(
      `${this.normalizedBaseUrl}/api/account/${accountNumber}/operations`
    );
  }

  getOperationsPage(accountNumber: string, page = 0, size = 20): Observable<AccountOperationDto[]> {
    return this.httpClient.get<AccountOperationDto[]>(
      `${this.normalizedBaseUrl}/api/account/${accountNumber}/operations/page`,
      { params: { page, size } }
    );
  }

  getAccountsByUserId(userId: UserId): Observable<AccountDto[]> {
    return this.httpClient.get<AccountDto[]>(`${this.normalizedBaseUrl}/api/account/user/${userId}`);
  }

  getAccountByNumber(accountNumber: string): Observable<AccountDto> {
    return this.httpClient.get<AccountDto>(`${this.normalizedBaseUrl}/api/account/number/${accountNumber}`);
  }

  getMyAccounts(): Observable<AccountDto[]> {
    return this.httpClient.get<AccountDto[]>(`${this.normalizedBaseUrl}/api/account/my`);
  }

  getAccountsList(query: AccountListQuery = {}): Observable<AccountListResponse> {
    const {
      filterUserId,
      status,
      minBalance,
      maxBalance,
      page = 0,
      size = 20,
      sort = ['id', 'desc'],
    } = query;

    return this.httpClient.get<AccountListResponse>(`${this.normalizedBaseUrl}/api/account/list`, {
      params: {
        ...(filterUserId !== undefined ? { filterUserId } : {}),
        ...(status ? { status } : {}),
        ...(minBalance !== undefined ? { minBalance } : {}),
        ...(maxBalance !== undefined ? { maxBalance } : {}),
        page,
        size,
        sort,
      },
    });
  }

  deleteAccount(accountNumber: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.normalizedBaseUrl}/api/account/${accountNumber}`);
  }

  private get normalizedBaseUrl(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }

  private operationParams(amount: number, description?: string): Record<string, string> {
    const params: Record<string, string> = { amount: String(amount) };
    if (description) {
      params['description'] = description;
    }
    return params;
  }
}
