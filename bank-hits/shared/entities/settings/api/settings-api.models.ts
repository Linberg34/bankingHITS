export type SettingsTheme = 'LIGHT' | 'DARK';

export interface ClientSettingsDto {
  theme: SettingsTheme;
  hiddenAccountIds: string[];
}

export interface UpdateSettingsRequest {
  theme?: SettingsTheme;
  hiddenAccountIds?: string[];
}
