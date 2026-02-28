import { Component } from '@angular/core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { ACCOUNT_RECORDS } from '../../../data-domain/accounts/model/accounts.model';

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
}
