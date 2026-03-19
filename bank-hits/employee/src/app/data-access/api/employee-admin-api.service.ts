import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  type AccountListQuery,
  type AccountListResponse,
  type AccountOperationDto,
} from 'shared/entities/accounts';
import { type AuthTokenResponse } from 'shared/entities/auth';
import { type CreditDto } from 'shared/entities/credits';
import { type TariffDto } from 'shared/entities/tariffs';
import { type UserDto, type UserId, type UsersQueryType } from 'shared/entities/users';
import { EmployeeNetworkService } from '../network/employee-network.service';

@Injectable({ providedIn: 'root' })
export class EmployeeAdminApiService {
  private readonly network = inject(EmployeeNetworkService);

  login(email: string): Observable<AuthTokenResponse> {
    return this.network.login({ email });
  }

  logout(): Observable<string> {
    return this.network.logout();
  }

  clearAuth(): void {
    this.network.clearAuth();
  }

  getUsers(queryType: UsersQueryType): Observable<UserDto[]> {
    return this.network.getUsers(queryType);
  }

  banUser(userId: UserId): Observable<UserDto> {
    return this.network.banUser(userId);
  }

  unbanUser(userId: UserId): Observable<UserDto> {
    return this.network.unbanUser(userId);
  }

  createUser(name: string, email: string, employee: boolean): Observable<AuthTokenResponse> {
    if (employee) {
      return this.network.registerEmployee({ name, email });
    }

    return this.network.registerUser({ name, email });
  }

  getAccountsList(query: AccountListQuery = {}): Observable<AccountListResponse> {
    return this.network.getAccountsList(query);
  }

  getAccountOperations(accountNumber: string): Observable<AccountOperationDto[]> {
    return this.network.getAccountOperations(accountNumber);
  }

  getCredits(): Observable<CreditDto[]> {
    return this.network.getCredits();
  }

  getTariffs(): Observable<TariffDto[]> {
    return this.network.getTariffs();
  }

  createTariff(name: string, annualRate: number): Observable<TariffDto> {
    return this.network.createTariff({ name, annualRate });
  }
}
