import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import {
  AccountsApiService,
  type AccountWithOwnerDto,
  type OperationDto,
} from 'shared/entities/accounts';
import { AuthApiService } from 'shared/entities/auth';
import { CreditsApiService, type CreditSummaryDto } from 'shared/entities/credits';
import { TariffsApiService, type TariffDto, type CreateTariffResponse } from 'shared/entities/tariffs';
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

  createUser(name: string, username: string, password: string, isEmployee: boolean): Observable<void> {
    return this.usersApi
      .createUser({ name, username, password, role: isEmployee ? 'EMPLOYEE' : 'CLIENT' })
      .pipe(map(() => void 0));
  }

  getAllAccounts(): Observable<AccountWithOwnerDto[]> {
    return this.accountsApi.getAllAccounts().pipe(map((resp) => resp.accounts));
  }

  getAccountOperations(accountNumber: string): Observable<OperationDto[]> {
    return this.accountsApi.getOperations(accountNumber).pipe(map((resp) => resp.content));
  }

  getCredits(): Observable<CreditSummaryDto[]> {
    return this.creditsApi.getMyCredits().pipe(map((resp) => resp.credits));
  }

  getTariffs(): Observable<TariffDto[]> {
    return this.tariffsApi.getTariffs();
  }

  createTariff(name: string, annualRate: number): Observable<CreateTariffResponse> {
    return this.tariffsApi.createTariff({ name, annualRate });
  }
}
