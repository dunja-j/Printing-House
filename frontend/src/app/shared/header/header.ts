import { Component, computed, effect, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../services/auth.service';
import { KorpaService } from '../../services/korpa.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {
  private auth = inject(AuthService);
  private korpa = inject(KorpaService);
  private router = inject(Router);

  korisnik = this.auth.korisnik;
  brojUKorpi = this.korpa.brojStavki;

  jeKlijent = computed(() => {
    const tip = this.korisnik()?.tip;
    return tip === 'klijent_fizicko' || tip === 'klijent_pravno';
  });

  constructor() {
    // korpa se ucitava cim se zna da je prijavljen klijent, da bi brojac bio tacan
    effect(() => {
      if (this.jeKlijent()) {
        this.korpa.ucitaj().subscribe({ error: () => this.korpa.zaboravi() });
      } else {
        this.korpa.zaboravi();
      }
    });
  }

  odjaviSe(): void {
    this.auth.odjava().subscribe({
      // i u slucaju greske korisnik je lokalno odjavljen, pa uvek vodimo na pocetnu
      next: () => this.router.navigate(['/']),
      error: () => this.router.navigate(['/'])
    });
  }
}
