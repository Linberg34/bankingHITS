import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AccountsApiService, type AccountDto, type OperationDto } from 'shared/entities/accounts';
import { CreditsApiService, type CreditSummaryDto, type TakeCreditResponse } from 'shared/entities/credits';
import { TariffsApiService, type TariffDto } from 'shared/entities/tariffs';
import type { Account, Credit, CreditRating, CreditTariff, Transaction } from '../../core/models/client.types';

@Injectable({ providedIn: 'root' })
export class ClientBankingRequestService {
  private readonly accountsApi = inject(AccountsApiService);
  private readonly tariffsApi = inject(TariffsApiService);
  private readonly creditsApi = inject(CreditsApiService);

  getMyAccounts(): Observable<Account[]> {
    return this.accountsApi.getMyAccounts().pipe(
      map((resp) => resp.accounts.map(mapAccountDtoToAccount))
    );
  }

  getOperations(accountNumber: string): Observable<Transaction[]> {
    return this.accountsApi
      .getOperations(accountNumber)
      .pipe(map((resp) => resp.content.map(mapOperationDtoToTransaction)));
  }

  openAccount(currency: 'RUB' | 'USD' | 'EUR' = 'RUB'): Observable<Account> {
    return this.accountsApi.openAccount({ currency }).pipe(map(mapAccountDtoToAccount));
  }

  deposit(accountNumber: string, amount: number): Observable<void> {
    return this.accountsApi.deposit({ accountNumber, amount }).pipe(map(() => void 0));
  }

  withdraw(accountNumber: string, amount: number): Observable<void> {
    return this.accountsApi.withdraw({ accountNumber, amount }).pipe(map(() => void 0));
  }

  transfer(fromAccountNumber: string, toAccountNumber: string, amount: number): Observable<void> {
    return this.accountsApi.transfer({ fromAccountNumber, toAccountNumber, amount }).pipe(map(() => void 0));
  }

  closeAccount(accountNumber: string): Observable<void> {
    return this.accountsApi.closeAccount(accountNumber).pipe(map(() => void 0));
  }

  getCreditTariffs(): Observable<CreditTariff[]> {
    return this.tariffsApi.getTariffs().pipe(map((items) => items.map(mapTariffDtoToTariff)));
  }

  getMyCredits(): Observable<Credit[]> {
    return this.creditsApi.getMyCredits().pipe(map((resp) => resp.credits.map(mapCreditDtoToCredit)));
  }

  takeCredit(accountNumber: string, tariffId: string, amount: number): Observable<Credit> {
    return this.creditsApi.takeCredit({ accountNumber, tariffId, amount }).pipe(map(mapTakeCreditResponseToCredit));
  }

  repayCreditFull(creditId: string, fullAmount: number): Observable<void> {
    return this.creditsApi.repayCredit(creditId, { amount: fullAmount }).pipe(map(() => void 0));
  }

  repayCreditPartial(creditId: string, amount: number): Observable<void> {
    return this.creditsApi.repayCredit(creditId, { amount }).pipe(map(() => void 0));
  }

  getCreditRating(): Observable<CreditRating> {
    return this.creditsApi.getCreditRating().pipe(
      map((dto) => ({
        score: dto.score,
        ratingLabel: dto.ratingLabel,
        overduePaymentsCount: dto.overduePaymentsCount,
        totalCredits: dto.totalCredits,
        activeCredits: dto.activeCredits,
        closedCredits: dto.closedCredits,
        calculatedAt: dto.calculatedAt,
      }))
    );
  }
}

function mapAccountDtoToAccount(dto: AccountDto): Account {
  const status = dto.status === 'ACTIVE' ? 'active' : 'closed';
  return {
    id: dto.accountNumber,
    uuid: dto.id,
    clientId: dto.clientId,
    accountNumber: dto.accountNumber,
    balance: dto.balance,
    currency: dto.currency,
    status,
    createdAt: '',
  };
}

function mapOperationDtoToTransaction(op: OperationDto): Transaction {
  return {
    id: op.operationId,
    accountId: op.accountNumber,
    type: mapOperationType(op.type),
    amount: Math.abs(op.amount),
    description: op.description ?? '',
    createdAt: op.createdAt,
  };
}

function mapOperationType(value: string): Transaction['type'] {
  if (value === 'DEPOSIT') return 'deposit';
  if (value === 'WITHDRAW') return 'withdrawal';
  if (value === 'TRANSFER_IN' || value === 'TRANSFER_OUT') return 'transfer';
  if (value === 'CREDIT_ISSUE') return 'credit_issue';
  if (value === 'CREDIT_PAYMENT') return 'credit_payment';
  return 'deposit';
}

function mapTariffDtoToTariff(dto: TariffDto): CreditTariff {
  return {
    id: dto.tariffId,
    name: dto.name,
    interestRate: dto.interestRate,
    createdAt: '',
    createdBy: '',
  };
}

function mapCreditDtoToCredit(dto: CreditSummaryDto): Credit {
  return {
    id: dto.creditId,
    clientId: '',
    accountId: dto.accountNumber,
    tariffId: dto.tariffName,
    amount: dto.amount,
    remainingAmount: dto.remainingDebt,
    interestRate: dto.interestRate,
    status: mapCreditStatus(dto.status),
    issueDate: '',
    nextPaymentDate: dto.nextPaymentAt ?? '',
    dailyPayment: 0,
  };
}

function mapTakeCreditResponseToCredit(dto: TakeCreditResponse): Credit {
  return {
    id: dto.creditId,
    clientId: '',
    accountId: dto.accountNumber,
    tariffId: dto.tariffName,
    amount: dto.amount,
    remainingAmount: dto.remainingDebt,
    interestRate: dto.interestRate,
    status: mapCreditStatus(dto.status),
    issueDate: dto.issuedAt,
    nextPaymentDate: '',
    dailyPayment: 0,
  };
}

function mapCreditStatus(status: string): 'active' | 'paid' | 'overdue' {
  if (status === 'CLOSED') return 'paid';
  if (status === 'OVERDUE') return 'overdue';
  return 'active';
}
