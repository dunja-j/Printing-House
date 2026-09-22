import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { DetaljiProizvoda, DetaljiZaKlijenta } from '../../../models/javno';
import { AuthService } from '../../../services/auth.service';
import { JavnoService } from '../../../services/javno.service';

@Component({
  selector: 'app-detalji-proizvoda',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './detalji-proizvoda.html',
  styleUrl: './detalji-proizvoda.css'
})
export class DetaljiProizvodaKomponenta {
  private javno = inject(JavnoService);
  private auth = inject(AuthService);

  proizvod = signal<DetaljiProizvoda | null>(null);
  greska = signal<string | null>(null);

  /** Klijent dobija prošireni prikaz (boje, usluge štampe); ostali skraćeni. */
  jeKlijent = computed(() => {
    const tip = this.auth.korisnik()?.tip;
    return tip === 'klijent_fizicko' || tip === 'klijent_pravno';
  });

  prosireni = computed(() =>
    this.jeKlijent() ? (this.proizvod() as DetaljiZaKlijenta | null) : null
  );

  constructor() {
    const id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.greska.set('Proizvod nije pronađen.');
      return;
    }

    const zahtev = this.jeKlijent() ? this.javno.detaljiZaKlijenta(id) : this.javno.detalji(id);
    zahtev.subscribe({
      next: (p) => this.proizvod.set(p),
      error: (err) => this.greska.set(err?.error?.poruka ?? 'Proizvod nije pronađen.')
    });
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }

  /** Cena komada sa izabranom uslugom štampe — osnovna cena + doplata. */
  cenaSaUslugom(dodatna: number): number {
    return (this.proizvod()?.jedinicnaCena ?? 0) + dodatna;
  }

  dimenzije(sirina: number | null, visina: number | null): string {
    if (sirina === null && visina === null) {
      return '—';
    }
    return `${sirina ?? '?'} × ${visina ?? '?'} mm`;
  }
}
