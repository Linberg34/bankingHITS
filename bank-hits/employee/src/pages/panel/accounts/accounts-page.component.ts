import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { NotificationService } from '../../../../../shared/frontend-core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { AccountOperationRecord, AccountPageRecord, AccountsPageService } from './model';

@Component({
  selector: 'employee-accounts-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './accounts-page.component.html',
  styleUrl: './accounts-page.component.scss',
})
export class AccountsPageComponent {
  historyModalOpen = signal(false);
  isHistoryLoading = signal(false);
  errorText = signal('');
  selectedClient = signal('all');
  selectedStatus = signal('all');
  balanceSort = signal<'none' | 'asc' | 'desc'>('none');

  accountRecords = signal<AccountPageRecord[]>([]);
  selectedAccount = signal<AccountPageRecord | null>(null);
  selectedAccountOperations = signal<AccountOperationRecord[]>([]);

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

  constructor(
    private readonly accountsPageService: AccountsPageService,
    private readonly notifications: NotificationService
  ) {
    this.loadAccounts();
  }

  openHistory(record: AccountPageRecord): void {
    this.selectedAccount.set(record);
    this.selectedAccountOperations.set([]);
    this.historyModalOpen.set(true);
    this.isHistoryLoading.set(true);

    this.accountsPageService
      .loadOperations(record.accountNumber)
      .pipe(finalize(() => this.isHistoryLoading.set(false)))
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

    this.accountsPageService.loadAccounts().subscribe({
      next: (records) => {
        this.accountRecords.set(records);      },
      error: () => {
        const message = 'Не удалось загрузить список счетов.';
        this.errorText.set(message);
        this.notifications.error(message);
      },
    });
  }
}


