import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';
import { HeaderComponent } from '../../../shared/ui/header';

@Component({
  imports: [HeaderComponent, RouterModule],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit, OnDestroy {
  private readonly router = inject(Router);
  private sub?: ReturnType<typeof this.router.events.subscribe>;

  protected title = 'Панель клиента';

  private setTitleFromRoute(): void {
    let route = this.router.routerState.root;
    while (route.firstChild) route = route.firstChild;
    const dataTitle = route.snapshot.data['title'] as string | undefined;
    this.title = dataTitle ?? 'Панель клиента';
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
