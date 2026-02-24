export interface TariffRecord {
  name: string;
  createdAt: string;
  rate: string;
}

export const TARIFF_RECORDS: TariffRecord[] = [
  {
    name: 'Базовый кредит',
    createdAt: '01.12.2024',
    rate: '12.5%',
  },
  {
    name: 'Льготный кредит',
    createdAt: '01.12.2024',
    rate: '8%',
  },
];
