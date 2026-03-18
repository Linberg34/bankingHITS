import { Injectable } from '@angular/core';

export type ThemeMode = 'light' | 'dark';

const THEME_STORAGE_KEY = 'bank-hits.theme-mode';
const DEFAULT_THEME_MODE: ThemeMode = 'light';

@Injectable({ providedIn: 'root' })
export class ThemeModeService {
  private currentMode: ThemeMode = DEFAULT_THEME_MODE;

  constructor() {
    this.currentMode = this.readStoredMode();
    this.applyMode(this.currentMode);
  }

  get mode(): ThemeMode {
    return this.currentMode;
  }

  toggle(): void {
    this.setMode(this.currentMode === 'light' ? 'dark' : 'light');
  }

  setMode(mode: ThemeMode): void {
    this.currentMode = mode;
    localStorage.setItem(THEME_STORAGE_KEY, mode);
    this.applyMode(mode);
  }

  private readStoredMode(): ThemeMode {
    const storedMode = localStorage.getItem(THEME_STORAGE_KEY);
    return storedMode === 'dark' ? 'dark' : DEFAULT_THEME_MODE;
  }

  private applyMode(mode: ThemeMode): void {
    document.documentElement.setAttribute('data-theme', mode);
  }
}
