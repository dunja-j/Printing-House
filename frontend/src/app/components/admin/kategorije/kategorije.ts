import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { KategorijaSaPotkategorijama } from '../../../models/stampar';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-kategorije',
  imports: [RouterLink, FormsModule],
  templateUrl: './kategorije.html',
  styleUrl: './kategorije.css'
})
export class AdminKategorije {
  private servis = inject(AdminService);

  kategorije = signal<KategorijaSaPotkategorijama[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  cuva = signal(false);

  novaKategorija = '';
  otvorena = signal<number | null>(null);
  novaPotkategorija = '';

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.kategorije().subscribe({
      next: (lista) => {
        this.kategorije.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Kategorije trenutno nisu dostupne.');
      }
    });
  }

  dodajKategoriju(): void {
    this.greska.set(null);
    this.uspeh.set(null);

    const naziv = this.novaKategorija.trim();
    if (!naziv) {
      this.greska.set('Unesite naziv kategorije.');
      return;
    }

    this.cuva.set(true);
    this.servis.dodajKategoriju(naziv).subscribe({
      next: (k) => {
        this.cuva.set(false);
        this.novaKategorija = '';
        this.uspeh.set(`Kategorija "${k.naziv}" je dodata.`);
        this.ucitaj();
      },
      error: (err) => {
        this.cuva.set(false);
        this.greska.set(err?.error?.poruka ?? 'Kategorija nije dodata.');
      }
    });
  }

  otvoriFormu(kategorijaId: number): void {
    this.greska.set(null);
    this.uspeh.set(null);
    this.novaPotkategorija = '';
    this.otvorena.update((trenutna) => (trenutna === kategorijaId ? null : kategorijaId));
  }

  dodajPotkategoriju(kategorijaId: number): void {
    this.greska.set(null);
    this.uspeh.set(null);

    const naziv = this.novaPotkategorija.trim();
    if (!naziv) {
      this.greska.set('Unesite naziv potkategorije.');
      return;
    }

    this.cuva.set(true);
    this.servis.dodajPotkategoriju(kategorijaId, naziv).subscribe({
      next: (p) => {
        this.cuva.set(false);
        this.novaPotkategorija = '';
        this.otvorena.set(null);
        this.uspeh.set(`Potkategorija "${p.naziv}" je dodata.`);
        this.ucitaj();
      },
      error: (err) => {
        this.cuva.set(false);
        this.greska.set(err?.error?.poruka ?? 'Potkategorija nije dodata.');
      }
    });
  }
}
