export interface AccountRecord {
  client: string;
  accountNumber: string;
  balance: string;
  status: string;
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
