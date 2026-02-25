import { Component, inject } from '@angular/core';
import { ClientDataService } from '../../app/core/services/client-data.service';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import {
  CardComponent,
  CardHeaderComponent,
  CardTitleComponent,
  CardDescriptionComponent,
  CardContentComponent,
} from '../../../../shared/ui/card';

@Component({
  selector: 'app-client-dashboard-page',
  standalone: true,
  imports: [
    ClientShellComponent,
    CardComponent,
    CardHeaderComponent,
    CardTitleComponent,
    CardDescriptionComponent,
    CardContentComponent,
  ],
  templateUrl: './client-dashboard-page.component.html',
  styleUrl: './client-dashboard-page.component.scss',
})
export class ClientDashboardPageComponent {
  private readonly data = inject(ClientDataService);

  protected clientAccounts = this.data.getActiveAccounts();
  protected clientCredits = this.data.getCredits();
  protected totalBalance = this.clientAccounts.reduce((sum, acc) => sum + acc.balance, 0);
  protected totalCreditDebt = this.clientCredits.reduce(
    (sum, credit) => sum + credit.remainingAmount,
    0
  );

  protected formatDate(d: string): string {
    return new Date(d).toLocaleDateString('ru-RU');
  }

  protected formatMoney(n: number): string {
    return n.toLocaleString('ru-RU') + ' ₽';
  }
}
