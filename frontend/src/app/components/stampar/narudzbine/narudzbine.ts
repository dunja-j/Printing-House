import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { NAZIV_STATUSA, StatusNarudzbine } from '../../../models/narudzbina';
import { StamparNarudzbina } from '../../../models/stampar-narudzbina';
import { StamparService } from '../../../services/stampar.service';

type Kolona = 'id' | 'datumNarudzbine' | 'imeKlijenta' | 'brojStavki' | 'ukupanIznos' | 'status';

@Component({
  selector: 'app-stampar-narudzbine',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './narudzbine.html',
  styleUrl: './narudzbine.css'
})
export class StamparNarudzbine {
  private servis = inject(StamparService);

  narudzbine = signal<StamparNarudzbina[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal<number | null>(null);
  razvijene = signal<Set<number>>(new Set());

  kolonaSortiranja = signal<Kolona>('datumNarudzbine');
  rastuce = signal(false);

  sortirane = computed(() => {
    const kolona = this.kolonaSortiranja();
    const smer = this.rastuce() ? 1 : -1;

    return [...this.narudzbine()].sort((a, b) => {
      const x = a[kolona];
      const y = b[kolona];
      if (typeof x === 'number' && typeof y === 'number') {
        return (x - y) * smer;
      }
      return String(x).localeCompare(String(y), 'sr') * smer;
    });
  });

  /** Narudžbine koje čekaju reakciju štamparije. */
  brojNaCekanju = computed(() => this.narudzbine().filter((n) => n.sledeciStatus).length);

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.narudzbine().subscribe({
      next: (lista) => {
        this.narudzbine.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Narudžbine trenutno nisu dostupne.');
      }
    });
  }

  pomeriStatus(n: StamparNarudzbina): void {
    if (!n.sledeciStatus) {
      return;
    }
    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(n.id);

    this.servis.promeniStatus(n.id, n.sledeciStatus).subscribe({
      next: (izmenjena) => {
        this.narudzbine.update((lista) =>
          lista.map((x) => (x.id === izmenjena.id ? izmenjena : x))
        );
        this.radiSe.set(null);
        this.uspeh.set(
          `Narudžbina #${izmenjena.id} je prebačena u status "${this.nazivStatusa(izmenjena.status)}".`
        );
      },
      error: (err) => {
        this.radiSe.set(null);
        this.greska.set(err?.error?.poruka ?? 'Status nije promenjen.');
        this.ucitaj();
      }
    });
  }

  prikaziStavke(id: number): void {
    this.razvijene.update((skup) => {
      const novi = new Set(skup);
      if (!novi.delete(id)) {
        novi.add(id);
      }
      return novi;
    });
  }

  razvijena(id: number): boolean {
    return this.razvijene().has(id);
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

  nazivStatusa(status: StatusNarudzbine): string {
    return NAZIV_STATUSA[status];
  }

  natpisDugmeta(n: StamparNarudzbina): string {
    return n.sledeciStatus === 'u_stampi' ? 'PRIMI U ŠTAMPU' : 'OZNAČI KAO ISPORUČENO';
  }
}
