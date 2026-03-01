import { Component, inject, OnInit } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { ClientDataService } from '../../app/core/services/client-data.service';
import { AuthApiService } from '../../app/core/services/auth-api.service';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import {
  CardComponent,
  CardHeaderComponent,
  CardTitleComponent,
  CardDescriptionComponent,
  CardContentComponent,
} from '../../../../shared/ui/card';
import { map } from 'rxjs';

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
    AsyncPipe,
  ],
  templateUrl: './client-dashboard-page.component.html',
  styleUrl: './client-dashboard-page.component.scss',
})
export class ClientDashboardPageComponent implements OnInit {
  private readonly data = inject(ClientDataService);
  private readonly authApi = inject(AuthApiService);

  protected clientAccounts$ = this.data.getActiveAccounts();
  protected clientCredits$ = this.data.getCredits();
  protected totalBalance$ = this.data.getActiveAccounts().pipe(
    map((accounts) => accounts.reduce((sum, acc) => sum + acc.balance, 0))
  );
  protected totalCreditDebt$ = this.data.getCredits().pipe(
    map((credits) => credits.reduce((sum, credit) => sum + credit.remainingAmount, 0))
  );

  ngOnInit(): void {
    this.data.loadAccounts().subscribe();
    this.authApi.getMe().subscribe((user) => {
      this.data.loadCredits(user.id).subscribe();
    });
  }

  protected formatDate(d: string): string {
    return new Date(d).toLocaleDateString('ru-RU');
  }

  protected formatMoney(n: number): string {
    return n.toLocaleString('ru-RU') + ' ₽';
  }
}
