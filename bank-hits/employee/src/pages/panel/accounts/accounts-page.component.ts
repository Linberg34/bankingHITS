import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { AccountPageRecord } from '../../../app/application/use-cases/employee-accounts-use-cases.service';
import { AccountsPageFacade } from '../../../app/application/facades/accounts-page.facade';

@Component({
  selector: 'employee-accounts-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  providers: [AccountsPageFacade],
  templateUrl: './accounts-page.component.html',
  styleUrl: './accounts-page.component.scss',
})
export class AccountsPageComponent implements OnInit {
  private readonly facade = inject(AccountsPageFacade);

  protected readonly historyModalOpen = this.facade.historyModalOpen;
  protected readonly isHistoryLoading = this.facade.isHistoryLoading;
  protected readonly errorText = this.facade.errorText;
  protected readonly selectedClient = this.facade.selectedClient;
  protected readonly selectedStatus = this.facade.selectedStatus;
  protected readonly balanceSort = this.facade.balanceSort;

  protected readonly selectedAccount = this.facade.selectedAccount;
  protected readonly selectedAccountOperations = this.facade.selectedAccountOperations;

  protected readonly clientOptions = this.facade.clientOptions;
  protected readonly statusOptions = this.facade.statusOptions;
  protected readonly filteredAccountRecords = this.facade.filteredAccountRecords;

  ngOnInit(): void {
    this.facade.init();
  }

  protected openHistory(record: AccountPageRecord): void {
    this.facade.openHistory(record);
  }

  protected closeHistory(): void {
    this.facade.closeHistory();
  }
}
