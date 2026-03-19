import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';
import { type TariffDto } from 'shared/entities/tariffs';
import { EmployeeAdminApiService } from '../../data-access/api/employee-admin-api.service';

export interface TariffRecord {
  id: string;
  name: string;
  createdAt: string;
  rate: string;
}

@Injectable({ providedIn: 'root' })
export class EmployeeTariffsUseCasesService {
  private readonly api = inject(EmployeeAdminApiService);

  loadTariffs(): Observable<TariffRecord[]> {
    return this.api.getTariffs().pipe(map((tariffs) => tariffs.map((tariff) => this.mapTariff(tariff))));
  }

  createTariff(name: string, annualRate: number): Observable<TariffRecord> {
    return this.api.createTariff(name, annualRate).pipe(map((tariff) => this.mapTariff(tariff)));
  }

  private mapTariff(tariff: TariffDto): TariffRecord {
    return {
      id: String(tariff.id),
      name: tariff.name,
      createdAt: this.formatDate(tariff.createdAt),
      rate: `${tariff.annualRate}%`,
    };
  }

  private formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}

