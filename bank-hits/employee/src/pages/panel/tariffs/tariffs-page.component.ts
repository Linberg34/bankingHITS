import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { TARIFF_RECORDS, type TariffRecord } from '../../../data-domain/tariffs/model/tariffs.model';

@Component({
  selector: 'employee-tariffs-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  templateUrl: './tariffs-page.component.html',
  styleUrl: './tariffs-page.component.scss',
})
export class TariffsPageComponent {
  addTariffModalOpen = false;
  tariffs = [...TARIFF_RECORDS];
  isSubmitted = false;
  nameError = '';
  rateError = '';

  newTariff = {
    name: '',
    rate: '',
  };

  openAddTariffModal(): void {
    this.resetValidation();
    this.addTariffModalOpen = true;
  }

  closeAddTariffModal(): void {
    this.addTariffModalOpen = false;
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
    if (!this.validateForm()) {
      return;
    }

    const trimmedName = this.newTariff.name.trim();
    const parsedRate = Number(this.newTariff.rate);
    const tariff: TariffRecord = {
      name: trimmedName,
      rate: `${parsedRate}%`,
      createdAt: this.formatDate(new Date()),
    };

    this.tariffs = [tariff, ...this.tariffs];
    this.closeAddTariffModal();
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

    const isDuplicate = this.tariffs.some(
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

  private formatDate(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}
