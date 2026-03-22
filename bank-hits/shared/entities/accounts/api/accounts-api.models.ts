export type AccountCurrency = 'RUB' | 'USD' | 'EUR';
export type AccountStatus = 'ACTIVE' | 'CLOSED';
export type OperationType =
  | 'DEPOSIT'
  | 'WITHDRAWAL'
  | 'TRANSFER_IN'
  | 'TRANSFER_OUT'
  | 'CREDIT_ISSUE'
  | 'CREDIT_PAYMENT';
export type OperationStatus = 'SUCCESS' | 'PENDING' | 'FAILED';

/** Счёт, возвращаемый client-bff */
export interface AccountDto {
  clientId: string;
  accountNumber: string;
  currency: AccountCurrency;
  balance: number;
  status: AccountStatus;
}

/** Обёртка GET /bff/client/accounts */
export interface AccountListResponse {
  accounts: AccountDto[];
}

/** Операция, возвращаемая client-bff */
export interface OperationDto {
  operationId: string;
  type: OperationType;
  amount: number;
  currency: AccountCurrency;
  accountNumber: string;
  status: OperationStatus;
  description: string | null;
  createdAt: string;
}

/** Обёртка GET /bff/client/accounts/{num}/operations */
export interface OperationPageResponse {
  content: OperationDto[];
  page: number;
  size: number;
  totalElements: number;
}

/** POST /bff/client/operations/deposit | withdraw */
export interface DepositRequest {
  accountNumber: string;
  amount: number;
}

export interface WithdrawRequest {
  accountNumber: string;
  amount: number;
}

/** POST /bff/client/operations/transfer */
export interface TransferRequest {
  fromAccountNumber: string;
  toAccountNumber: string;
  amount: number;
}

/** Ответ на операцию через Kafka (async) */
export interface OperationAcceptedResponse {
  operationId: string;
  status: OperationStatus;
}

/** POST /bff/client/accounts */
export interface OpenAccountRequest {
  currency: AccountCurrency;
}

export interface OpenAccountResponse {
  clientId: string;
  accountNumber: string;
  currency: AccountCurrency;
  balance: number;
  status: AccountStatus;
}

/** DELETE /bff/client/accounts/{accountId} */
export interface CloseAccountResponse {
  accountNumber: string;
  status: AccountStatus;
}

/** Параметры запроса списка счетов (для employee BFF) */
export interface AccountListQuery {
  page?: number;
  size?: number;
}

/** Employee BFF: все счета с владельцами */
export interface AccountWithOwnerDto {
  clientId: string;
  accountNumber: string;
  currency: AccountCurrency;
  balance: number;
  status: AccountStatus;
  clientName?: string;
}

export interface AllAccountsPageResponse {
  accounts: AccountWithOwnerDto[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}
