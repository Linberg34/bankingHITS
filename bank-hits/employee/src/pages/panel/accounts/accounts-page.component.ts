import { Component, OnDestroy, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationService } from 'shared/frontend-core';
import { BasicModalComponent } from 'shared/ui/basic-modal';
import { OperationsWsService, type WsBalanceEvent } from 'shared/api';
import { AccountOperationRecord, AccountPageRecord, AccountsPageService } from './model';
import { finalize } from 'rxjs';

@Component({
  selector: 'employee-accounts-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './accounts-page.component.html',
  styleUrl: './accounts-page.component.scss',
})
export class AccountsPageComponent implements OnDestroy {
  historyModalOpen = signal(false);
  isHistoryLoading = signal(false);
  errorText = signal('');
  selectedClient = signal('all');
  selectedStatus = signal('all');
  balanceSort = signal<'none' | 'asc' | 'desc'>('none');

  accountRecords = signal<AccountPageRecord[]>([]);
  selectedAccount = signal<AccountPageRecord | null>(null);
  selectedAccountOperations = signal<AccountOperationRecord[]>([]);

  private wsSub: Subscription | null = null;

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
    private readonly notifications: NotificationService,
    private readonly wsService: OperationsWsService
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

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    this.wsService.disconnect();
  }

  private loadAccounts(): void {
    this.errorText.set('');

    this.accountsPageService.loadAccounts().subscribe({
      next: (records) => {
        this.accountRecords.set(records);
        const accountIds = records
          .map((r) => r.accountId)
          .filter((id): id is string => !!id);
        if (accountIds.length) {
          this.connectWs(accountIds);
        }
      },
      error: () => {
        const message = 'Не удалось загрузить список счетов.';
        this.errorText.set(message);
        this.notifications.error(message);
      },
    });
  }

  private connectWs(accountIds: string[]): void {
    this.wsSub?.unsubscribe();
    this.wsSub = this.wsService.connect('http://localhost:8085/ws', accountIds).subscribe({
      next: (event) => {
        if (event.type === 'BALANCE_UPDATED') {
          const balanceEvent = event as WsBalanceEvent;
          this.notifications.success(
            `Баланс обновлён: ${balanceEvent.newBalance.toLocaleString('ru-RU')} ${balanceEvent.currency}`
          );
          this.loadAccountsOnly();
        } else if (event.type === 'OPERATION_ADDED' || event.type === 'OPERATION_UPDATED') {
          this.loadAccountsOnly();
          const openAccount = this.selectedAccount();
          if (openAccount) {
            this.refreshOperations(openAccount);
          }
        }
      },
    });
  }

  private loadAccountsOnly(): void {
    this.accountsPageService.loadAccounts().subscribe({
      next: (records) => this.accountRecords.set(records),
    });
  }

  private refreshOperations(record: AccountPageRecord): void {
    this.accountsPageService.loadOperations(record.accountNumber).subscribe({
      next: (operations) => this.selectedAccountOperations.set(operations),
    });
  }
}
