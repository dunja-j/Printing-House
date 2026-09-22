import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Korpa, StavkaKorpe } from '../../../models/korpa';
import { JavnoService } from '../../../services/javno.service';
import { KorpaService } from '../../../services/korpa.service';

@Component({
  selector: 'app-korpa',
  imports: [RouterLink, DecimalPipe, FormsModule],
  templateUrl: './korpa.html',
  styleUrl: './korpa.css'
})
export class KorpaKomponenta {
  private servis = inject(KorpaService);
  private javno = inject(JavnoService);

  korpa = this.servis.korpa;
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal(false);
  poslateNarudzbine = signal<number[]>([]);

  constructor() {
    this.servis.ucitaj().subscribe({
      next: () => this.ucitavanje.set(false),
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Korpa trenutno nije dostupna.');
      }
    });
  }

  promeniKolicinu(s: StavkaKorpe, vrednost: string): void {
    const kolicina = Number(vrednost);
    if (!Number.isInteger(kolicina) || kolicina < 1) {
      this.postaviGresku('Količina mora biti najmanje 1.');
      return;
    }
    this.izvrsi(this.servis.promeniKolicinu(s.id, kolicina));
  }

  ukloni(s: StavkaKorpe): void {
    this.izvrsi(this.servis.ukloni(s.id), `"${s.nazivProizvoda}" je uklonjen iz korpe.`);
  }

  isprazni(): void {
    if (!confirm('Da li sigurno želite da ispraznite korpu?')) {
      return;
    }
    this.izvrsi(this.servis.isprazni(), 'Korpa je ispražnjena.');
  }

  zakljuci(): void {
    const k = this.korpa();
    if (!k || !k.ukupnoStavki) {
      return;
    }
    if (!confirm(`Potvrđujete narudžbinu na iznos od ${k.ukupanIznos.toFixed(2)} RSD?`)) {
      return;
    }

    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(true);

    this.servis.zakljuci().subscribe({
      next: (rezultat) => {
        this.radiSe.set(false);
        this.poslateNarudzbine.set(rezultat.narudzbine);
        this.uspeh.set(rezultat.poruka);
        // ponovo ucitaj da se prikaze prazna korpa umesto praznog ekrana
        this.servis.ucitaj().subscribe();
      },
      error: (err) => {
        this.radiSe.set(false);
        this.greska.set(err?.error?.poruka ?? 'Narudžbina nije mogla da se zatvori.');
      }
    });
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }

  private izvrsi(zahtev: ReturnType<KorpaService['ukloni']>, poruka?: string): void {
    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(true);

    zahtev.subscribe({
      next: (_k: Korpa) => {
        this.radiSe.set(false);
        if (poruka) {
          this.uspeh.set(poruka);
        }
      },
      error: (err: { error?: { poruka?: string } }) => {
        this.radiSe.set(false);
        this.greska.set(err?.error?.poruka ?? 'Izmena nije sačuvana.');
        // posle neuspeha stanje na serveru je merodavno
        this.servis.ucitaj().subscribe();
      }
    });
  }

  private postaviGresku(poruka: string): void {
    this.uspeh.set(null);
    this.greska.set(poruka);
  }
}
