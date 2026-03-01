export type AccountId = number | string;
export type UserId = number | string;

export interface CreateAccountRequest {
  clientId: UserId;
  accountNumber: string;
  balance: number;
  status: string;
}

export interface AccountDto {
  clientId: UserId;
  accountNumber: string;
  balance: number;
  status: string;
}

export interface AccountListQuery {
  filterUserId?: UserId;
  status?: string;
  minBalance?: number;
  maxBalance?: number;
  page?: number;
  size?: number;
  sort?: string[];
}

export interface AccountListResponse {
  content: AccountDto[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}

export interface AccountOperationDto {
  id: AccountId;
  accountId: AccountId;
  accountNumber: string;
  operationType: string;
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  status: string;
  description: string;
  createdAt: string;
}

export interface AccountOperationResultDto {
  operation: AccountOperationDto;
  account: AccountDto;
  message: string;
}

