import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NotificationCenterComponent } from '../../../shared/frontend-core';

@Component({
  imports: [RouterModule, NotificationCenterComponent],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
