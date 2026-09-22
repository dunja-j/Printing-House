import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AdminKorisnik, NAZIV_TIPA } from '../../../models/admin';
import { TipKorisnika } from '../../../models/korisnik';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-zahtevi',
  imports: [RouterLink, DatePipe],
  templateUrl: './zahtevi.html',
  styleUrl: './zahtevi.css'
})
export class AdminZahtevi {
  private servis = inject(AdminService);

  zahtevi = signal<AdminKorisnik[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal<string | null>(null);

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.zahtevi().subscribe({
      next: (lista) => {
        this.zahtevi.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Zahtevi trenutno nisu dostupni.');
      }
    });
  }

  odluci(k: AdminKorisnik, prihvati: boolean): void {
    const pitanje = prihvati
      ? `Odobriti registraciju naloga "${k.korIme}"?`
      : `Odbiti registraciju naloga "${k.korIme}"?`;
    if (!confirm(pitanje)) {
      return;
    }

    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(k.korIme);

    this.servis.odluci(k.korIme, prihvati).subscribe({
      next: () => {
        this.zahtevi.update((lista) => lista.filter((x) => x.korIme !== k.korIme));
        this.radiSe.set(null);
        this.uspeh.set(
          prihvati
            ? `Nalog "${k.korIme}" je odobren i može da se prijavi.`
            : `Zahtev naloga "${k.korIme}" je odbijen.`
        );
      },
      error: (err) => {
        this.radiSe.set(null);
        this.greska.set(err?.error?.poruka ?? 'Odluka nije sačuvana.');
        this.ucitaj();
      }
    });
  }

  nazivTipa(tip: TipKorisnika): string {
    return NAZIV_TIPA[tip];
  }
}
