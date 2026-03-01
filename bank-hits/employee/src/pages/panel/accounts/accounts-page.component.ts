import { Component } from '@angular/core';
import { finalize } from 'rxjs';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { AccountOperationRecord, AccountPageRecord, AccountsPageService } from './model';

@Component({
  selector: 'employee-accounts-page',
  standalone: true,
  imports: [BasicModalComponent],
  templateUrl: './accounts-page.component.html',
  styleUrl: './accounts-page.component.scss',
})
export class AccountsPageComponent {
  historyModalOpen = false;
  isLoading = false;
  isHistoryLoading = false;
  errorText = '';
  accountRecords: AccountPageRecord[] = [];
  selectedAccount: AccountPageRecord | null = null;
  selectedAccountOperations: AccountOperationRecord[] = [];

  constructor(private readonly accountsPageService: AccountsPageService) {
    this.loadAccounts();
  }

  openHistory(record: AccountPageRecord): void {
    this.selectedAccount = record;
    this.selectedAccountOperations = [];
    this.historyModalOpen = true;
    this.isHistoryLoading = true;

    this.accountsPageService
      .loadOperations(record.accountNumber)
      .pipe(finalize(() => (this.isHistoryLoading = false)))
      .subscribe({
        next: (operations) => {
          this.selectedAccountOperations = operations;
        },
        error: () => {
          this.errorText = 'Не удалось загрузить историю операций.';
        },
      });
  }

  closeHistory(): void {
    this.historyModalOpen = false;
    this.selectedAccount = null;
    this.selectedAccountOperations = [];
  }

  private loadAccounts(): void {
    this.isLoading = true;
    this.errorText = '';

    this.accountsPageService
      .loadAccounts()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (records) => {
          this.accountRecords = records;
        },
        error: () => {
          this.errorText = 'Не удалось загрузить список счетов.';
        },
      });
  }
}
