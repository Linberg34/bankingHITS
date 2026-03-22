import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import { CreateUserRequest, CurrentUserDto, UserDto, UserId, UsersQueryType } from './users-api.models';

@Injectable({
  providedIn: 'root',
})
export class UsersApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  getUsers(queryType?: UsersQueryType): Observable<UserDto[]> {
    return this.httpClient.get<UserDto[]>(`${this.base}/users`, {
      params: queryType ? { queryType } : {},
    });
  }

  getCurrentUser(): Observable<CurrentUserDto> {
    return this.httpClient.get<CurrentUserDto>(`${this.base}/users/me`);
  }

  banUser(userId: UserId): Observable<UserDto> {
    return this.httpClient.post<UserDto>(`${this.base}/users/${userId}/ban`, null);
  }

  unbanUser(userId: UserId): Observable<UserDto> {
    return this.httpClient.post<UserDto>(`${this.base}/users/${userId}/unban`, null);
  }

  createUser(request: CreateUserRequest): Observable<UserDto> {
    return this.httpClient.post<UserDto>(`${this.base}/users`, request);
  }

  private get base(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }
}
