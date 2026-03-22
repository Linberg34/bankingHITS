export type CreditStatus = 'ACTIVE' | 'OVERDUE' | 'CLOSED';
export type PaymentStatus = 'SCHEDULED' | 'PAID' | 'OVERDUE';

/** Кредит в списке (client-bff CreditSummaryDto) */
export interface CreditSummaryDto {
  creditId: string;
  currency: string;
  accountNumber: string;
  amount: number;          // principalAmount
  remainingDebt: number;
  interestRate: number;    // annualRate
  tariffName: string;
  status: CreditStatus;
  nextPaymentAt: string | null;
}

/** Обёртка GET /bff/client/credits */
export interface CreditListResponse {
  credits: CreditSummaryDto[];
}

/** Платёж по кредиту */
export interface CreditPaymentDto {
  paymentId: string;
  amount: number;
  dueAt: string;
  paidAt: string | null;
  status: PaymentStatus;
}

/** Детали кредита */
export interface CreditDetailResponse {
  creditId: string;
  clientId: string;
  accountNumber: string;
  amount: number;
  remainingDebt: number;
  interestRate: number;
  tariffName: string;
  status: CreditStatus;
  issuedAt: string;
  nextPaymentAt: string | null;
  payments: CreditPaymentDto[];
}

/** Кредитный рейтинг (поля точно по BFF CreditRatingResponse) */
export interface CreditRatingResponse {
  score: number;
  ratingLabel: string;
  overduePaymentsCount: number;
  totalCredits: number;
  activeCredits: number;
  closedCredits: number;
  calculatedAt: string;
}

/** POST /bff/client/credits */
export interface TakeCreditRequest {
  accountNumber: string;
  tariffId: string;  // UUID
  amount: number;
}

/** Ответ на взятие кредита */
export interface TakeCreditResponse {
  creditId: string;
  accountNumber: string;
  amount: number;
  remainingDebt: number;
  interestRate: number;
  tariffName: string;
  status: CreditStatus;
  issuedAt: string;
}

/** POST /bff/client/credits/{id}/repay — BFF принимает только amount */
export interface RepayCreditRequest {
  amount: number;
}

export interface RepayCreditResponse {
  creditId: string;
  remainingDebt: number;
  status: CreditStatus;
}
