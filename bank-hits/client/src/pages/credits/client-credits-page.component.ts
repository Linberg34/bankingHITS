import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { toObservable } from '@angular/core/rxjs-interop';
import { map, combineLatest, switchMap, of } from 'rxjs';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import { ClientDataService } from '../../app/core/services/client-data.service';
import { AuthApiService } from '../../app/core/services/auth-api.service';
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
    AsyncPipe,
  ],
  templateUrl: './client-credits-page.component.html',
  styleUrl: './client-credits-page.component.scss',
})
export class ClientCreditsPageComponent implements OnInit {
  private readonly data = inject(ClientDataService);
  private readonly authApi = inject(AuthApiService);

  protected openNewCredit = signal(false);
  protected openPayCredit = signal(false);
  protected selectedTariff = signal('');
  protected selectedAccount = signal('');
  protected creditAmount = signal('');
  protected paymentAmount = signal('');
  protected selectedCreditId = signal('');
  protected actionError = signal<string | null>(null);
  protected actionLoading = signal(false);

  private readonly currentUserId = signal<number | null>(null);

  protected clientCredits$ = this.data.getCredits();
  protected clientAccounts$ = this.data.getActiveAccounts();
  protected creditTariffs$ = this.data.getCreditTariffs();

  protected tariffOptions$ = this.data.getCreditTariffs().pipe(
    map((tariffs) =>
      tariffs.map((t) => ({
        value: String(t.id ?? ''),
        label: `${t.name} - ${t.interestRate}% годовых`,
      }))
    )
  );

  protected accountOptions$ = this.data.getActiveAccounts().pipe(
    map((accounts) =>
      accounts.map((a) => ({
        value: String(a.id ?? ''),
        label: `${a.accountNumber} (${this.formatMoney(a.balance)})`,
      }))
    )
  );

  protected selectedCreditForPayment$ = combineLatest([
    toObservable(this.selectedCreditId),
    this.data.getCredits(),
  ]).pipe(map(([id, credits]) => (id ? credits.find((c) => c.id === id) : undefined)));

  ngOnInit(): void {
    this.data.loadAccounts().subscribe();
    this.data.loadTariffs().subscribe();
    this.authApi.getMe().subscribe((user) => {
      this.currentUserId.set(user.id);
      this.data.loadCredits(user.id).subscribe();
    });
  }

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
    return this.data.getCreditTariffById(tariffId)?.name ?? tariffId;
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
    this.actionError.set(null);
    this.openNewCredit.set(true);
  }
  protected closeNewCredit(): void {
    this.openNewCredit.set(false);
    this.actionError.set(null);
  }
  protected handleTakeCredit(): void {
    const accountStr = this.selectedAccount();
    const tariffStr = this.selectedTariff();
    const amountRaw = this.creditAmount();

    this.actionError.set(null);

    if (!tariffStr) {
      this.actionError.set('Выберите тариф');
      return;
    }
    if (!accountStr) {
      this.actionError.set('Выберите счёт для зачисления');
      return;
    }
    if (!amountRaw) {
      this.actionError.set('Введите сумму кредита');
      return;
    }

    const amount = Math.floor(Number(amountRaw));
    if (!Number.isFinite(amount) || amount < 1) {
      this.actionError.set('Введите корректную сумму (не менее 1 ₽)');
      return;
    }

    const accountNumber = accountStr;
    const tariffId = Number(tariffStr);
    if (!Number.isFinite(tariffId)) {
      this.actionError.set('Неверные данные счёта или тарифа. Обновите страницу и попробуйте снова.');
      return;
    }

    this.actionLoading.set(true);
    this.actionError.set(null);
    this.data.takeCredit(accountNumber, tariffId, amount).subscribe({
      next: () => {
        this.actionLoading.set(false);
        this.closeNewCredit();
        this.selectedTariff.set('');
        this.selectedAccount.set('');
        this.creditAmount.set('');
        const uid = this.currentUserId();
        if (uid != null) this.data.loadCredits(uid).subscribe();
      },
      error: (err) => {
        this.actionLoading.set(false);
        const msg = err?.error?.message ?? err?.message ?? err?.statusText;
        this.actionError.set(msg && String(msg).trim() ? String(msg) : 'Не удалось оформить кредит');
      },
    });
  }

  protected openPayCreditDialog(credit: Credit): void {
    this.selectedCreditId.set(credit.id);
    this.paymentAmount.set('');
    this.actionError.set(null);
    this.openPayCredit.set(true);
  }
  protected closePayCredit(): void {
    this.openPayCredit.set(false);
    this.actionError.set(null);
  }
  protected handlePayCredit(): void {
    const creditId = Number(this.selectedCreditId());
    const amount = Number(this.paymentAmount());
    if (!creditId || !amount || amount <= 0) return;
    this.actionLoading.set(true);
    this.actionError.set(null);
    this.data.repayCreditPartial(creditId, amount).subscribe({
      next: () => {
        this.actionLoading.set(false);
        this.closePayCredit();
        this.paymentAmount.set('');
        this.selectedCreditId.set('');
        const uid = this.currentUserId();
        if (uid != null) this.data.loadCredits(uid).subscribe();
      },
      error: (err) => {
        this.actionLoading.set(false);
        const msg = err?.error?.message ?? err?.message ?? err?.statusText;
        this.actionError.set(msg && String(msg).trim() ? String(msg) : 'Не удалось погасить кредит');
      },
    });
  }

  protected handlePayCreditFull(credit: Credit): void {
    if (!confirm('Погасить кредит полностью?')) return;
    const creditId = Number(credit.id);
    this.actionLoading.set(true);
    this.actionError.set(null);
    this.data.repayCreditFull(creditId).subscribe({
      next: () => {
        this.actionLoading.set(false);
        const uid = this.currentUserId();
        if (uid != null) this.data.loadCredits(uid).subscribe();
      },
      error: (err) => {
        this.actionLoading.set(false);
        const msg = err?.error?.message ?? err?.message ?? err?.statusText;
        this.actionError.set(msg && String(msg).trim() ? String(msg) : 'Не удалось погасить кредит');
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
}
