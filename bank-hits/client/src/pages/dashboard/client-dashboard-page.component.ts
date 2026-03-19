import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AsyncPipe } from '@angular/common';
import { NotificationService } from '../../../../shared/frontend-core';
import { ClientDataUseCasesService } from '../../app/application/use-cases/client-data-use-cases.service';
import { ClientSessionUseCasesService } from '../../app/application/use-cases/client-session-use-cases.service';
import { ClientShellComponent } from '../../app/layout/client-shell/client-shell.component';
import {
  CardComponent,
  CardHeaderComponent,
  CardTitleComponent,
  CardDescriptionComponent,
  CardContentComponent,
} from '../../../../shared/ui/card';
import { map, switchMap } from 'rxjs';

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
  private readonly data = inject(ClientDataUseCasesService);
  private readonly sessionUseCases = inject(ClientSessionUseCasesService);
  private readonly notifications = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);

  protected clientAccounts$ = this.data.getActiveAccounts();
  protected clientCredits$ = this.data.getCredits();
  protected totalBalance$ = this.data.getActiveAccounts().pipe(
    map((accounts) => accounts.reduce((sum, acc) => sum + acc.balance, 0))
  );
  protected totalCreditDebt$ = this.data.getCredits().pipe(
    map((credits) => credits.reduce((sum, credit) => sum + credit.remainingAmount, 0))
  );

  ngOnInit(): void {
    this.data.loadAccounts().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      error: () => this.notifications.error('Failed to load accounts.'),
    });

    this.sessionUseCases
      .getCurrentUser()
      .pipe(
        switchMap((user) => this.data.loadCredits(Number(user.id))),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        error: () => this.notifications.error('Failed to load credits.'),
      });
  }

  protected formatDate(d: string): string {
    return new Date(d).toLocaleDateString('ru-RU');
  }

  protected formatMoney(n: number): string {
    return n.toLocaleString('ru-RU') + ' ₽';
  }
}
