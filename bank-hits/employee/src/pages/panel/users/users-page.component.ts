import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import {
  UsersPageRecord,
  UsersPageRole,
} from '../../../app/application/use-cases/employee-users-use-cases.service';
import { UsersPageFacade } from '../../../app/application/facades/users-page.facade';

@Component({
  selector: 'employee-users-page',
  standalone: true,
  imports: [BasicModalComponent, FormsModule],
  providers: [UsersPageFacade],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.scss',
})
export class UsersPageComponent implements OnInit {
  private readonly facade = inject(UsersPageFacade);

  protected readonly addModalOpen = this.facade.addModalOpen;
  protected readonly blockModalOpen = this.facade.blockModalOpen;
  protected readonly actionInProgress = this.facade.actionInProgress;
  protected readonly errorText = this.facade.errorText;
  protected readonly clientUsers = this.facade.clientUsers;
  protected readonly employeeUsers = this.facade.employeeUsers;
  protected readonly blockTarget = this.facade.blockTarget;

  ngOnInit(): void {
    this.facade.init();
  }

  protected get newUser(): { name: string; email: string; role: UsersPageRole; status: string } {
    return this.facade.newUser;
  }

  protected addUser(): void {
    this.facade.addUser();
  }

  protected openBlockUser(user: UsersPageRecord, role: UsersPageRole): void {
    this.facade.openBlockUser(user, role);
  }

  protected confirmBlockUser(): void {
    this.facade.confirmBlockUser();
  }

  protected closeBlockModal(): void {
    this.facade.closeBlockModal();
  }

  protected isBlocked(user: UsersPageRecord): boolean {
    return this.facade.isBlocked(user);
  }
}
