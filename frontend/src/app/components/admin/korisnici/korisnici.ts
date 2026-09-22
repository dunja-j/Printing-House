import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  AdminKorisnik,
  NAZIV_STATUSA_REGISTRACIJE,
  NAZIV_TIPA,
  StatusRegistracije
} from '../../../models/admin';
import { TipKorisnika } from '../../../models/korisnik';
import { AdminService } from '../../../services/admin.service';
import { ProfilPodaci } from '../../../services/profil.service';

type Kolona = 'korIme' | 'ime' | 'mejl' | 'tip' | 'statusRegistracije';

@Component({
  selector: 'app-admin-korisnici',
  imports: [RouterLink, FormsModule],
  templateUrl: './korisnici.html',
  styleUrl: './korisnici.css'
})
export class AdminKorisnici {
  private servis = inject(AdminService);

  korisnici = signal<AdminKorisnik[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal<string | null>(null);

  izmena = signal<string | null>(null);
  forma: ProfilPodaci = { ime: '', prezime: '', telefon: '', mejl: '' };

  kolonaSortiranja = signal<Kolona>('korIme');
  rastuce = signal(true);

  sortirani = computed(() => {
    const kolona = this.kolonaSortiranja();
    const smer = this.rastuce() ? 1 : -1;
    return [...this.korisnici()].sort(
      (a, b) => String(a[kolona]).localeCompare(String(b[kolona]), 'sr') * smer
    );
  });

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.korisnici().subscribe({
      next: (lista) => {
        this.korisnici.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Nalozi trenutno nisu dostupni.');
      }
    });
  }

  zapocniIzmenu(k: AdminKorisnik): void {
    this.greska.set(null);
    this.uspeh.set(null);

    if (this.izmena() === k.korIme) {
      this.izmena.set(null);
      return;
    }

    this.izmena.set(k.korIme);
    this.forma = {
      ime: k.ime,
      prezime: k.prezime,
      telefon: k.telefon ?? '',
      mejl: k.mejl,
      grad: k.grad ?? '',
      nazivInstitucije: k.nazivInstitucije ?? '',
      adresaSedista: k.adresaSedista ?? '',
      maticniBroj: k.maticniBroj ?? '',
      pib: k.pib ?? ''
    };
  }

  sacuvaj(k: AdminKorisnik): void {
    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(k.korIme);

    this.servis.azuriraj(k.korIme, this.forma).subscribe({
      next: (izmenjen) => {
        this.korisnici.update((lista) =>
          lista.map((x) => (x.korIme === izmenjen.korIme ? izmenjen : x))
        );
        this.radiSe.set(null);
        this.izmena.set(null);
        this.uspeh.set(`Nalog "${izmenjen.korIme}" je izmenjen.`);
      },
      error: (err) => {
        this.radiSe.set(null);
        this.greska.set(err?.error?.poruka ?? 'Izmene nisu sačuvane.');
      }
    });
  }

  obrisi(k: AdminKorisnik): void {
    if (!confirm(`Trajno obrisati nalog "${k.korIme}"?`)) {
      return;
    }
    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(k.korIme);

    this.servis.obrisi(k.korIme).subscribe({
      next: (rezultat) => {
        this.korisnici.update((lista) => lista.filter((x) => x.korIme !== k.korIme));
        this.radiSe.set(null);
        this.uspeh.set(rezultat.poruka);
      },
      error: (err) => {
        this.radiSe.set(null);
        this.greska.set(err?.error?.poruka ?? 'Nalog nije obrisan.');
      }
    });
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

  nazivTipa(tip: TipKorisnika): string {
    return NAZIV_TIPA[tip];
  }

  nazivStatusa(status: StatusRegistracije): string {
    return NAZIV_STATUSA_REGISTRACIJE[status];
  }
}
