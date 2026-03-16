import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { AuthApiService } from 'shared/entities/auth';
import { UserDto, UserId, UsersApiService } from 'shared/entities/users';

export type UsersPageRole = 'Клиент' | 'Сотрудник';

export interface UsersPageRecord {
  id: string;
  name: string;
  email: string;
  status: string;
  registeredAt: string;
}

export interface UsersPageData {
  clientUsers: UsersPageRecord[];
  employeeUsers: UsersPageRecord[];
}

@Injectable({
  providedIn: 'root',
})
export class UsersPageService {
  constructor(
    private readonly usersApiService: UsersApiService,
    private readonly authApiService: AuthApiService
  ) {}

  getClientUsers(): Observable<UsersPageRecord[]> {
    return this.usersApiService.getUsers('CLIENTS').pipe(map((users) => users.map((user) => this.mapUser(user))));
  }

  getEmployeeUsers(): Observable<UsersPageRecord[]> {
    return this.usersApiService
      .getUsers('EMPLOYEES')
      .pipe(map((users) => users.map((user) => this.mapUser(user))));
  }

  banUser(userId: UserId): Observable<UsersPageRecord> {
    return this.usersApiService.banUser(userId).pipe(map((user) => this.mapUser(user)));
  }

  unbanUser(userId: UserId): Observable<UsersPageRecord> {
    return this.usersApiService.unbanUser(userId).pipe(map((user) => this.mapUser(user)));
  }

  createUser(name: string, email: string, role: UsersPageRole): Observable<void> {
    const payload = { name, email };
    const request$ =
      role === 'Сотрудник'
        ? this.authApiService.registerEmployeeWithoutAuth(payload)
        : this.authApiService.registerWithoutAuth(payload);

    return request$.pipe(map(() => void 0));
  }

  private mapUser(user: UserDto): UsersPageRecord {
    return {
      id: String(user.id),
      name: user.name,
      email: user.email,
      status: this.mapStatus(user.status),
      registeredAt: this.formatDate(user.registeredAt),
    };
  }

  private mapStatus(status: string): string {
    const normalizedStatus = String(status).toUpperCase();
    if (normalizedStatus === 'BANNED' || normalizedStatus === 'BLOCKED') {
      return 'Заблокирован';
    }
    return 'Активен';
  }

  private formatDate(dateValue: string): string {
    const localDatePattern = /^(\d{4})-(\d{2})-(\d{2})$/;
    const match = localDatePattern.exec(dateValue);
    if (!match) {
      return String(dateValue);
    }

    const [, year, month, day] = match;
    return `${day}.${month}.${year}`;
  }
}
