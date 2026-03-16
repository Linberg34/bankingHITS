import { HttpErrorResponse } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { NotificationService } from '../../../../../shared/frontend-core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { TariffRecord, TariffsPageService } from './model';

@Component({
  selector: 'employee-tariffs-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './tariffs-page.component.html',
  styleUrl: './tariffs-page.component.scss',
})
export class TariffsPageComponent {
  addTariffModalOpen = signal(false);
  actionInProgress = signal(false);
  errorText = signal('');
  tariffs = signal<TariffRecord[]>([]);
  isSubmitted = false;
  nameError = '';
  rateError = '';

  newTariff = {
    name: '',
    rate: '',
  };

  constructor(
    private readonly tariffsPageService: TariffsPageService,
    private readonly notifications: NotificationService
  ) {
    this.loadTariffs();
  }

  openAddTariffModal(): void {
    this.resetValidation();
    this.addTariffModalOpen.set(true);
  }

  closeAddTariffModal(): void {
    this.addTariffModalOpen.set(false);
    this.resetForm();
    this.resetValidation();
  }

  onNameChange(): void {
    if (this.isSubmitted) {
      this.validateName();
    }
  }

  onRateChange(): void {
    if (this.isSubmitted) {
      this.validateRate();
    }
  }

  get hasNameError(): boolean {
    return this.isSubmitted && !!this.nameError;
  }

  get hasRateError(): boolean {
    return this.isSubmitted && !!this.rateError;
  }

  addTariff(): void {
    this.isSubmitted = true;
    if (!this.validateForm() || this.actionInProgress()) {
      return;
    }

    const trimmedName = this.newTariff.name.trim();
    const parsedRate = Number(this.newTariff.rate);
    this.actionInProgress.set(true);
    this.errorText.set('');

    this.tariffsPageService
      .createTariff(trimmedName, parsedRate)
      .pipe(finalize(() => this.actionInProgress.set(false)))
      .subscribe({
        next: (tariff) => {
          this.tariffs.update((records) => [tariff, ...records]);
          this.closeAddTariffModal();
          this.notifications.success('Тариф создан.');
        },
        error: (error: unknown) => {
          const message = this.resolveErrorText(error);
          this.errorText.set(message);
          this.notifications.error(message);
        },
      });
  }

  private loadTariffs(): void {
    this.errorText.set('');

    this.tariffsPageService
      .loadTariffs()
      .subscribe({
        next: (tariffs) => {
          this.tariffs.set(tariffs);
          this.notifications.info('Тарифы обновлены.');
        },
        error: () => {
          const message = 'Не удалось загрузить тарифы.';
          this.errorText.set(message);
          this.notifications.error(message);
        },
      });
  }

  private validateForm(): boolean {
    this.validateName();
    this.validateRate();
    return !this.nameError && !this.rateError;
  }

  private validateName(): void {
    const name = String(this.newTariff.name ?? '').trim();
    if (!name) {
      this.nameError = 'Введите название тарифа.';
      return;
    }
    if (name.length < 3) {
      this.nameError = 'Название должно быть не короче 3 символов.';
      return;
    }

    const isDuplicate = this.tariffs().some(
      (tariff) => tariff.name.toLowerCase() === name.toLowerCase()
    );
    if (isDuplicate) {
      this.nameError = 'Тариф с таким названием уже существует.';
      return;
    }

    this.nameError = '';
  }

  private validateRate(): void {
    const rawRate = String(this.newTariff.rate ?? '').trim();
    if (!rawRate) {
      this.rateError = 'Укажите процентную ставку.';
      return;
    }

    const parsedRate = Number(rawRate);
    if (Number.isNaN(parsedRate)) {
      this.rateError = 'Ставка должна быть числом.';
      return;
    }

    if (parsedRate <= 0) {
      this.rateError = 'Ставка не может быть отрицательной или нулевой.';
      return;
    }

    if (parsedRate > 100) {
      this.rateError = 'Ставка не может быть больше 100%.';
      return;
    }

    this.rateError = '';
  }

  private resetForm(): void {
    this.newTariff = { name: '', rate: '' };
  }

  private resetValidation(): void {
    this.isSubmitted = false;
    this.nameError = '';
    this.rateError = '';
  }

  private resolveErrorText(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Не удалось сохранить тариф. Попробуйте позже.';
    }

    const backendMessage =
      typeof error.error === 'object' && error.error && 'message' in error.error
        ? String(error.error.message)
        : '';

    return backendMessage || 'Не удалось сохранить тариф.';
  }
}

