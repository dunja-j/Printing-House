import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { NAZIV_STATUSA, Narudzbina, StatusNarudzbine } from '../../../models/narudzbina';
import { NarudzbinaService } from '../../../services/narudzbina.service';

type Kolona = 'id' | 'datumNarudzbine' | 'nazivStamparije' | 'brojStavki' | 'ukupanIznos' | 'status';

@Component({
  selector: 'app-narudzbine',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './narudzbine.html',
  styleUrl: './narudzbine.css'
})
export class Narudzbine {
  private servis = inject(NarudzbinaService);

  narudzbine = signal<Narudzbina[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  otkazivanjeId = signal<number | null>(null);
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

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.mojeNarudzbine().subscribe({
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

  otkazi(n: Narudzbina): void {
    if (!confirm(`Da li sigurno želite da otkažete narudžbinu #${n.id}?`)) {
      return;
    }
    this.greska.set(null);
    this.uspeh.set(null);
    this.otkazivanjeId.set(n.id);

    this.servis.otkazi(n.id).subscribe({
      next: (izmenjena) => {
        this.narudzbine.update((lista) =>
          lista.map((x) => (x.id === izmenjena.id ? izmenjena : x))
        );
        this.otkazivanjeId.set(null);
        this.uspeh.set(`Narudžbina #${izmenjena.id} je otkazana.`);
      },
      error: (err) => {
        this.otkazivanjeId.set(null);
        this.greska.set(err?.error?.poruka ?? 'Narudžbina nije mogla da se otkaže.');
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
}
