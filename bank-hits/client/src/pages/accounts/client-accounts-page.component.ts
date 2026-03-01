import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap, of, catchError } from 'rxjs';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import { ClientDataService } from '../../app/core/services/client-data.service';
import {
  CardComponent,
  CardHeaderComponent,
  CardTitleComponent,
  CardDescriptionComponent,
  CardContentComponent,
} from '../../../../shared/ui/card';
import { ButtonComponent } from '../../../../shared/ui/button';
import {
  DialogComponent,
  DialogHeaderComponent,
  DialogTitleComponent,
  DialogDescriptionComponent,
  DialogFooterComponent,
} from '../../../../shared/ui/dialog';
import { InputComponent } from '../../../../shared/ui/input';
import { LabelComponent } from '../../../../shared/ui/label';
import { SelectComponent, type SelectOption } from '../../../../shared/ui/select';
import {
  TableComponent,
  TableHeaderComponent,
  TableBodyComponent,
  TableRowComponent,
  TableHeadComponent,
  TableCellComponent,
} from '../../../../shared/ui/table';
import type { Account, Transaction } from '../../app/core/models/client.types';

const TRANSACTION_LABELS: Record<string, string> = {
  deposit: 'Пополнение',
  withdrawal: 'Снятие',
  credit_issue: 'Выдача кредита',
  credit_payment: 'Оплата кредита',
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
  private readonly data = inject(ClientDataService);

  protected openNewAccount = signal(false);
  protected openDeposit = signal(false);
  protected openWithdraw = signal(false);
  protected openHistory = signal(false);
  protected selectedAccountId = signal('');
  protected selectedAccountNumber = signal('');
  protected amount = signal('');
  protected actionError = signal<string | null>(null);
  protected actionLoading = signal(false);

  protected activeAccounts$ = this.data.getActiveAccounts();
  protected accountTransactions$ = toObservable(this.selectedAccountNumber).pipe(
    switchMap((accountNumber) =>
      accountNumber
        ? this.data.loadOperations(accountNumber).pipe(
            catchError(() => of<Transaction[]>([]))
          )
        : of<Transaction[]>([])
    )
  );
  protected selectedAccountForHistory = computed(() => {
    const id = this.selectedAccountId();
    const num = this.selectedAccountNumber();
    return id || num ? this.data.getAccountById(id || num) : undefined;
  });

  protected currencyOptions: SelectOption[] = [
    { value: 'RUB', label: 'Российский рубль (₽)' },
    { value: 'USD', label: 'Доллар США ($)' },
    { value: 'EUR', label: 'Евро (€)' },
  ];

  ngOnInit(): void {
    this.data.loadAccounts().subscribe();
  }

  protected formatMoney(n: number): string {
    return n.toLocaleString('ru-RU') + ' ₽';
  }

  protected getTransactionLabel(type: string): string {
    return TRANSACTION_LABELS[type] ?? type;
  }

  protected formatDate(d: string): string {
    return new Date(d).toLocaleDateString('ru-RU');
  }

  protected openNewAccountDialog(): void {
    this.openNewAccount.set(true);
    this.actionError.set(null);
  }
  protected closeNewAccount(): void {
    this.openNewAccount.set(false);
    this.actionError.set(null);
  }
  protected handleOpenAccount(): void {
    this.actionLoading.set(true);
    this.actionError.set(null);
    this.data.openCurrentAccount().subscribe({
      next: () => {
        this.actionLoading.set(false);
        this.closeNewAccount();
      },
      error: (err) => {
        this.actionLoading.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось открыть счёт');
      },
    });
  }

  protected openDepositDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.amount.set('');
    this.actionError.set(null);
    this.openDeposit.set(true);
  }
  protected closeDeposit(): void {
    this.openDeposit.set(false);
    this.actionError.set(null);
  }
  protected handleDeposit(): void {
    const id = this.selectedAccountId();
    const sum = Number(this.amount());
    if (!id || !sum || sum <= 0) return;
    this.actionLoading.set(true);
    this.actionError.set(null);
    this.data.deposit(id, sum).subscribe({
      next: () => {
        this.actionLoading.set(false);
        this.amount.set('');
        this.closeDeposit();
      },
      error: (err) => {
        this.actionLoading.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось пополнить счёт');
      },
    });
  }

  protected openWithdrawDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.amount.set('');
    this.actionError.set(null);
    this.openWithdraw.set(true);
  }
  protected closeWithdraw(): void {
    this.openWithdraw.set(false);
    this.actionError.set(null);
  }
  protected handleWithdraw(): void {
    const id = this.selectedAccountId();
    const sum = Number(this.amount());
    if (!id || !sum || sum <= 0) return;
    this.actionLoading.set(true);
    this.actionError.set(null);
    this.data.withdraw(id, sum).subscribe({
      next: () => {
        this.actionLoading.set(false);
        this.amount.set('');
        this.closeWithdraw();
      },
      error: (err) => {
        this.actionLoading.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось снять средства');
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

  protected handleCloseAccount(account: Account): void {
    if (!confirm('Закрыть счёт ' + account.accountNumber + '?')) return;
    this.actionLoading.set(true);
    this.actionError.set(null);
    this.data.deleteAccount(account.id).subscribe({
      next: () => this.actionLoading.set(false),
      error: (err) => {
        this.actionLoading.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось закрыть счёт');
      },
    });
  }

  protected isIncoming(type: string): boolean {
    return type === 'deposit' || type === 'credit_issue';
  }
}
