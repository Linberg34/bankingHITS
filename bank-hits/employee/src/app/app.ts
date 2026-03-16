import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NotificationCenterComponent } from '../../../shared/frontend-core';

@Component({
  imports: [RouterOutlet, NotificationCenterComponent],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
