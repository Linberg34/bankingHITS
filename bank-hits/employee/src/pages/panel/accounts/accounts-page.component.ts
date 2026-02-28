import { Component } from '@angular/core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import {
  ACCOUNT_OPERATIONS_BY_NUMBER,
  ACCOUNT_RECORDS,
  type AccountOperation,
  type AccountRecord,
} from '../../../data-domain/accounts/model/accounts.model';

@Component({
  selector: 'employee-accounts-page',
  standalone: true,
  imports: [BasicModalComponent],
  templateUrl: './accounts-page.component.html',
  styleUrl: './accounts-page.component.scss',
})
export class AccountsPageComponent {
  historyModalOpen = false;
  accountRecords = ACCOUNT_RECORDS;
  selectedAccount: AccountRecord | null = null;

  get selectedAccountOperations(): AccountOperation[] {
    if (!this.selectedAccount) {
      return [];
    }
    return ACCOUNT_OPERATIONS_BY_NUMBER[this.selectedAccount.accountNumber] ?? [];
  }

  openHistory(record: AccountRecord): void {
    this.selectedAccount = record;
    this.historyModalOpen = true;
  }

  closeHistory(): void {
    this.historyModalOpen = false;
    this.selectedAccount = null;
  }
}
