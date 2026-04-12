import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { DatePipe, PercentPipe } from '@angular/common';
import { MonitoringFeedService } from './data/mock-monitoring-feed.service';
import {
  type MonitoringApp,
  type MonitoringLevel,
  type MonitoringLogEntry,
  type MonitoringService,
} from './models/monitoring-log.model';

type AppFilter = 'all' | MonitoringApp;
type ServiceFilter = 'all' | MonitoringService;
type LevelFilter = 'all' | MonitoringLevel;

@Component({
  selector: 'app-root',
  imports: [DatePipe, PercentPipe],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'monitoring-shell',
  },
})
export class App {
  private readonly feed = inject(MonitoringFeedService);

  readonly logs = this.feed.logs;
  readonly paused = this.feed.paused;

  readonly appFilter = signal<AppFilter>('all');
  readonly serviceFilter = signal<ServiceFilter>('all');
  readonly levelFilter = signal<LevelFilter>('all');
  readonly query = signal('');

  readonly filteredLogs = computed(() => {
    const app = this.appFilter();
    const service = this.serviceFilter();
    const level = this.levelFilter();
    const query = this.query().trim().toLowerCase();

    return this.logs().filter((entry) => {
      if (app !== 'all' && entry.app !== app) {
        return false;
      }

      if (service !== 'all' && entry.service !== service) {
        return false;
      }

      if (level !== 'all' && entry.level !== level) {
        return false;
      }

      if (!query) {
        return true;
      }

      return (
        entry.path.toLowerCase().includes(query) ||
        entry.traceId.toLowerCase().includes(query) ||
        entry.message.toLowerCase().includes(query)
      );
    });
  });

  readonly latest = computed(() => this.filteredLogs().slice(0, 80));

  readonly summary = computed(() => {
    const items = this.filteredLogs();
    const total = items.length;
    if (!total) {
      return {
        total: 0,
        failures: 0,
        blocked: 0,
        retries: 0,
        avgLatency: 0,
        p95Latency: 0,
        errorRate: 0,
      };
    }

    const failures = items.filter((entry) => entry.status >= 500).length;
    const blocked = items.filter((entry) => entry.blockedByCircuit).length;
    const retries = items.reduce((acc, entry) => acc + entry.retries, 0);
    const latencies = items.map((entry) => entry.latencyMs).sort((left, right) => left - right);
    const avgLatency = Math.round(latencies.reduce((acc, value) => acc + value, 0) / total);
    const p95Index = Math.max(0, Math.ceil(latencies.length * 0.95) - 1);
    const p95Latency = latencies[p95Index] ?? 0;

    return {
      total,
      failures,
      blocked,
      retries,
      avgLatency,
      p95Latency,
      errorRate: failures / total,
    };
  });

  readonly latencySeries = computed(() => {
    const points = this.filteredLogs()
      .slice(0, 40)
      .map((entry) => entry.latencyMs)
      .reverse();

    return toLinePoints(points, 420, 120);
  });

  readonly errorRateSeries = computed(() => {
    const chunkSize = 8;
    const logs = this.filteredLogs().slice(0, 160).reverse();
    const values: number[] = [];

    for (let index = 0; index < logs.length; index += chunkSize) {
      const chunk = logs.slice(index, index + chunkSize);
      if (!chunk.length) {
        continue;
      }
      const failures = chunk.filter((entry) => entry.status >= 500).length;
      values.push((failures / chunk.length) * 100);
    }

    return toLinePoints(values, 420, 120, 100);
  });

  readonly appOptions: AppFilter[] = ['all', 'client', 'employee', 'system'];
  readonly serviceOptions: ServiceFilter[] = ['all', 'users', 'credits', 'core', 'sso', 'client-bff', 'employee-bff'];
  readonly levelOptions: LevelFilter[] = ['all', 'info', 'warn', 'error'];

  toggleStream(): void {
    this.feed.togglePause();
  }

  clearLogs(): void {
    this.feed.clear();
  }

  generateBurst(): void {
    this.feed.burst();
  }

  setAppFilter(value: string): void {
    this.appFilter.set(asAppFilter(value));
  }

  setServiceFilter(value: string): void {
    this.serviceFilter.set(asServiceFilter(value));
  }

  setLevelFilter(value: string): void {
    this.levelFilter.set(asLevelFilter(value));
  }

  setQuery(value: string): void {
    this.query.set(value);
  }

  appFilterLabel(option: AppFilter): string {
    return option === 'all' ? 'Все' : this.appLabel(option);
  }

  serviceFilterLabel(option: ServiceFilter): string {
    if (option === 'all') {
      return 'Все';
    }
    return this.serviceLabel(option);
  }

  levelFilterLabel(option: LevelFilter): string {
    switch (option) {
      case 'all':
        return 'Все';
      case 'info':
        return 'Инфо';
      case 'warn':
        return 'Предупреждение';
      default:
        return 'Ошибка';
    }
  }

  appLabel(value: MonitoringApp): string {
    switch (value) {
      case 'client':
        return 'Клиент';
      case 'employee':
        return 'Сотрудник';
      default:
        return 'Система';
    }
  }

  serviceLabel(value: MonitoringService): string {
    switch (value) {
      case 'users':
        return 'Пользователи';
      case 'credits':
        return 'Кредиты';
      case 'sso':
        return 'SSO';
      case 'client-bff':
        return 'Client BFF';
      case 'employee-bff':
        return 'Employee BFF';
      default:
        return 'Ядро';
    }
  }

  circuitStateLabel(value: 'CLOSED' | 'OPEN' | 'HALF_OPEN'): string {
    switch (value) {
      case 'CLOSED':
        return 'Закрыт';
      case 'OPEN':
        return 'Открыт';
      default:
        return 'Полуоткрыт';
    }
  }
}

function toLinePoints(values: number[], width: number, height: number, maxValue?: number): string {
  if (!values.length) {
    return '';
  }

  const largest = maxValue ?? Math.max(...values, 1);
  const stepX = values.length > 1 ? width / (values.length - 1) : width;

  return values
    .map((value, index) => {
      const x = index * stepX;
      const y = height - (value / largest) * height;
      return `${x.toFixed(2)},${y.toFixed(2)}`;
    })
    .join(' ');
}

function asAppFilter(value: string): AppFilter {
  return value === 'client' || value === 'employee' || value === 'system' ? value : 'all';
}

function asServiceFilter(value: string): ServiceFilter {
  return value === 'users' || value === 'credits' || value === 'core' || value === 'sso' || value === 'client-bff' || value === 'employee-bff' ? value : 'all';
}

function asLevelFilter(value: string): LevelFilter {
  return value === 'info' || value === 'warn' || value === 'error' ? value : 'all';
}
