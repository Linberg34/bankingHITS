import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import {
  CLIENT_USERS,
  EMPLOYEE_USERS,
  type UserRecord,
} from '../../../data-domain/users/model/users.model';

type UserRole = 'Клиент' | 'Сотрудник';

interface BlockTarget {
  email: string;
  name: string;
  role: UserRole;
}

@Component({
  selector: 'employee-users-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.scss',
})
export class UsersPageComponent {
  addModalOpen = false;
  blockModalOpen = false;
  clientUsers = [...CLIENT_USERS];
  employeeUsers = [...EMPLOYEE_USERS];
  blockTarget: BlockTarget | null = null;

  newUser = {
    name: '',
    email: '',
    role: 'Клиент' as UserRole,
    status: 'Активен',
  };

  addUser(): void {
    const trimmedName = this.newUser.name.trim();
    const trimmedEmail = this.newUser.email.trim().toLowerCase();
    if (!trimmedName || !trimmedEmail) {
      return;
    }

    const user: UserRecord = {
      name: trimmedName,
      email: trimmedEmail,
      status: this.newUser.status,
      registeredAt: this.formatDate(new Date()),
    };

    if (this.newUser.role === 'Сотрудник') {
      this.employeeUsers = [user, ...this.employeeUsers];
    } else {
      this.clientUsers = [user, ...this.clientUsers];
    }

    this.resetAddUserForm();
    this.addModalOpen = false;
  }

  openBlockUser(user: UserRecord, role: UserRole): void {
    if (this.isBlocked(user)) {
      return;
    }

    this.blockTarget = {
      email: user.email,
      name: user.name,
      role,
    };
    this.blockModalOpen = true;
  }

  confirmBlockUser(): void {
    if (!this.blockTarget) {
      return;
    }

    const nextStatus = 'Заблокирован';

    if (this.blockTarget.role === 'Сотрудник') {
      this.employeeUsers = this.employeeUsers.map((user) =>
        user.email === this.blockTarget?.email ? { ...user, status: nextStatus } : user
      );
    } else {
      this.clientUsers = this.clientUsers.map((user) =>
        user.email === this.blockTarget?.email ? { ...user, status: nextStatus } : user
      );
    }

    this.closeBlockModal();
  }

  closeBlockModal(): void {
    this.blockModalOpen = false;
    this.blockTarget = null;
  }

  isBlocked(user: UserRecord): boolean {
    const normalizedStatus = user.status.toLowerCase();
    return normalizedStatus.includes('блок') || normalizedStatus.includes('block');
  }

  private resetAddUserForm(): void {
    this.newUser = {
      name: '',
      email: '',
      role: 'Клиент',
      status: 'Активен',
    };
  }

  private formatDate(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}
