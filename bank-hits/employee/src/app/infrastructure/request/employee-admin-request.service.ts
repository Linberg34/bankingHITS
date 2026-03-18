import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccountsApiService,
  type AccountListResponse,
  type AccountOperationDto,
} from 'shared/entities/accounts';
import { AuthApiService, type AuthTokenResponse } from 'shared/entities/auth';
import { CreditsApiService, type CreditDto } from 'shared/entities/credits';
import {
  TariffsApiService,
  type TariffDto,
} from 'shared/entities/tariffs';
import {
  UsersApiService,
  type UserDto,
  type UserId,
  type UsersQueryType,
} from 'shared/entities/users';

@Injectable({ providedIn: 'root' })
export class EmployeeAdminRequestService {
  private readonly accountsApi = inject(AccountsApiService);
  private readonly authApi = inject(AuthApiService);
  private readonly creditsApi = inject(CreditsApiService);
  private readonly tariffsApi = inject(TariffsApiService);
  private readonly usersApi = inject(UsersApiService);

  login(email: string): Observable<AuthTokenResponse> {
    return this.authApi.login({ email });
  }

  logout(): Observable<string> {
    return this.authApi.logout();
  }

  clearAuth(): void {
    this.authApi.clearAuth();
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

  createUser(name: string, email: string, employee: boolean): Observable<AuthTokenResponse> {
    if (employee) {
      return this.authApi.registerEmployeeWithoutAuth({ name, email });
    }

    return this.authApi.registerWithoutAuth({ name, email });
  }

  getAccountsList(): Observable<AccountListResponse> {
    return this.accountsApi.getAccountsList({ page: 0, size: 200, sort: ['id', 'desc'] });
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

  createTariff(name: string, annualRate: number): Observable<TariffDto> {
    return this.tariffsApi.createTariff({ name, annualRate });
  }
}

