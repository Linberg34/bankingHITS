import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import {
  AccountListResponse,
  AccountDto,
  OpenAccountRequest,
  OpenAccountResponse,
  CloseAccountResponse,
  OperationPageResponse,
  DepositRequest,
  WithdrawRequest,
  TransferRequest,
  OperationAcceptedResponse,
  AllAccountsPageResponse,
  AccountListQuery,
} from './accounts-api.models';

@Injectable({
  providedIn: 'root',
})
export class AccountsApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  /** Получить список счетов текущего клиента */
  getMyAccounts(): Observable<AccountListResponse> {
    return this.httpClient.get<AccountListResponse>(`${this.base}/accounts`);
  }

  /** Открыть новый счёт */
  openAccount(request: OpenAccountRequest): Observable<OpenAccountResponse> {
    return this.httpClient.post<OpenAccountResponse>(`${this.base}/accounts`, request);
  }

  /** Закрыть счёт */
  closeAccount(accountNumber: string): Observable<CloseAccountResponse> {
    return this.httpClient.delete<CloseAccountResponse>(`${this.base}/accounts/${accountNumber}`);
  }

  /** История операций по счёту (пагинация) */
  getOperations(accountNumber: string, page = 0, size = 50): Observable<OperationPageResponse> {
    return this.httpClient.get<OperationPageResponse>(
      `${this.base}/accounts/${accountNumber}/operations`,
      { params: { page, size } }
    );
  }

  /** Пополнение счёта (через Kafka) */
  deposit(request: DepositRequest): Observable<OperationAcceptedResponse> {
    return this.httpClient.post<OperationAcceptedResponse>(
      `${this.base}/operations/deposit`,
      request
    );
  }

  /** Снятие со счёта (через Kafka) */
  withdraw(request: WithdrawRequest): Observable<OperationAcceptedResponse> {
    return this.httpClient.post<OperationAcceptedResponse>(
      `${this.base}/operations/withdraw`,
      request
    );
  }

  /** Перевод между счетами (через Kafka) */
  transfer(request: TransferRequest): Observable<OperationAcceptedResponse> {
    return this.httpClient.post<OperationAcceptedResponse>(
      `${this.base}/operations/transfer`,
      request
    );
  }

  /** [Employee] Все счета всех клиентов */
  getAllAccounts(query: AccountListQuery = {}): Observable<AllAccountsPageResponse> {
    const { page = 0, size = 20 } = query;
    return this.httpClient.get<AllAccountsPageResponse>(`${this.base}/accounts`, {
      params: { page, size },
    });
  }

  private get base(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}
