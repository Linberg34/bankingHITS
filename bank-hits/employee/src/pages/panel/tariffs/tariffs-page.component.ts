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

  newTariff = {
    name: '',
    rate: '',
  };

  addTariff(): void {
    const trimmedName = this.newTariff.name.trim();
    const parsedRate = Number(this.newTariff.rate);
    if (!trimmedName || Number.isNaN(parsedRate) || parsedRate <= 0) {
      return;
    }

    const tariff: TariffRecord = {
      name: trimmedName,
      rate: `${parsedRate}%`,
      createdAt: this.formatDate(new Date()),
    };

    this.tariffs = [tariff, ...this.tariffs];
    this.newTariff = { name: '', rate: '' };
    this.addTariffModalOpen = false;
  }

  private formatDate(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}
