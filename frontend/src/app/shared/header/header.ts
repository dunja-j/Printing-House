import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {
  private auth = inject(AuthService);
  private router = inject(Router);

  korisnik = this.auth.korisnik;

  odjaviSe(): void {
    this.auth.odjava().subscribe({
      // i u slucaju greske korisnik je lokalno odjavljen, pa uvek vodimo na pocetnu
      next: () => this.router.navigate(['/']),
      error: () => this.router.navigate(['/'])
    });
  }
}
