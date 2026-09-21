import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { TopProizvod } from '../../../models/javno';
import { AuthService } from '../../../services/auth.service';
import { JavnoService } from '../../../services/javno.service';

@Component({
  selector: 'app-pocetna',
  imports: [RouterLink],
  templateUrl: './pocetna.html',
  styleUrl: './pocetna.css'
})
export class Pocetna {
  private auth = inject(AuthService);
  private javno = inject(JavnoService);

  korisnik = this.auth.korisnik;
  mojaRuta = computed(() => {
    const k = this.korisnik();
    return k ? this.auth.pocetnaRuta(k.tip) : '/login';
  });

  brojStamparija = signal<number | null>(null);
  topProizvodi = signal<TopProizvod[]>([]);
  greska = signal<string | null>(null);

  constructor() {
    this.javno.pocetna().subscribe({
      next: (podaci) => {
        this.brojStamparija.set(podaci.brojStamparija);
        this.topProizvodi.set(podaci.topProizvodi);
      },
      error: () => this.greska.set('Podaci trenutno nisu dostupni. Pokušajte kasnije.')
    });
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }
}
