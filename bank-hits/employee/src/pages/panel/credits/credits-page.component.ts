import { Component } from '@angular/core';
import { CREDIT_TABLE_COLUMNS } from '../../../data-domain/credits/model/credits.model';

@Component({
  selector: 'employee-credits-page',
  standalone: true,
  templateUrl: './credits-page.component.html',
  styleUrl: './credits-page.component.scss',
})
export class CreditsPageComponent {
  columns = CREDIT_TABLE_COLUMNS;
}
