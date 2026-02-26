export interface CreditRecord {
  id: string;
  clientName: string;
  account: string;
  tariff: string;
  amount: string;
  remaining: string;
  rate: string;
  status: string;
  issuedAt: string;
  termMonths: number;
  paidMonths: number;
  nextPaymentDate: string;
  nextPaymentAmount: string;
  purpose: string;
}

export const CREDIT_TABLE_COLUMNS = ['Клиент', 'Счет', 'Тариф', 'Сумма', 'Осталось', 'Ставка', 'Статус', 'Дата выдачи'];

export const CREDIT_RECORDS: CreditRecord[] = [
  {
    id: 'CR-1001',
    clientName: 'Иван Петров',
    account: '40817810099910001234',
    tariff: 'Базовый кредит',
    amount: '1 200 000 ₽',
    remaining: '884 300 ₽',
    rate: '12.5%',
    status: 'Активен',
    issuedAt: '12.09.2025',
    termMonths: 36,
    paidMonths: 14,
    nextPaymentDate: '10.03.2026',
    nextPaymentAmount: '45 900 ₽',
    purpose: 'Ремонт квартиры',
  },
  {
    id: 'CR-1002',
    clientName: 'Мария Сидорова',
    account: '40817810099910005678',
    tariff: 'Льготный кредит',
    amount: '650 000 ₽',
    remaining: '410 750 ₽',
    rate: '8%',
    status: 'Активен',
    issuedAt: '03.11.2025',
    termMonths: 24,
    paidMonths: 8,
    nextPaymentDate: '05.03.2026',
    nextPaymentAmount: '31 200 ₽',
    purpose: 'Покупка авто',
  },
  {
    id: 'CR-1003',
    clientName: 'Алексей Смирнов',
    account: '40817810099910007890',
    tariff: 'Базовый кредит',
    amount: '300 000 ₽',
    remaining: '0 ₽',
    rate: '12.5%',
    status: 'Погашен',
    issuedAt: '15.02.2024',
    termMonths: 18,
    paidMonths: 18,
    nextPaymentDate: '-',
    nextPaymentAmount: '-',
    purpose: 'Потребительские нужды',
  },
];
