import { Component } from '@angular/core';
import { BasicModalComponent } from '../../../../../shared/ui/basic-modal';
import { CLIENT_USERS, EMPLOYEE_USERS } from '../../../data-domain/users/model/users.model';

@Component({
  selector: 'employee-users-page',
  standalone: true,
  imports: [BasicModalComponent],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.scss',
})
export class UsersPageComponent {
  addModalOpen = false;
  blockModalOpen = false;
  clientUsers = CLIENT_USERS;
  employeeUsers = EMPLOYEE_USERS;
}
