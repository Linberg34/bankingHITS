import { HttpErrorResponse } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { NotificationService } from '../../../../../shared/frontend-core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { UsersPageRecord, UsersPageRole, UsersPageService } from './model';

interface BlockTarget {
  id: string;
  name: string;
  email: string;
  role: UsersPageRole;
  isBlocked: boolean;
}

@Component({
  selector: 'employee-users-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.scss',
})
export class UsersPageComponent {
  addModalOpen = signal(false);
  blockModalOpen = signal(false);
  actionInProgress = signal(false);
  errorText = signal('');
  clientUsers = signal<UsersPageRecord[]>([]);
  employeeUsers = signal<UsersPageRecord[]>([]);
  blockTarget = signal<BlockTarget | null>(null);

  newUser = {
    name: '',
    email: '',
    role: 'Клиент' as UsersPageRole,
    status: 'Активен',
  };

  constructor(
    private readonly usersPageService: UsersPageService,
    private readonly notifications: NotificationService
  ) {
    this.loadUsers();
  }

  addUser(): void {
    const trimmedName = this.newUser.name.trim();
    const trimmedEmail = this.newUser.email.trim().toLowerCase();
    if (!trimmedName || !trimmedEmail || this.actionInProgress()) {
      return;
    }

    const targetRole = this.newUser.role;
    this.actionInProgress.set(true);
    this.errorText.set('');

    this.usersPageService
      .createUser(trimmedName, trimmedEmail, targetRole)
      .pipe(finalize(() => this.actionInProgress.set(false)))
      .subscribe({
        next: () => {
          if (targetRole === 'Сотрудник') {
            this.reloadEmployeeUsers();
          } else {
            this.reloadClientUsers();
          }

          this.resetAddUserForm();
          this.addModalOpen.set(false);
          this.notifications.success('Пользователь создан.');
        },
        error: () => {
          const message = 'Не удалось создать пользователя. Проверьте данные и повторите попытку.';
          this.errorText.set(message);
          this.notifications.error(message);
        },
      });
  }

  openBlockUser(user: UsersPageRecord, role: UsersPageRole): void {
    if (this.actionInProgress()) {
      return;
    }

    this.blockTarget.set({
      id: user.id,
      name: user.name,
      email: user.email,
      role,
      isBlocked: this.isBlocked(user),
    });
    this.blockModalOpen.set(true);
  }

  confirmBlockUser(): void {
    const target = this.blockTarget();
    if (!target || this.actionInProgress()) {
      return;
    }

    this.actionInProgress.set(true);
    this.errorText.set('');

    const action$ = target.isBlocked
      ? this.usersPageService.unbanUser(target.id)
      : this.usersPageService.banUser(target.id);

    action$
      .pipe(finalize(() => this.actionInProgress.set(false)))
      .subscribe({
        next: (updatedUser) => {
          this.replaceUser(updatedUser);
          this.closeBlockModal();
          this.notifications.success(target.isBlocked ? 'Пользователь разблокирован.' : 'Пользователь заблокирован.');
        },
        error: () => {
          const message = 'Не удалось выполнить операцию. Попробуйте позже.';
          this.errorText.set(message);
          this.notifications.error(message);
        },
      });
  }

  closeBlockModal(): void {
    this.blockModalOpen.set(false);
    this.blockTarget.set(null);
  }

  isBlocked(user: UsersPageRecord): boolean {
    const normalizedStatus = user.status.toLowerCase();
    return (
      normalizedStatus.includes('блок') ||
      normalizedStatus.includes('banned') ||
      normalizedStatus.includes('block')
    );
  }

  private resetAddUserForm(): void {
    this.newUser = {
      name: '',
      email: '',
      role: 'Клиент',
      status: 'Активен',
    };
  }

  private loadUsers(): void {
    this.errorText.set('');
    this.reloadClientUsers();
    this.reloadEmployeeUsers();
  }

  private replaceUser(updatedUser: UsersPageRecord): void {
    this.clientUsers.update((users) =>
      users.map((user) => (user.id === updatedUser.id ? updatedUser : user))
    );
    this.employeeUsers.update((users) =>
      users.map((user) => (user.id === updatedUser.id ? updatedUser : user))
    );
  }

  private resolveLoadError(error: unknown, section: 'клиентов' | 'сотрудников'): string {
    if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
      return `Нет доступа к данным ${section}. Проверьте авторизацию.`;
    }

    return `Не удалось загрузить данные ${section}.`;
  }

  private reloadClientUsers(): void {
    this.usersPageService.getClientUsers().subscribe({
      next: (clientUsers) => {
        this.clientUsers.set(clientUsers);
      },
      error: (error: unknown) => {
        const message = this.resolveLoadError(error, 'клиентов');
        this.errorText.set(message);
        this.notifications.error(message);
      },
    });
  }

  private reloadEmployeeUsers(): void {
    this.usersPageService.getEmployeeUsers().subscribe({
      next: (employeeUsers) => {
        this.employeeUsers.set(employeeUsers);
      },
      error: (error: unknown) => {
        const nextMessage = this.resolveLoadError(error, 'сотрудников');
        const currentError = this.errorText();
        const fullMessage = currentError ? `${currentError} ${nextMessage}` : nextMessage;
        this.errorText.set(fullMessage);
        this.notifications.error(nextMessage);
      },
    });
  }
}

