import { Injectable, OnDestroy } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { EMPTY, Observable, merge } from 'rxjs';
import { map } from 'rxjs';
import { AUTH_TOKEN_STORAGE_KEY } from './users-auth-token.interceptor';

export type WsEventType = 'OPERATION_ADDED' | 'OPERATION_UPDATED' | 'BALANCE_UPDATED';

/** Событие об операции — BFF посылает WsOperationEvent */
export interface WsOperationEvent {
  type: WsEventType;
  operation: {
    operationId: string;
    type: string;
    amount: number;
    currency: string;
    accountNumber: string;
    status: string;
    description: string | null;
    createdAt: string;
  };
}

/** Событие об изменении баланса — BFF посылает WsBalanceEvent */
export interface WsBalanceEvent {
  type: 'BALANCE_UPDATED';
  accountId: string;
  newBalance: number;
  currency: string;
}

export type WsAccountEvent = WsOperationEvent | WsBalanceEvent;

/**
 * Подключается к BFF WebSocket и подписывается на события по конкретным счетам.
 * BFF отправляет на /user/queue/operations/{accountId}.
 */
@Injectable({
  providedIn: 'root',
})
export class OperationsWsService implements OnDestroy {
  private stompClient: RxStomp | null = null;

  private getToken(): string | null {
    try {
      return globalThis.localStorage?.getItem(AUTH_TOKEN_STORAGE_KEY) ?? null;
    } catch {
      return null;
    }
  }

  /**
   * Подключается к WebSocket и возвращает merged observable событий по всем переданным счетам.
   * @param wsUrl  например 'http://localhost:8084/ws'
   * @param accountIds  UUID счетов для подписки
   */
  connect(wsUrl: string, accountIds: string[]): Observable<WsAccountEvent> {
    this.disconnect();

    if (!accountIds.length) {
      return EMPTY;
    }

    const token = this.getToken();
    const stompConfig: RxStompConfig = {
      brokerURL: wsUrl.replace(/^http/, 'ws'),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 5000,
    };

    this.stompClient = new RxStomp();
    this.stompClient.configure(stompConfig);
    this.stompClient.activate();

    const subscriptions = accountIds.map((id) =>
      this.stompClient!.watch(`/user/queue/operations/${id}`).pipe(
        map((message) => JSON.parse(message.body) as WsAccountEvent)
      )
    );

    return merge(...subscriptions);
  }

  disconnect(): void {
    if (this.stompClient) {
      void this.stompClient.deactivate();
      this.stompClient = null;
    }
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
