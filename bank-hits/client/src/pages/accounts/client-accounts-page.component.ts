import { AsyncPipe } from '@angular/common';
import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { catchError, of, switchMap } from 'rxjs';
import { IDLE_ACTION_STATE, NotificationService, type AsyncActionState, mapUnknownError } from '../../../../shared/frontend-core';
import { ButtonComponent } from '../../../../shared/ui/button';
import {
  CardComponent,
  CardContentComponent,
  CardDescriptionComponent,
  CardHeaderComponent,
  CardTitleComponent,
} from '../../../../shared/ui/card';
import { ConfirmDialogComponent } from '../../../../shared/ui/confirm-dialog';
import {
  DialogComponent,
  DialogDescriptionComponent,
  DialogFooterComponent,
  DialogHeaderComponent,
  DialogTitleComponent,
} from '../../../../shared/ui/dialog';
import { InputComponent } from '../../../../shared/ui/input';
import { LabelComponent } from '../../../../shared/ui/label';
import { SelectComponent, type SelectOption } from '../../../../shared/ui/select';
import {
  TableBodyComponent,
  TableCellComponent,
  TableComponent,
  TableHeadComponent,
  TableHeaderComponent,
  TableRowComponent,
} from '../../../../shared/ui/table';
import { ClientDataUseCasesService } from '../../app/application/use-cases/client-data-use-cases.service';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import type { Account, Transaction } from '../../app/core/models/client.types';

const TRANSACTION_LABELS: Record<string, string> = {
  deposit: 'Пополнение',
  withdrawal: 'Снятие',
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
export class ClientAccountsPageComponent implements OnInit {
  private readonly data = inject(ClientDataUseCasesService);
  private readonly notifications = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);

  protected openNewAccount = signal(false);
  protected openDeposit = signal(false);
  protected openWithdraw = signal(false);
  protected openHistory = signal(false);
  protected openCloseConfirm = signal(false);

  protected selectedAccountId = signal('');
  protected selectedAccountNumber = signal('');
  protected selectedAccountToClose = signal<Account | null>(null);

  protected amount = signal('');
  protected actionState = signal<AsyncActionState>(IDLE_ACTION_STATE);

  protected activeAccounts$ = this.data.getActiveAccounts();
  protected accountTransactions$ = toObservable(this.selectedAccountNumber).pipe(
    switchMap((accountNumber) =>
      accountNumber
        ? this.data.loadOperations(accountNumber).pipe(catchError(() => of<Transaction[]>([])))
        : of<Transaction[]>([])
    )
  );

  protected selectedAccountForHistory = computed(() => {
    const id = this.selectedAccountId();
    const number = this.selectedAccountNumber();
    return id || number ? this.data.getAccountById(id || number) : undefined;
  });

  protected currencyOptions: SelectOption[] = [{ value: 'RUB', label: 'Российский рубль (?)' }];

  ngOnInit(): void {
    this.data.loadAccounts().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      error: () => this.notifications.error('Failed to load accounts.'),
    });
  }

  protected formatMoney(n: number): string {
    return `${n.toLocaleString('ru-RU')} ?`;
  }

  protected getTransactionLabel(type: string): string {
    return TRANSACTION_LABELS[type] ?? type;
  }

  protected formatDate(value: string): string {
    return new Date(value).toLocaleDateString('ru-RU');
  }

  protected openNewAccountDialog(): void {
    this.openNewAccount.set(true);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected closeNewAccount(): void {
    this.openNewAccount.set(false);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected handleOpenAccount(): void {
    this.actionState.set({ status: 'loading' });
    this.data
      .openCurrentAccount()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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
    this.data
      .deposit(accountId, sum)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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

    this.actionState.set({ status: 'loading' });
    this.data
      .withdraw(accountId, sum)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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
    this.data
      .deleteAccount(account.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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
    return type === 'deposit' || type === 'credit_issue';
  }
}


