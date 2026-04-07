import { AsyncPipe } from '@angular/common';
import { Component, NgZone, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { BehaviorSubject, Subscription, catchError, combineLatest, map, of, switchMap } from 'rxjs';
import { OperationsWsService, type WsBalanceEvent } from 'shared/api';
import { IDLE_ACTION_STATE, NotificationService, type AsyncActionState, mapUnknownError } from 'shared/frontend-core';
import { ButtonComponent } from 'shared/ui/button';
import {
  CardComponent,
  CardContentComponent,
  CardDescriptionComponent,
  CardHeaderComponent,
  CardTitleComponent,
} from 'shared/ui/card';
import { ConfirmDialogComponent } from 'shared/ui/confirm-dialog';
import {
  DialogComponent,
  DialogDescriptionComponent,
  DialogFooterComponent,
  DialogHeaderComponent,
  DialogTitleComponent,
} from 'shared/ui/dialog';
import { InputComponent } from 'shared/ui/input';
import { LabelComponent } from 'shared/ui/label';
import { SelectComponent, type SelectOption } from 'shared/ui/select';
import {
  TableBodyComponent,
  TableCellComponent,
  TableComponent,
  TableHeadComponent,
  TableHeaderComponent,
  TableRowComponent,
} from 'shared/ui/table';
import { ClientDataUseCasesService } from '../../app/application/use-cases/client-data-use-cases.service';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import type { Account, Transaction } from '../../app/core/models/client.types';

const TRANSACTION_LABELS: Record<string, string> = {
  deposit: 'Пополнение',
  withdrawal: 'Снятие',
  transfer_in: 'Входящий перевод',
  transfer_out: 'Исходящий перевод',
  credit_issue: 'Выдача кредита',
  credit_payment: 'Погашение кредита',
};

@Component({
  selector: 'app-client-accounts-page',
  standalone: true,
  imports: [
    ClientShellComponent,
    CardComponent,
    CardHeaderComponent,
    CardTitleComponent,
    CardDescriptionComponent,
    CardContentComponent,
    ButtonComponent,
    DialogComponent,
    DialogHeaderComponent,
    DialogTitleComponent,
    DialogDescriptionComponent,
    DialogFooterComponent,
    ConfirmDialogComponent,
    InputComponent,
    LabelComponent,
    SelectComponent,
    TableComponent,
    TableHeaderComponent,
    TableBodyComponent,
    TableRowComponent,
    TableHeadComponent,
    TableCellComponent,
    AsyncPipe,
  ],
  templateUrl: './client-accounts-page.component.html',
  styleUrl: './client-accounts-page.component.scss',
})
export class ClientAccountsPageComponent implements OnInit, OnDestroy {
  private readonly data = inject(ClientDataUseCasesService);
  private readonly notifications = inject(NotificationService);
  private readonly wsService = inject(OperationsWsService);
  private readonly ngZone = inject(NgZone);
  private wsSub: Subscription | null = null;

  protected openNewAccount = signal(false);
  protected openDeposit = signal(false);
  protected openWithdraw = signal(false);
  protected openTransfer = signal(false);
  protected openHistory = signal(false);
  protected openCloseConfirm = signal(false);
  protected showHidden = signal(false);

  protected selectedAccountId = signal('');
  protected selectedAccountNumber = signal('');
  protected selectedAccountToClose = signal<Account | null>(null);
  protected selectedCurrency = signal<'RUB' | 'USD' | 'EUR'>('RUB');

  protected amount = signal('');
  protected transferToAccountNumber = signal('');
  protected selectedAccountBalance = signal(0);
  protected actionState = signal<AsyncActionState>(IDLE_ACTION_STATE);

  private readonly operationRefresh$ = new BehaviorSubject<null>(null);

  protected visibleAccounts$ = combineLatest([
    this.data.getActiveAccounts(),
    this.data.getHiddenAccountIds(),
    toObservable(this.showHidden),
  ]).pipe(
    map(([accounts, hiddenIds, showHidden]) =>
      showHidden
        ? accounts
        : accounts.filter((a) => !hiddenIds.has(a.uuid ?? a.accountNumber))
    )
  );

  protected accountTransactions$ = toObservable(this.selectedAccountNumber).pipe(
    switchMap((accountNumber) =>
      accountNumber
        ? this.operationRefresh$.pipe(
            switchMap(() =>
              this.data.loadOperations(accountNumber).pipe(catchError(() => of<Transaction[]>([])))
            )
          )
        : of<Transaction[]>([])
    )
  );

  protected selectedAccountForHistory = computed(() => {
    const id = this.selectedAccountId();
    const number = this.selectedAccountNumber();
    return id || number ? this.data.getAccountById(id || number) : undefined;
  });

  protected currencyOptions: SelectOption[] = [
    { value: 'RUB', label: 'Российский рубль (RUB)' },
    { value: 'USD', label: 'Доллар США (USD)' },
    { value: 'EUR', label: 'Евро (EUR)' },
  ];

  ngOnInit(): void {
    this.data.loadHiddenAccounts().subscribe();
    this.data.loadAccounts().subscribe({
      next: (accounts) => {
        const uuids = accounts.map((a) => a.uuid);
        this.connectWs(uuids);
      },
      error: () => this.notifications.error('Failed to load accounts.'),
    });
  }

  private connectWs(accountIds: string[]): void {
    this.wsSub?.unsubscribe();
    this.wsSub = this.wsService.connect('http://localhost:8084/ws', accountIds).subscribe({
      next: (event) => {
        this.ngZone.run(() => {
          if (event.type === 'BALANCE_UPDATED') {
            const balanceEvent = event as WsBalanceEvent;
            this.notifications.success(
              `Баланс обновлён: ${balanceEvent.newBalance.toLocaleString('ru-RU')} ${balanceEvent.currency}`
            );
            this.data.loadAccounts().subscribe();
          } else if (event.type === 'OPERATION_ADDED' || event.type === 'OPERATION_UPDATED') {
            this.data.loadAccounts().subscribe();
            if (this.selectedAccountNumber()) {
              this.operationRefresh$.next(null);
            }
          }
        });
      },
    });
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    this.wsService.disconnect();
  }

  protected formatMoney(n: number, currency?: string): string {
    const symbol = currency === 'USD' ? '$' : currency === 'EUR' ? '€' : '₽';
    return `${n.toLocaleString('ru-RU')} ${symbol}`;
  }

  protected getTransactionLabel(type: string): string {
    return TRANSACTION_LABELS[type] ?? type;
  }

  protected formatDate(value: string): string {
    return new Date(value).toLocaleDateString('ru-RU');
  }

  protected isAccountHidden(account: Account): boolean {
    return this.data.isAccountHidden(account);
  }

  protected toggleHidden(account: Account): void {
    this.data.toggleAccountHidden(account).subscribe({
      error: () => this.notifications.error('Не удалось обновить настройки.'),
    });
  }

  protected openNewAccountDialog(): void {
    this.selectedCurrency.set('RUB');
    this.openNewAccount.set(true);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected closeNewAccount(): void {
    this.openNewAccount.set(false);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected handleOpenAccount(): void {
    this.actionState.set({ status: 'loading' });
    this.data.openAccount(this.selectedCurrency()).subscribe({
      next: () => {
        this.actionState.set({ status: 'success', message: 'Account opened.' });
        this.closeNewAccount();
      },
      error: (error: unknown) => {
        const mapped = mapUnknownError(error);
        this.actionState.set({ status: 'error', message: mapped.message });
        this.notifications.error(mapped.message);
      },
    });
  }

  protected openDepositDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.amount.set('');
    this.actionState.set(IDLE_ACTION_STATE);
    this.openDeposit.set(true);
  }

  protected closeDeposit(): void {
    this.openDeposit.set(false);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected handleDeposit(): void {
    const accountId = this.selectedAccountId();
    const sum = Number(this.amount());
    if (!accountId || !sum || sum <= 0) {
      return;
    }

    this.actionState.set({ status: 'loading' });
    this.data.deposit(accountId, sum).subscribe({
      next: () => {
        this.actionState.set({ status: 'success', message: 'Balance updated.' });
        this.amount.set('');
        this.closeDeposit();
      },
      error: (error: unknown) => {
        const mapped = mapUnknownError(error);
        this.actionState.set({ status: 'error', message: mapped.message });
        this.notifications.error(mapped.message);
      },
    });
  }

  protected openWithdrawDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.selectedAccountBalance.set(account.balance);
    this.amount.set('');
    this.actionState.set(IDLE_ACTION_STATE);
    this.openWithdraw.set(true);
  }

  protected closeWithdraw(): void {
    this.openWithdraw.set(false);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected handleWithdraw(): void {
    const accountId = this.selectedAccountId();
    const sum = Number(this.amount());
    if (!accountId || !sum || sum <= 0) {
      return;
    }
    if (sum > this.selectedAccountBalance()) {
      this.notifications.error('Недостаточно средств на счёте.');
      return;
    }

    this.actionState.set({ status: 'loading' });
    this.data.withdraw(accountId, sum).subscribe({
      next: () => {
        this.actionState.set({ status: 'success', message: 'Withdrawal completed.' });
        this.amount.set('');
        this.closeWithdraw();
      },
      error: (error: unknown) => {
        const mapped = mapUnknownError(error);
        this.actionState.set({ status: 'error', message: mapped.message });
        this.notifications.error(mapped.message);
      },
    });
  }

  protected openTransferDialog(account: Account): void {
    this.selectedAccountId.set(account.accountNumber);
    this.selectedAccountBalance.set(account.balance);
    this.amount.set('');
    this.transferToAccountNumber.set('');
    this.actionState.set(IDLE_ACTION_STATE);
    this.openTransfer.set(true);
  }

  protected closeTransfer(): void {
    this.openTransfer.set(false);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected handleTransfer(): void {
    const fromAccount = this.selectedAccountId();
    const toAccount = this.transferToAccountNumber().trim();
    const sum = Number(this.amount());
    if (!fromAccount || !toAccount || !sum || sum <= 0) {
      return;
    }
    if (sum > this.selectedAccountBalance()) {
      this.notifications.error('Недостаточно средств на счёте.');
      return;
    }

    this.actionState.set({ status: 'loading' });
    this.data.transfer(fromAccount, toAccount, sum).subscribe({
      next: () => {
        this.actionState.set({ status: 'success', message: 'Transfer sent.' });
        this.amount.set('');
        this.transferToAccountNumber.set('');
        this.closeTransfer();
      },
      error: (error: unknown) => {
        const mapped = mapUnknownError(error);
        this.actionState.set({ status: 'error', message: mapped.message });
        this.notifications.error(mapped.message);
      },
    });
  }

  protected openHistoryDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.selectedAccountNumber.set(account.accountNumber);
    this.openHistory.set(true);
  }

  protected closeHistory(): void {
    this.openHistory.set(false);
    this.selectedAccountNumber.set('');
  }

  protected requestCloseAccount(account: Account): void {
    this.selectedAccountToClose.set(account);
    this.openCloseConfirm.set(true);
  }

  protected handleCloseAccount(): void {
    const account = this.selectedAccountToClose();
    if (!account) {
      return;
    }

    this.actionState.set({ status: 'loading' });
    this.data.closeAccount(account.accountNumber).subscribe({
      next: () => {
        this.actionState.set({ status: 'success', message: 'Account closed.' });
        this.openCloseConfirm.set(false);
        this.selectedAccountToClose.set(null);
      },
      error: (error: unknown) => {
        const mapped = mapUnknownError(error);
        this.actionState.set({ status: 'error', message: mapped.message });
        this.notifications.error(mapped.message);
      },
    });
  }

  protected isIncoming(type: string): boolean {
    return type === 'deposit' || type === 'credit_issue' || type === 'transfer_in';
  }
}
