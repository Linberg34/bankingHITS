import { Component, inject, signal, computed } from '@angular/core';
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
  ],
  templateUrl: './client-accounts-page.component.html',
  styleUrl: './client-accounts-page.component.scss',
})
export class ClientAccountsPageComponent {
  private readonly data = inject(ClientDataService);

  protected openNewAccount = signal(false);
  protected openDeposit = signal(false);
  protected openWithdraw = signal(false);
  protected openHistory = signal(false);
  protected selectedAccountId = signal('');
  protected amount = signal('');

  protected activeAccounts = this.data.getActiveAccounts();
  protected currencyOptions: SelectOption[] = [
    { value: 'RUB', label: 'Российский рубль (₽)' },
    { value: 'USD', label: 'Доллар США ($)' },
    { value: 'EUR', label: 'Евро (€)' },
  ];

  protected accountTransactions = computed(() => {
    const id = this.selectedAccountId();
    return id ? this.data.getTransactions(id) : [];
  });

  protected selectedAccountForHistory = computed(() => {
    const id = this.selectedAccountId();
    return id ? this.data.getAccountById(id) : undefined;
  });

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
  }
  protected closeNewAccount(): void {
    this.openNewAccount.set(false);
  }
  protected handleOpenAccount(): void {
    this.openNewAccount.set(false);
  }

  protected openDepositDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.amount.set('');
    this.openDeposit.set(true);
  }
  protected closeDeposit(): void {
    this.openDeposit.set(false);
  }
  protected handleDeposit(): void {
    this.amount.set('');
    this.openDeposit.set(false);
  }

  protected openWithdrawDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.amount.set('');
    this.openWithdraw.set(true);
  }
  protected closeWithdraw(): void {
    this.openWithdraw.set(false);
  }
  protected handleWithdraw(): void {
    this.amount.set('');
    this.openWithdraw.set(false);
  }

  protected openHistoryDialog(account: Account): void {
    this.selectedAccountId.set(account.id);
    this.openHistory.set(true);
  }
  protected closeHistory(): void {
    this.openHistory.set(false);
  }

  protected handleCloseAccount(accountNumber: string): void {}

  protected isIncoming(type: string): boolean {
    return type === 'deposit' || type === 'credit_issue';
  }
}
