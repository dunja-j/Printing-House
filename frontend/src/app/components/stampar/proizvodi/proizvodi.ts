import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { StamparProizvod } from '../../../models/stampar';
import { JavnoService } from '../../../services/javno.service';
import { StamparService } from '../../../services/stampar.service';

type Kolona = 'sifra' | 'naziv' | 'kategorija' | 'jedinicnaCena' | 'kolicinaNaLageru';

@Component({
  selector: 'app-stampar-proizvodi',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './proizvodi.html',
  styleUrl: './proizvodi.css'
})
export class StamparProizvodi {
  private servis = inject(StamparService);
  private javno = inject(JavnoService);

  proizvodi = signal<StamparProizvod[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal<number | null>(null);

  kolonaSortiranja = signal<Kolona>('naziv');
  rastuce = signal(true);

  sortirani = computed(() => {
    const kolona = this.kolonaSortiranja();
    const smer = this.rastuce() ? 1 : -1;

    return [...this.proizvodi()].sort((a, b) => {
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
    this.servis.proizvodi().subscribe({
      next: (lista) => {
        this.proizvodi.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Proizvodi trenutno nisu dostupni.');
      }
    });
  }

  sacuvajKolicinu(p: StamparProizvod, vrednost: string): void {
    const kolicina = Number(vrednost);
    if (!Number.isInteger(kolicina) || kolicina < 0) {
      this.postaviGresku('Količina mora biti ceo broj, 0 ili veći.');
      return;
    }
    if (kolicina === p.kolicinaNaLageru) {
      return;
    }
    this.izvrsi(p.id, this.servis.promeniKolicinu(p.id, kolicina),
      `Količina za "${p.naziv}" je promenjena na ${kolicina}.`);
  }

  promeniDostupnost(p: StamparProizvod): void {
    this.izvrsi(p.id, this.servis.promeniDostupnost(p.id, !p.aktivan),
      p.aktivan ? `"${p.naziv}" više nije u ponudi.` : `"${p.naziv}" je vraćen u ponudu.`);
  }

  promeniSliku(p: StamparProizvod, dogadjaj: Event): void {
    const fajl = (dogadjaj.target as HTMLInputElement).files?.[0];
    if (!fajl) {
      return;
    }
    this.izvrsi(p.id, this.servis.promeniSliku(p.id, fajl), `Slika za "${p.naziv}" je promenjena.`);
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }

  promeniSortiranje(kolona: Kolona): void {
    if (this.kolonaSortiranja() === kolona) {
      this.rastuce.update((v) => !v);
    } else {
      this.kolonaSortiranja.set(kolona);
      this.rastuce.set(true);
    }
  }

  strelica(kolona: Kolona): string {
    if (this.kolonaSortiranja() !== kolona) {
      return '';
    }
    return this.rastuce() ? '▲' : '▼';
  }

  private izvrsi(
    id: number,
    zahtev: ReturnType<StamparService['promeniKolicinu']>,
    poruka: string
  ): void {
    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(id);

    zahtev.subscribe({
      next: (izmenjen) => {
        this.proizvodi.update((lista) => lista.map((x) => (x.id === izmenjen.id ? izmenjen : x)));
        this.radiSe.set(null);
        this.uspeh.set(poruka);
      },
      error: (err: { error?: { poruka?: string } }) => {
        this.radiSe.set(null);
        this.greska.set(err?.error?.poruka ?? 'Izmena nije sačuvana.');
        this.ucitaj();
      }
    });
  }

  private postaviGresku(poruka: string): void {
    this.uspeh.set(null);
    this.greska.set(poruka);
  }
}
