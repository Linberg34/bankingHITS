import { Component } from '@angular/core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { TARIFF_RECORDS } from '../../../data-domain/tariffs/model/tariffs.model';

@Component({
  selector: 'employee-tariffs-page',
  standalone: true,
  imports: [BasicModalComponent],
  templateUrl: './tariffs-page.component.html',
  styleUrl: './tariffs-page.component.scss',
})
export class TariffsPageComponent {
  addTariffModalOpen = false;
  tariffs = TARIFF_RECORDS;
}
