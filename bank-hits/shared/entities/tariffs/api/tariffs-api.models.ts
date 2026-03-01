export interface TariffDto {
  id: number | string;
  name: string;
  annualRate: number;
  createdAt: string;
}

export interface CreateTariffRequest {
  name: string;
  annualRate: number;
}

