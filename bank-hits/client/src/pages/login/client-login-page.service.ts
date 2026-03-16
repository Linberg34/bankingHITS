import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ClientSessionUseCasesService, type LoginResult } from '../../app/application/use-cases/client-session-use-cases.service';

@Injectable({ providedIn: 'root' })
export class ClientLoginPageService {
  private readonly sessionUseCases = inject(ClientSessionUseCasesService);

  login(email: string): Observable<LoginResult> {
    return this.sessionUseCases.login(email);
  }
}

