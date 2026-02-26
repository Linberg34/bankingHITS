import { Component } from '@angular/core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { CREDIT_RECORDS, CREDIT_TABLE_COLUMNS, type CreditRecord } from '../../../data-domain/credits/model/credits.model';

@Component({
  selector: 'employee-credits-page',
  standalone: true,
  imports: [BasicModalComponent],
  templateUrl: './credits-page.component.html',
  styleUrl: './credits-page.component.scss',
})
export class CreditsPageComponent {
  columns = CREDIT_TABLE_COLUMNS;
  credits = CREDIT_RECORDS;
  detailsModalOpen = false;
  selectedCredit: CreditRecord | null = null;

  openDetails(credit: CreditRecord): void {
    this.selectedCredit = credit;
    this.detailsModalOpen = true;
  }

  closeDetails(): void {
    this.detailsModalOpen = false;
    this.selectedCredit = null;
  }
}
