import { Component, inject, signal, computed } from '@angular/core';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
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
import { BadgeComponent } from '../../../../shared/ui/badge';
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
  ],
  templateUrl: './client-credits-page.component.html',
  styleUrl: './client-credits-page.component.scss',
})
export class ClientCreditsPageComponent {
  private readonly data = inject(ClientDataService);

  protected openNewCredit = signal(false);
  protected openPayCredit = signal(false);
  protected selectedTariff = signal('');
  protected selectedAccount = signal('');
  protected creditAmount = signal('');
  protected paymentAmount = signal('');
  protected selectedCreditId = signal('');

  protected clientCredits = this.data.getCredits();
  protected clientAccounts = this.data.getActiveAccounts();
  protected creditTariffs = this.data.getCreditTariffs();

  protected tariffOptions = computed<SelectOption[]>(() =>
    this.creditTariffs.map((t) => ({
      value: t.id,
      label: `${t.name} - ${t.interestRate}% годовых`,
    }))
  );

  protected accountOptions = computed<SelectOption[]>(() =>
    this.clientAccounts.map((a) => ({
      value: a.id,
      label: `${a.accountNumber} (${this.formatMoney(a.balance)})`,
    }))
  );

  protected selectedCreditForPayment = computed(() => {
    const id = this.selectedCreditId();
    return this.clientCredits.find((c) => c.id === id);
  });

  protected formatMoney(n: number): string {
    return n.toLocaleString('ru-RU') + ' ₽';
  }

  protected formatDate(d: string): string {
    return new Date(d).toLocaleDateString('ru-RU');
  }

  protected getAccountNumber(accountId: string): string {
    return this.data.getAccountById(accountId)?.accountNumber ?? '';
  }

  protected getTariffName(tariffId: string): string {
    return this.data.getCreditTariffById(tariffId)?.name ?? '';
  }

  protected getStatusVariant(
    status: string
  ): 'default' | 'secondary' | 'destructive' {
    const map: Record<string, 'default' | 'secondary' | 'destructive'> = {
      active: 'default',
      paid: 'secondary',
      overdue: 'destructive',
    };
    return map[status] ?? 'secondary';
  }

  protected getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      active: 'Активный',
      paid: 'Погашен',
      overdue: 'Просрочен',
    };
    return map[status] ?? status;
  }

  protected openNewCreditDialog(): void {
    this.selectedTariff.set('');
    this.selectedAccount.set('');
    this.creditAmount.set('');
    this.openNewCredit.set(true);
  }
  protected closeNewCredit(): void {
    this.openNewCredit.set(false);
  }
  protected handleTakeCredit(): void {
    this.openNewCredit.set(false);
    this.selectedTariff.set('');
    this.selectedAccount.set('');
    this.creditAmount.set('');
  }

  protected openPayCreditDialog(credit: Credit): void {
    this.selectedCreditId.set(credit.id);
    this.paymentAmount.set('');
    this.openPayCredit.set(true);
  }
  protected closePayCredit(): void {
    this.openPayCredit.set(false);
  }
  protected handlePayCredit(): void {
    this.openPayCredit.set(false);
    this.paymentAmount.set('');
    this.selectedCreditId.set('');
  }

  protected canSubmitCredit(): boolean {
    return !!(
      this.selectedTariff() &&
      this.selectedAccount() &&
      this.creditAmount().trim()
    );
  }
}
