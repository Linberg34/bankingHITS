import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccountsApiService,
  type AccountListQuery,
  type AccountListResponse,
  type AccountOperationDto,
} from 'shared/entities/accounts';
import {
  AuthApiService,
  type AuthLoginRequest,
  type AuthRegisterRequest,
  type AuthTokenResponse,
} from 'shared/entities/auth';
import { CreditsApiService, type CreditDto } from 'shared/entities/credits';
import { TariffsApiService, type CreateTariffRequest, type TariffDto } from 'shared/entities/tariffs';
import {
  UsersApiService,
  type UserDto,
  type UserId,
  type UsersQueryType,
} from 'shared/entities/users';

@Injectable({ providedIn: 'root' })
export class EmployeeNetworkService {
  private readonly accountsApi = inject(AccountsApiService);
  private readonly authApi = inject(AuthApiService);
  private readonly creditsApi = inject(CreditsApiService);
  private readonly tariffsApi = inject(TariffsApiService);
  private readonly usersApi = inject(UsersApiService);

  login(payload: AuthLoginRequest): Observable<AuthTokenResponse> {
    return this.authApi.login(payload);
  }

  logout(): Observable<string> {
    return this.authApi.logout();
  }

  clearAuth(): void {
    this.authApi.clearAuth();
  }

  registerUser(payload: AuthRegisterRequest): Observable<AuthTokenResponse> {
    return this.authApi.registerWithoutAuth(payload);
  }

  registerEmployee(payload: AuthRegisterRequest): Observable<AuthTokenResponse> {
    return this.authApi.registerEmployeeWithoutAuth(payload);
  }

  getUsers(queryType: UsersQueryType): Observable<UserDto[]> {
    return this.usersApi.getUsers(queryType);
  }

  banUser(userId: UserId): Observable<UserDto> {
    return this.usersApi.banUser(userId);
  }

  unbanUser(userId: UserId): Observable<UserDto> {
    return this.usersApi.unbanUser(userId);
  }

  getAccountsList(request: AccountListQuery): Observable<AccountListResponse> {
    return this.accountsApi.getAccountsList(request);
  }

  getAccountOperations(accountNumber: string): Observable<AccountOperationDto[]> {
    return this.accountsApi.getOperations(accountNumber);
  }

  getCredits(): Observable<CreditDto[]> {
    return this.creditsApi.getAllCredits();
  }

  getTariffs(): Observable<TariffDto[]> {
    return this.tariffsApi.getTariffs();
  }

  createTariff(payload: CreateTariffRequest): Observable<TariffDto> {
    return this.tariffsApi.createTariff(payload);
  }
}
