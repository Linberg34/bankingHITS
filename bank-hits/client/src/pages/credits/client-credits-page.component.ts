import { AsyncPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { combineLatest, map } from 'rxjs';
import { IDLE_ACTION_STATE, NotificationService, type AsyncActionState, mapUnknownError } from '../../../../shared/frontend-core';
import { BadgeComponent } from '../../../../shared/ui/badge';
import { ButtonComponent } from '../../../../shared/ui/button';
import {
  CardComponent,
  CardContentComponent,
  CardDescriptionComponent,
  CardHeaderComponent,
  CardTitleComponent,
} from '../../../../shared/ui/card';
import {
  DialogComponent,
  DialogDescriptionComponent,
  DialogFooterComponent,
  DialogHeaderComponent,
  DialogTitleComponent,
} from '../../../../shared/ui/dialog';
import { InputComponent } from '../../../../shared/ui/input';
import { LabelComponent } from '../../../../shared/ui/label';
import { SelectComponent } from '../../../../shared/ui/select';
import { ClientDataUseCasesService } from '../../app/application/use-cases/client-data-use-cases.service';
import { ClientSessionUseCasesService } from '../../app/application/use-cases/client-session-use-cases.service';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import type { Credit } from '../../app/core/models/client.types';

@Component({
  selector: 'app-client-credits-page',
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
    BadgeComponent,
    AsyncPipe,
  ],
  templateUrl: './client-credits-page.component.html',
  styleUrl: './client-credits-page.component.scss',
})
export class ClientCreditsPageComponent implements OnInit {
  private readonly data = inject(ClientDataUseCasesService);
  private readonly sessionUseCases = inject(ClientSessionUseCasesService);
  private readonly notifications = inject(NotificationService);

  protected openNewCredit = signal(false);
  protected openPayCredit = signal(false);
  protected selectedTariff = signal('');
  protected selectedAccount = signal('');
  protected creditAmount = signal('');
  protected paymentAmount = signal('');
  protected selectedCreditId = signal('');
  protected actionState = signal<AsyncActionState>(IDLE_ACTION_STATE);

  private readonly currentUserId = signal<number | null>(null);

  protected clientCredits$ = this.data.getCredits();

  protected tariffOptions$ = this.data.getCreditTariffs().pipe(
    map((tariffs) =>
      tariffs.map((item) => ({
        value: String(item.id ?? ''),
        label: `${item.name} - ${item.interestRate}% годовых`,
      }))
    )
  );

  protected accountOptions$ = this.data.getActiveAccounts().pipe(
    map((accounts) =>
      accounts.map((item) => ({
        value: String(item.accountNumber),
        label: `${item.accountNumber} (${this.formatMoney(item.balance)})`,
      }))
    )
  );

  protected selectedCreditForPayment$ = combineLatest([
    toObservable(this.selectedCreditId),
    this.data.getCredits(),
  ]).pipe(map(([id, credits]) => (id ? credits.find((item) => item.id === id) : undefined)));

  ngOnInit(): void {
    this.data.loadAccounts().subscribe({
      error: () => this.notifications.error('Failed to load accounts.'),
    });

    this.data.loadTariffs().subscribe({
      error: () => this.notifications.error('Failed to load tariffs.'),
    });

    this.sessionUseCases.getCurrentUser().subscribe({
      next: (user) => {
        const id = Number(user.id);
        this.currentUserId.set(id);
        this.data.loadCredits(id).subscribe({
          error: () => this.notifications.error('Failed to load credits.'),
        });
      },
      error: () => this.notifications.error('Failed to load user profile.'),
    });
  }

  protected formatMoney(n: number): string {
    return `${n.toLocaleString('ru-RU')} ?`;
  }

  protected formatDate(value: string): string {
    return new Date(value).toLocaleDateString('ru-RU');
  }

  protected getAccountNumber(accountId: string): string {
    return this.data.getAccountById(accountId)?.accountNumber ?? accountId;
  }

  protected getTariffName(tariffId: string): string {
    return this.data.getCreditTariffById(tariffId)?.name ?? tariffId;
  }

  protected getStatusVariant(status: string): 'default' | 'secondary' | 'destructive' {
    const mapValue: Record<string, 'default' | 'secondary' | 'destructive'> = {
      active: 'default',
      paid: 'secondary',
      overdue: 'destructive',
    };

    return mapValue[status] ?? 'secondary';
  }

  protected getStatusLabel(status: string): string {
    const mapValue: Record<string, string> = {
      active: 'Активный',
      paid: 'Погашен',
      overdue: 'Просрочен',
    };

    return mapValue[status] ?? status;
  }

  protected openNewCreditDialog(): void {
    this.selectedTariff.set('');
    this.selectedAccount.set('');
    this.creditAmount.set('');
    this.actionState.set(IDLE_ACTION_STATE);
    this.openNewCredit.set(true);
  }

  protected closeNewCredit(): void {
    this.openNewCredit.set(false);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected handleTakeCredit(): void {
    const accountNumber = this.selectedAccount();
    const tariff = Number(this.selectedTariff());
    const amount = Math.floor(Number(this.creditAmount()));

    if (!accountNumber || !Number.isFinite(tariff) || !Number.isFinite(amount) || amount <= 0) {
      this.actionState.set({ status: 'error', message: 'Fill all credit fields correctly.' });
      return;
    }

    this.actionState.set({ status: 'loading' });
    this.data.takeCredit(accountNumber, tariff, amount).subscribe({
      next: () => {
        this.actionState.set({ status: 'success', message: 'Credit created.' });        this.closeNewCredit();
        this.reloadCredits();
      },
      error: (error: unknown) => {
        const mapped = mapUnknownError(error);
        this.actionState.set({ status: 'error', message: mapped.message });
        this.notifications.error(mapped.message);
      },
    });
  }

  protected openPayCreditDialog(credit: Credit): void {
    this.selectedCreditId.set(credit.id);
    this.paymentAmount.set('');
    this.actionState.set(IDLE_ACTION_STATE);
    this.openPayCredit.set(true);
  }

  protected closePayCredit(): void {
    this.openPayCredit.set(false);
    this.actionState.set(IDLE_ACTION_STATE);
  }

  protected handlePayCredit(): void {
    const creditId = Number(this.selectedCreditId());
    const amount = Number(this.paymentAmount());

    if (!Number.isFinite(creditId) || !Number.isFinite(amount) || amount <= 0) {
      this.actionState.set({ status: 'error', message: 'Enter valid payment amount.' });
      return;
    }

    this.actionState.set({ status: 'loading' });
    this.data.repayCreditPartial(creditId, amount).subscribe({
      next: () => {
        this.actionState.set({ status: 'success', message: 'Payment completed.' });        this.closePayCredit();
        this.paymentAmount.set('');
        this.selectedCreditId.set('');
        this.reloadCredits();
      },
      error: (error: unknown) => {
        const mapped = mapUnknownError(error);
        this.actionState.set({ status: 'error', message: mapped.message });
        this.notifications.error(mapped.message);
      },
    });
  }

  protected canSubmitCredit(): boolean {
    return !!(
      this.selectedTariff() &&
      this.selectedAccount() &&
      this.creditAmount().trim() &&
      Number(this.creditAmount()) > 0
    );
  }

  private reloadCredits(): void {
    const currentUserId = this.currentUserId();
    if (currentUserId == null) {
      return;
    }

    this.data.loadCredits(currentUserId).subscribe({
      error: () => this.notifications.error('Failed to refresh credits.'),
    });
  }
}


