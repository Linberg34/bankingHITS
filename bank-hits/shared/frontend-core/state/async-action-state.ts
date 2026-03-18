export type AsyncActionStatus = 'idle' | 'loading' | 'success' | 'error';

export interface AsyncActionState {
  status: AsyncActionStatus;
  message?: string;
}

export const IDLE_ACTION_STATE: AsyncActionState = { status: 'idle' };

