export type CreditId = number | string;
export type UserId = number | string;
export type AccountId = number | string;

export interface CreditDto {
  id: CreditId;
  clientId: UserId;
  accountId?: AccountId;
  accountNumber?: string;
  tariffName: string;
  annualRate: number;
  principalAmount: number;
  remainingDebt: number;
  issuedAt: string;
  closedAt: string | null;
  status: string;
}

export interface TakeCreditRequest {
  accountNumber: string;
  tariffId: number;
  amount: number;
}

export interface RepayPartialRequest {
  amount: number;
}
