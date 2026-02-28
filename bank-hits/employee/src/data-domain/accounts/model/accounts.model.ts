export interface AccountRecord {
  client: string;
  accountNumber: string;
  balance: string;
  status: string;
}

export interface AccountOperation {
  id: string;
  date: string;
  type: string;
  amount: string;
  description: string;
}

export const ACCOUNT_RECORDS: AccountRecord[] = [
  {
    client: 'Иван Петров',
    accountNumber: '40817810099910004312',
    balance: '50 000 ₽',
    status: 'Активен',
  },
  {
    client: 'Мария Сидорова',
    accountNumber: '40817810099910004313',
    balance: '75 000 ₽',
    status: 'Активен',
  },
];

export const ACCOUNT_OPERATIONS_BY_NUMBER: Record<string, AccountOperation[]> = {
  '40817810099910004312': [
    {
      id: 'op-1001',
      date: '24.02.2026',
      type: 'Пополнение',
      amount: '+20 000 ₽',
      description: 'Перевод с накопительного счета',
    },
    {
      id: 'op-1002',
      date: '25.02.2026',
      type: 'Снятие',
      amount: '-5 000 ₽',
      description: 'Снятие наличных в банкомате',
    },
    {
      id: 'op-1003',
      date: '26.02.2026',
      type: 'Оплата кредита',
      amount: '-3 400 ₽',
      description: 'Ежедневный платеж по кредиту',
    },
  ],
  '40817810099910004313': [
    {
      id: 'op-1004',
      date: '23.02.2026',
      type: 'Пополнение',
      amount: '+10 000 ₽',
      description: 'Зачисление заработной платы',
    },
    {
      id: 'op-1005',
      date: '26.02.2026',
      type: 'Снятие',
      amount: '-2 500 ₽',
      description: 'Оплата коммунальных услуг',
    },
  ],
};
