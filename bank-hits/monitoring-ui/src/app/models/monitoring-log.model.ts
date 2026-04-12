export type MonitoringApp = 'client' | 'employee' | 'system';

export type MonitoringService = 'users' | 'credits' | 'core' | 'sso' | 'client-bff' | 'employee-bff';

export type MonitoringLevel = 'info' | 'warn' | 'error';

export type CircuitState = 'CLOSED' | 'OPEN' | 'HALF_OPEN';

export interface MonitoringLogEntry {
  id: string;
  timestamp: number;
  app: MonitoringApp;
  service: MonitoringService;
  level: MonitoringLevel;
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  path: string;
  status: number;
  latencyMs: number;
  retries: number;
  blockedByCircuit: boolean;
  circuitState: CircuitState;
  traceId: string;
  message: string;
}
