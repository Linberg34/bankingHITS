export type CreditId = number | string;
export type UserId = number | string;
export type AccountId = number | string;

export interface CreditDto {
  id: CreditId;
  clientId: UserId;
  accountId: AccountId;
  tariffName: string;
  annualRate: number;
  principalAmount: number;
  remainingDebt: number;
  issuedAt: string;
  closedAt: string | null;
  status: string;
}
