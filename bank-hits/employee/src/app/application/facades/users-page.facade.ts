import { HttpErrorResponse } from '@angular/common/http';
import { DestroyRef, Injectable, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, finalize, forkJoin, of, switchMap } from 'rxjs';
import { NotificationService } from 'shared/frontend-core';
import {
  EmployeeUsersUseCasesService,
  UsersPageRecord,
  UsersPageRole,
} from '../use-cases/employee-users-use-cases.service';

interface BlockTarget {
  id: string;
  name: string;
  email: string;
  role: UsersPageRole;
  isBlocked: boolean;
}

@Injectable()
export class UsersPageFacade {
  private readonly usersUseCases = inject(EmployeeUsersUseCasesService);
  private readonly notifications = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);

  readonly addModalOpen = signal(false);
  readonly blockModalOpen = signal(false);
  readonly actionInProgress = signal(false);
  readonly errorText = signal('');
  readonly clientUsers = signal<UsersPageRecord[]>([]);
  readonly employeeUsers = signal<UsersPageRecord[]>([]);
  readonly blockTarget = signal<BlockTarget | null>(null);

  newUser: { name: string; email: string; role: UsersPageRole; status: string } = {
    name: '',
    email: '',
    role: 'Клиент',
    status: 'Активен',
  };

  readonly hasUsers = computed(() => this.clientUsers().length > 0 || this.employeeUsers().length > 0);

  init(): void {
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

    this.usersUseCases
      .createUser(trimmedName, trimmedEmail, targetRole)
      .pipe(
        switchMap(() =>
          targetRole === 'Сотрудник'
            ? this.usersUseCases.getEmployeeUsers()
            : this.usersUseCases.getClientUsers()
        ),
        finalize(() => this.actionInProgress.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (users) => {
          if (targetRole === 'Сотрудник') {
            this.employeeUsers.set(users);
          } else {
            this.clientUsers.set(users);
          }

          this.resetAddUserForm();
          this.addModalOpen.set(false);
        },
        error: (error: unknown) => {
          const message = this.resolveAddUserError(error, targetRole);
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
      ? this.usersUseCases.unbanUser(target.id)
      : this.usersUseCases.banUser(target.id);

    action$
      .pipe(
        finalize(() => this.actionInProgress.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (updatedUser) => {
          this.replaceUser(updatedUser);
          this.closeBlockModal();
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
    return normalizedStatus.includes('блок') || normalizedStatus.includes('banned') || normalizedStatus.includes('block');
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

    const clientSection = 'клиентов';
    const employeeSection = 'сотрудников';
    let clientLoadFailed = false;
    let employeeLoadFailed = false;

    forkJoin({
      clientUsers: this.usersUseCases.getClientUsers().pipe(
        catchError((error: unknown) => {
          clientLoadFailed = true;
          const message = this.resolveLoadError(error, clientSection);
          this.notifications.error(message);
          return of<UsersPageRecord[]>([]);
        })
      ),
      employeeUsers: this.usersUseCases.getEmployeeUsers().pipe(
        catchError((error: unknown) => {
          employeeLoadFailed = true;
          const message = this.resolveLoadError(error, employeeSection);
          this.notifications.error(message);
          return of<UsersPageRecord[]>([]);
        })
      ),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(({ clientUsers, employeeUsers }) => {
        this.clientUsers.set(clientUsers);
        this.employeeUsers.set(employeeUsers);

        const errors: string[] = [];
        if (clientLoadFailed) {
          errors.push('Не удалось загрузить данные клиентов.');
        }
        if (employeeLoadFailed) {
          errors.push('Не удалось загрузить данные сотрудников.');
        }

        this.errorText.set(errors.join(' ').trim());
      });
  }

  private replaceUser(updatedUser: UsersPageRecord): void {
    this.clientUsers.update((users) =>
      users.map((user) => (user.id === updatedUser.id ? updatedUser : user))
    );
    this.employeeUsers.update((users) =>
      users.map((user) => (user.id === updatedUser.id ? updatedUser : user))
    );
  }

  private resolveLoadError(error: unknown, section: string): string {
    if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
      return `Нет доступа к данным ${section}. Проверьте авторизацию.`;
    }

    return `Не удалось загрузить данные ${section}.`;
  }

  private resolveAddUserError(error: unknown, targetRole: UsersPageRole): string {
    if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
      return 'Недостаточно прав для создания пользователя.';
    }

    const section = targetRole === 'Сотрудник' ? 'сотрудников' : 'клиентов';
    return `Не удалось создать пользователя и обновить список ${section}.`;
  }
}
