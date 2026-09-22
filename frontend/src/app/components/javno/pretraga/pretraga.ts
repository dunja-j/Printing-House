import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Kategorija, PretragaRed } from '../../../models/javno';
import { JavnoService } from '../../../services/javno.service';

type Kolona = 'naziv' | 'nazivStamparije' | 'grad' | 'kategorija' | 'jedinicnaCena' | 'brojLajkova';

@Component({
  selector: 'app-pretraga',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './pretraga.html',
  styleUrl: './pretraga.css'
})
export class Pretraga {
  private javno = inject(JavnoService);

  naziv = '';
  kategorijaId: number | null = null;

  kategorije = signal<Kategorija[]>([]);
  rezultati = signal<PretragaRed[]>([]);
  pretrazeno = signal(false);
  ucitavanje = signal(false);
  greska = signal<string | null>(null);

  kolonaSortiranja = signal<Kolona>('naziv');
  rastuce = signal(true);

  /** Sortiranje se radi na klijentu — rezultat je već u memoriji, nema potrebe za novim upitom. */
  sortiraniRezultati = computed(() => {
    const kolona = this.kolonaSortiranja();
    const smer = this.rastuce() ? 1 : -1;

    return [...this.rezultati()].sort((a, b) => {
      const x = a[kolona];
      const y = b[kolona];
      if (typeof x === 'number' && typeof y === 'number') {
        return (x - y) * smer;
      }
      return String(x ?? '').localeCompare(String(y ?? ''), 'sr') * smer;
    });
  });

  constructor() {
    this.javno.kategorije().subscribe({
      next: (k) => this.kategorije.set(k),
      error: () => this.greska.set('Kategorije trenutno nisu dostupne.')
    });
    this.pretrazi();
  }

  pretrazi(): void {
    this.greska.set(null);
    this.ucitavanje.set(true);

    this.javno.pretraga(this.naziv, this.kategorijaId).subscribe({
      next: (redovi) => {
        this.rezultati.set(redovi);
        this.pretrazeno.set(true);
        this.ucitavanje.set(false);
      },
      error: () => {
        this.ucitavanje.set(false);
        this.greska.set('Pretraga trenutno nije dostupna. Pokušajte kasnije.');
      }
    });
  }

  ponisti(): void {
    this.naziv = '';
    this.kategorijaId = null;
    this.pretrazi();
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
}
