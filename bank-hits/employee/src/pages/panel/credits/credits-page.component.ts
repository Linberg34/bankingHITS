import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NotificationService } from 'shared/frontend-core';
import { BasicModalComponent } from 'shared/ui/basic-modal';
import { type ClientOption, type CreditRecord, CreditsPageService } from './model';

const CREDIT_TABLE_COLUMNS = ['Клиент', 'Тариф', 'Сумма', 'Осталось', 'Ставка', 'Статус'];

@Component({
  selector: 'employee-credits-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './credits-page.component.html',
  styleUrl: './credits-page.component.scss',
})
export class CreditsPageComponent implements OnInit {
  columns = CREDIT_TABLE_COLUMNS;
  credits = signal<CreditRecord[]>([]);
  allCredits = signal<CreditRecord[]>([]);
  clients = signal<ClientOption[]>([]);
  selectedClientId = 'all';
  detailsModalOpen = false;
  selectedCredit: CreditRecord | null = null;
  errorText = signal('');
  isLoading = signal(false);

  constructor(
    private readonly creditsPageService: CreditsPageService,
    private readonly notifications: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadClients();
  }

  private loadClients(): void {
    this.creditsPageService.loadClients().subscribe({
      next: (clients) => {
        this.clients.set(clients);
        this.loadAllCredits(clients);
      },
      error: () => {
        const message = 'Не удалось загрузить список клиентов.';
        this.errorText.set(message);
        this.notifications.error(message);
      },
    });
  }

  private loadAllCredits(clients: ClientOption[]): void {
    this.isLoading.set(true);
    this.creditsPageService.loadAllCredits(clients).subscribe({
      next: (records) => {
        this.allCredits.set(records);
        this.applyFilters();
        this.isLoading.set(false);
      },
      error: () => {
        const message = 'Не удалось загрузить кредиты.';
        this.errorText.set(message);
        this.notifications.error(message);
        this.isLoading.set(false);
      },
    });
  }

  applyFilters(): void {
    const id = this.selectedClientId;
    const all = this.allCredits();
    this.credits.set(id === 'all' ? all : all.filter((c) => c.clientId === id));
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
    return status.toLowerCase().includes('актив') || status === 'active';
  }

  isPaidStatus(status: string): boolean {
    const n = status.toLowerCase();
    return n.includes('погаш') || n === 'paid' || n === 'closed';
  }

  isOverdueStatus(status: string): boolean {
    return status.toLowerCase().includes('проср') || status === 'overdue';
  }
}
