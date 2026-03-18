import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { ThemeMode } from '../../frontend-core/theme/theme-mode.service';

@Component({
  selector: 'shared-header',
  standalone: true,
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  @Input() title = 'Интернет-Банк - Панель сотрудника';
  @Input() logoutLabel = 'Выход';
  @Input() themeMode: ThemeMode = 'light';
  @Input() showLogout = true;

  @Output() logout = new EventEmitter<void>();
  @Output() themeToggle = new EventEmitter<void>();

  get themeToggleLabel(): string {
    return this.themeMode === 'light' ? 'Темная тема' : 'Светлая тема';
  }
}
