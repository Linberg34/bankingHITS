import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ErrorStateService } from '../errors/error-state.service';

@Component({
  selector: 'shared-error-fallback-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './error-fallback-page.component.html',
  styleUrl: './error-fallback-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorFallbackPageComponent {
  private readonly errorState = inject(ErrorStateService);

  protected readonly error = this.errorState.lastUnhandledError;
  protected readonly message = computed(
    () => this.error()?.message || 'Unexpected error. Please try again later.'
  );
}

