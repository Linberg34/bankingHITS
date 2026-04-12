import { InjectionToken } from '@angular/core';

export type MonitoringApp = 'client' | 'employee';

export const MONITORING_APP_NAME = new InjectionToken<MonitoringApp>('MONITORING_APP_NAME');
