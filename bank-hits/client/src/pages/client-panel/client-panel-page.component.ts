import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'client-panel-page',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './client-panel-page.component.html',
  styleUrl: './client-panel-page.component.scss',
})
export class ClientPanelPageComponent {}
