/** Тариф (client-bff TariffDto) */
export interface TariffDto {
  tariffId: string;  // UUID
  name: string;
  interestRate: number;  // annualRate
  termDays: number;
}

export interface CreateTariffRequest {
  name: string;
  interestRate: number;
  termDays: number;
}

export interface CreateTariffResponse {
  tariffId: string;
  name: string;
  interestRate: number;
  termDays: number;
}
