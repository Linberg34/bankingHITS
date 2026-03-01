import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import { CurrentUserDto, UserDto, UserId, UsersQueryType } from './users-api.models';

@Injectable({
  providedIn: 'root',
})
export class UsersApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  getUsers(queryType?: UsersQueryType): Observable<UserDto[]> {
    return this.httpClient.get<UserDto[]>(`${this.normalizedBaseUrl}/api/users`, {
      params: queryType ? { queryType } : {},
    });
  }

  getCurrentUser(): Observable<CurrentUserDto> {
    return this.httpClient.get<CurrentUserDto>(`${this.normalizedBaseUrl}/api/users/me`);
  }

  getBannedUsers(): Observable<UserDto[]> {
    return this.httpClient.get<UserDto[]>(`${this.normalizedBaseUrl}/api/users/banned`);
  }

  banUser(userId: UserId): Observable<UserDto> {
    return this.httpClient.post<UserDto>(`${this.normalizedBaseUrl}/api/users/${userId}/ban`, null);
  }

  unbanUser(userId: UserId): Observable<UserDto> {
    return this.httpClient.post<UserDto>(`${this.normalizedBaseUrl}/api/users/${userId}/unban`, null);
  }

  private get normalizedBaseUrl(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}
