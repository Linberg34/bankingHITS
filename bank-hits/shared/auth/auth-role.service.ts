import { Injectable } from '@angular/core';
import { UserRole } from './user-role';

@Injectable({ providedIn: 'root' })
export class AuthRoleService {
  private readonly storageKey = 'hitsbank_user_role';

  getRole(): UserRole | null {
    const role = localStorage.getItem(this.storageKey);
    if (role === 'client' || role === 'employee') {
      return role;
    }
    return null;
  }

  setRole(role: UserRole): void {
    localStorage.setItem(this.storageKey, role);
  }

  clearRole(): void {
    localStorage.removeItem(this.storageKey);
  }
}
