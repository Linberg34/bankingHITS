import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { EmployeeAdminRequestService, type ClientSummaryDto, type CreditRatingDto } from '../../../../app/infrastructure/request/employee-admin-request.service';

export type { CreditRatingDto };

export type UsersPageRole = 'Клиент' | 'Сотрудник' | string;

export interface UsersPageRecord {
  id: string;
  name: string;
  email: string;
  status: string;
  registeredAt: string;
  accountCount: number;
  activeCreditCount: number;
}

@Injectable({
  providedIn: 'root',
})
export class UsersPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  getClientUsers(): Observable<UsersPageRecord[]> {
    return this.requestService.getClients().pipe(
      map((clients) => clients.map((c) => this.mapClient(c)))
    );
  }

  /** Employee BFF does not expose a list of employees — returns empty for now */
  getEmployeeUsers(): Observable<UsersPageRecord[]> {
    return new Observable((observer) => {
      observer.next([]);
      observer.complete();
    });
  }

  banUser(userId: string): Observable<UsersPageRecord> {
    return this.requestService.blockClient(userId).pipe(
      map((resp) => ({
        id: resp.userId,
        name: '',
        email: '',
        status: this.mapStatus(resp.status),
        registeredAt: '',
        accountCount: 0,
        activeCreditCount: 0,
      }))
    );
  }

  unbanUser(userId: string): Observable<UsersPageRecord> {
    return this.requestService.unblockClient(userId).pipe(
      map((resp) => ({
        id: resp.userId,
        name: '',
        email: '',
        status: this.mapStatus(resp.status),
        registeredAt: '',
        accountCount: 0,
        activeCreditCount: 0,
      }))
    );
  }

  getCreditRating(userId: string): Observable<CreditRatingDto> {
    return this.requestService.getCreditRating(userId);
  }

  createUser(name: string, email: string, password: string, role: UsersPageRole): Observable<void> {
    if (role === 'Сотрудник') {
      return this.requestService.createEmployee(name, email, password);
    }
    return this.requestService.createClient(name, email, password);
  }

  private mapClient(client: ClientSummaryDto): UsersPageRecord {
    return {
      id: client.userId,
      name: client.name,
      email: client.email,
      status: this.mapStatus(client.status),
      registeredAt: '',
      accountCount: client.accountCount,
      activeCreditCount: client.activeCreditCount,
    };
  }

  private mapStatus(status: string): string {
    if (status === 'BANNED') return 'Заблокирован';
    return 'Активен';
  }
}
