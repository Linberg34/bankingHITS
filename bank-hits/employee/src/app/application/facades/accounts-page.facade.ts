import { DestroyRef, Injectable, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { NotificationService } from 'shared/frontend-core';
import {
  AccountOperationRecord,
  AccountPageRecord,
  EmployeeAccountsUseCasesService,
} from '../use-cases/employee-accounts-use-cases.service';

@Injectable()
export class AccountsPageFacade {
  private readonly accountsUseCases = inject(EmployeeAccountsUseCasesService);
  private readonly notifications = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);

  readonly historyModalOpen = signal(false);
  readonly isHistoryLoading = signal(false);
  readonly errorText = signal('');
  readonly selectedClient = signal('all');
  readonly selectedStatus = signal('all');
  readonly balanceSort = signal<'none' | 'asc' | 'desc'>('none');

  readonly accountRecords = signal<AccountPageRecord[]>([]);
  readonly selectedAccount = signal<AccountPageRecord | null>(null);
  readonly selectedAccountOperations = signal<AccountOperationRecord[]>([]);

  readonly clientOptions = computed(() => [
    'all',
    ...new Set(this.accountRecords().map((record) => record.client)),
  ]);

  readonly statusOptions = computed(() => [
    'all',
    ...new Set(this.accountRecords().map((record) => record.status)),
  ]);

  readonly filteredAccountRecords = computed(() => {
    let next = [...this.accountRecords()];

    const client = this.selectedClient();
    const status = this.selectedStatus();
    const balanceSort = this.balanceSort();

    if (client !== 'all') {
      next = next.filter((record) => record.client === client);
    }

    if (status !== 'all') {
      next = next.filter((record) => record.status === status);
    }

    if (balanceSort !== 'none') {
      const direction = balanceSort === 'asc' ? 1 : -1;
      next.sort((a, b) => (a.balanceValue - b.balanceValue) * direction);
    }

    return next;
  });

  init(): void {
    this.loadAccounts();
  }

  openHistory(record: AccountPageRecord): void {
    this.selectedAccount.set(record);
    this.selectedAccountOperations.set([]);
    this.historyModalOpen.set(true);
    this.isHistoryLoading.set(true);

    this.accountsUseCases
      .loadOperations(record.accountNumber)
      .pipe(
        finalize(() => this.isHistoryLoading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (operations) => {
          this.selectedAccountOperations.set(operations);
        },
        error: () => {
          const message = 'Не удалось загрузить историю операций.';
          this.errorText.set(message);
          this.notifications.error(message);
        },
      });
  }

  closeHistory(): void {
    this.historyModalOpen.set(false);
    this.selectedAccount.set(null);
    this.selectedAccountOperations.set([]);
  }

  private loadAccounts(): void {
    this.errorText.set('');

    this.accountsUseCases
      .loadAccounts()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (records) => {
          this.accountRecords.set(records);
        },
        error: () => {
          const message = 'Не удалось загрузить список счетов.';
          this.errorText.set(message);
          this.notifications.error(message);
        },
      });
  }
}
