export interface CreditDto {
  id: number;
  clientId: number;
  accountNumber: string;
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
