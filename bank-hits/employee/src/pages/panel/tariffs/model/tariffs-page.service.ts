import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { type CreateTariffResponse, type TariffDto } from 'shared/entities/tariffs';
import { EmployeeAdminRequestService } from '../../../../app/infrastructure/request/employee-admin-request.service';

export interface TariffRecord {
  id: string;
  name: string;
  rate: string;
  termDays: number;
  createdAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class TariffsPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  loadTariffs(): Observable<TariffRecord[]> {
    return this.requestService
      .getTariffs()
      .pipe(map((tariffs) => tariffs.map((tariff) => this.mapTariff(tariff))));
  }

  createTariff(name: string, annualRate: number): Observable<TariffRecord> {
    return this.requestService
      .createTariff(name, annualRate)
      .pipe(map((tariff) => this.mapCreatedTariff(tariff)));
  }

  private mapTariff(tariff: TariffDto): TariffRecord {
    return {
      id: tariff.tariffId,
      name: tariff.name,
      rate: `${tariff.interestRate}%`,
      termDays: tariff.termDays,
      createdAt: '',
    };
  }

  private mapCreatedTariff(tariff: CreateTariffResponse): TariffRecord {
    return {
      id: tariff.tariffId,
      name: tariff.name,
      rate: `${tariff.interestRate}%`,
      termDays: tariff.termDays,
      createdAt: '',
    };
  }
}
