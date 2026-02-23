import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-client-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './client-layout.component.html',
  styleUrl: './client-layout.component.scss',
})
export class ClientLayoutComponent implements OnInit, OnDestroy {
  private readonly router = inject(Router);
  private sub?: ReturnType<typeof this.router.events.subscribe>;

  pageTitle = 'Клиент';

  navItems = [
    { path: 'dashboard', label: 'Главная' },
    { path: 'accounts', label: 'Мои счета' },
    { path: 'credits', label: 'Кредиты' },
  ];

  private setTitleFromRoute(): void {
    let route = this.router.routerState.root;
    while (route.firstChild) route = route.firstChild;
    this.pageTitle = (route.snapshot.data['title'] as string) ?? 'Клиент';
  }

  ngOnInit(): void {
    this.setTitleFromRoute();
    this.sub = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(() => this.setTitleFromRoute());
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
