import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NotificationService } from '../../../../../shared/frontend-core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import {
  CreditRecord,
  EmployeeCreditsUseCasesService,
} from '../../../app/application/use-cases/employee-credits-use-cases.service';

const CREDIT_TABLE_COLUMNS = ['Клиент', 'Счет', 'Тариф', 'Сумма', 'Осталось', 'Ставка', 'Статус', 'Дата выдачи'];

@Component({
  selector: 'employee-credits-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './credits-page.component.html',
  styleUrl: './credits-page.component.scss',
})
export class CreditsPageComponent {
  private readonly destroyRef = inject(DestroyRef);

  columns = CREDIT_TABLE_COLUMNS;
  credits = signal<CreditRecord[]>([]);
  readonly allCredits = signal<CreditRecord[]>([]);
  clientOptions = signal<string[]>(['all']);
  selectedClient = 'all';
  detailsModalOpen = false;
  selectedCredit: CreditRecord | null = null;
  errorText = signal('');

  constructor(
    private readonly creditsUseCases: EmployeeCreditsUseCasesService,
    private readonly notifications: NotificationService
  ) {
    this.loadCredits();
  }

  applyFilters(): void {
    let nextCredits = [...this.allCredits()];
    if (this.selectedClient !== 'all') {
      nextCredits = nextCredits.filter((credit) => credit.clientName === this.selectedClient);
    }

    this.credits.set(nextCredits);
  }

  openDetails(credit: CreditRecord): void {
    this.selectedCredit = credit;
    this.detailsModalOpen = true;
  }

  closeDetails(): void {
    this.detailsModalOpen = false;
    this.selectedCredit = null;
  }

  isActiveStatus(status: string): boolean {
    const normalized = status.toLowerCase();
    return normalized.includes('актив') || normalized === 'active';
  }

  isPaidStatus(status: string): boolean {
    const normalized = status.toLowerCase();
    return normalized.includes('погаш') || normalized === 'paid' || normalized === 'closed';
  }

  isOverdueStatus(status: string): boolean {
    const normalized = status.toLowerCase();
    return normalized.includes('проср') || normalized === 'overdue';
  }

  private loadCredits(): void {
    this.errorText.set('');

    this.creditsUseCases
      .loadCredits()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: (records) => {
        this.allCredits.set(records);
        this.clientOptions.set(['all', ...new Set(records.map((credit) => credit.clientName))]);
        if (!this.clientOptions().includes(this.selectedClient)) {
          this.selectedClient = 'all';
        }

        this.applyFilters();
      },
      error: () => {
        const message = 'Не удалось загрузить кредиты.';
        this.errorText.set(message);
        this.notifications.error(message);
      },
      });
  }
}


