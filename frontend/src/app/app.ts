import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { AuthService } from './services/auth.service';
import { Footer } from './shared/footer/footer';
import { Header } from './shared/header/header';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor() {
    // localStorage je samo kopija — pri svakom pokretanju se proverava prava sesija na serveru
    inject(AuthService).osveziSesiju().subscribe();
  }
}
