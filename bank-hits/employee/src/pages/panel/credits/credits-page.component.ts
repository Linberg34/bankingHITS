import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { CREDIT_RECORDS, CREDIT_TABLE_COLUMNS, type CreditRecord } from '../../../data-domain/credits/model/credits.model';

@Component({
  selector: 'employee-credits-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './credits-page.component.html',
  styleUrl: './credits-page.component.scss',
})
export class CreditsPageComponent {
  columns = CREDIT_TABLE_COLUMNS;
  credits = [...CREDIT_RECORDS];
  readonly allCredits = [...CREDIT_RECORDS];
  readonly clientOptions = ['all', ...new Set(CREDIT_RECORDS.map((credit) => credit.clientName))];
  selectedClient = 'all';
  sortDirection: 'asc' | 'desc' = 'asc';
  detailsModalOpen = false;
  selectedCredit: CreditRecord | null = null;

  constructor() {
    this.applyFilters();
  }

  applyFilters(): void {
    let nextCredits = [...this.allCredits];
    if (this.selectedClient !== 'all') {
      nextCredits = nextCredits.filter((credit) => credit.clientName === this.selectedClient);
    }

    const sortMultiplier = this.sortDirection === 'asc' ? 1 : -1;
    nextCredits.sort((a, b) => a.clientName.localeCompare(b.clientName) * sortMultiplier);

    this.credits = nextCredits;
  }

  openDetails(credit: CreditRecord): void {
    this.selectedCredit = credit;
    this.detailsModalOpen = true;
  }

  closeDetails(): void {
    this.detailsModalOpen = false;
    this.selectedCredit = null;
  }
}
