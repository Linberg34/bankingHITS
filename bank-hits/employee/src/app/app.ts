import { Component } from '@angular/core';
import { LoginPageComponent } from '../pages/login/login-page.component';

@Component({
  imports: [LoginPageComponent],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
