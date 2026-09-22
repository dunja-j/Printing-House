import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ArhivaStavka, VrednostOcene } from '../../../models/arhiva';
import { JavnoService } from '../../../services/javno.service';
import { NarudzbinaService } from '../../../services/narudzbina.service';

type Kolona = 'datumNarudzbine' | 'naziv' | 'kolicina' | 'nazivStamparije';

@Component({
  selector: 'app-arhiva',
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './arhiva.html',
  styleUrl: './arhiva.css'
})
export class Arhiva {
  private servis = inject(NarudzbinaService);
  private javno = inject(JavnoService);

  stavke = signal<ArhivaStavka[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal<number | null>(null);

  kolonaSortiranja = signal<Kolona>('datumNarudzbine');
  rastuce = signal(false);

  /** Tekst komentara po stavci, da svaka kartica ima svoje polje. */
  noviKomentari: Record<number, string> = {};

  sortirane = computed(() => {
    const kolona = this.kolonaSortiranja();
    const smer = this.rastuce() ? 1 : -1;

    return [...this.stavke()].sort((a, b) => {
      const x = a[kolona];
      const y = b[kolona];
      if (typeof x === 'number' && typeof y === 'number') {
        return (x - y) * smer;
      }
      return String(x).localeCompare(String(y), 'sr') * smer;
    });
  });

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.arhiva().subscribe({
      next: (lista) => {
        this.stavke.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Arhiva trenutno nije dostupna.');
      }
    });
  }

  potvrdiPrijem(s: ArhivaStavka): void {
    this.pripremi(s.stavkaId);
    this.servis.potvrdiPrijem(s.narudzbinaId).subscribe({
      next: () => {
        this.radiSe.set(null);
        this.uspeh.set(
          `Prijem narudžbine #${s.narudzbinaId} je potvrđen — proizvode sada možete oceniti.`
        );
        this.ucitaj();
      },
      error: (err) => this.neuspeh(err)
    });
  }

  oceni(s: ArhivaStavka, vrednost: VrednostOcene): void {
    this.pripremi(s.stavkaId);
    this.servis.oceni(s.proizvodId, vrednost).subscribe({
      next: (lista) => this.osvezi(lista),
      error: (err) => this.neuspeh(err)
    });
  }

  posaljiKomentar(s: ArhivaStavka): void {
    const tekst = (this.noviKomentari[s.stavkaId] ?? '').trim();
    if (!tekst) {
      this.uspeh.set(null);
      this.greska.set('Komentar ne sme biti prazan.');
      return;
    }

    this.pripremi(s.stavkaId);
    this.servis.komentarisi(s.proizvodId, tekst).subscribe({
      next: (lista) => {
        this.noviKomentari[s.stavkaId] = '';
        this.osvezi(lista);
        this.uspeh.set('Komentar je objavljen.');
      },
      error: (err) => this.neuspeh(err)
    });
  }

  promeniSortiranje(kolona: Kolona): void {
    if (this.kolonaSortiranja() === kolona) {
      this.rastuce.update((v) => !v);
    } else {
      this.kolonaSortiranja.set(kolona);
      this.rastuce.set(kolona !== 'datumNarudzbine');
    }
  }

  strelica(kolona: Kolona): string {
    if (this.kolonaSortiranja() !== kolona) {
      return '';
    }
    return this.rastuce() ? '▲' : '▼';
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }

  private pripremi(stavkaId: number): void {
    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(stavkaId);
  }

  private osvezi(lista: ArhivaStavka[]): void {
    this.stavke.set(lista);
    this.radiSe.set(null);
  }

  private neuspeh(err: { error?: { poruka?: string } }): void {
    this.radiSe.set(null);
    this.greska.set(err?.error?.poruka ?? 'Radnja nije uspela.');
  }
}
