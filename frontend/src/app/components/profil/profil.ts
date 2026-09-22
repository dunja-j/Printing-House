import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Korisnik } from '../../models/korisnik';
import { ProfilService } from '../../services/profil.service';
import { NarudzbineTabela } from '../klijent/narudzbine/narudzbine';

@Component({
  selector: 'app-profil',
  imports: [FormsModule, NarudzbineTabela],
  templateUrl: './profil.html',
  styleUrl: './profil.css'
})
export class Profil {
  private profilServis = inject(ProfilService);

  korisnik = signal<Korisnik | null>(null);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  cuvanje = signal(false);

  /** Menja se posle uploada da pregledac ne prikaze staru sliku iz kesa. */
  private verzijaSlike = signal(Date.now());

  ime = '';
  prezime = '';
  telefon = '';
  mejl = '';
  grad = '';
  nazivInstitucije = '';
  adresaSedista = '';
  maticniBroj = '';
  pib = '';

  institucija = computed(() => {
    const tip = this.korisnik()?.tip;
    return tip === 'klijent_pravno' || tip === 'stampar';
  });

  jeKlijent = computed(() => {
    const tip = this.korisnik()?.tip;
    return tip === 'klijent_fizicko' || tip === 'klijent_pravno';
  });

  izabranaSlika = signal<File | null>(null);
  pregledSlike = signal<string | null>(null);

  constructor() {
    this.profilServis.ucitaj().subscribe({
      next: (k) => this.popuni(k),
      error: (err) => this.greska.set(err?.error?.poruka ?? 'Profil nije mogao da se učita.')
    });
  }

  sacuvaj(): void {
    const poruka = this.prvaGreska();
    if (poruka) {
      this.greska.set(poruka);
      this.uspeh.set(null);
      return;
    }
    this.greska.set(null);
    this.uspeh.set(null);
    this.cuvanje.set(true);

    this.profilServis
      .azuriraj({
        ime: this.ime.trim(),
        prezime: this.prezime.trim(),
        telefon: this.telefon.trim(),
        mejl: this.mejl.trim(),
        grad: this.grad.trim(),
        ...(this.institucija()
          ? {
              nazivInstitucije: this.nazivInstitucije.trim(),
              adresaSedista: this.adresaSedista.trim(),
              maticniBroj: this.maticniBroj.trim(),
              pib: this.pib.trim()
            }
          : {})
      })
      .subscribe({
        next: (k) => {
          this.popuni(k);
          this.cuvanje.set(false);
          this.uspeh.set('Podaci su sačuvani.');
        },
        error: (err) => {
          this.cuvanje.set(false);
          this.greska.set(err?.error?.poruka ?? 'Izmene nisu sačuvane. Pokušajte ponovo.');
        }
      });
  }

  izaberiSliku(dogadjaj: Event): void {
    const fajl = (dogadjaj.target as HTMLInputElement).files?.[0] ?? null;
    this.greska.set(null);
    this.uspeh.set(null);
    this.izabranaSlika.set(fajl);
    this.pregledSlike.set(fajl ? URL.createObjectURL(fajl) : null);
  }

  posaljiSliku(): void {
    const fajl = this.izabranaSlika();
    if (!fajl) {
      this.greska.set('Prvo izaberite sliku.');
      return;
    }
    this.greska.set(null);
    this.uspeh.set(null);
    this.cuvanje.set(true);

    this.profilServis.promeniSliku(fajl).subscribe({
      next: (k) => {
        this.popuni(k);
        this.izabranaSlika.set(null);
        this.pregledSlike.set(null);
        this.cuvanje.set(false);
        this.uspeh.set('Profilna slika je promenjena.');
      },
      error: (err) => {
        this.cuvanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Slika nije mogla da se sačuva.');
      }
    });
  }

  /** Vremenski pecat sprecava da pregledac prikaze staru sliku iz kesa. */
  slika(): string {
    const k = this.korisnik();
    return this.profilServis.slikaProfila(k?.slikaUrl ?? null) + '?v=' + this.verzijaSlike();
  }

  nazivTipa(): string {
    switch (this.korisnik()?.tip) {
      case 'klijent_fizicko':
        return 'Klijent – fizičko lice';
      case 'klijent_pravno':
        return 'Klijent – pravno lice';
      case 'stampar':
        return 'Štamparija';
      case 'administrator':
        return 'Administrator';
      default:
        return '';
    }
  }

  private popuni(k: Korisnik): void {
    this.korisnik.set(k);
    this.ime = k.ime;
    this.prezime = k.prezime;
    this.telefon = k.telefon ?? '';
    this.mejl = k.mejl;
    this.grad = k.grad ?? '';
    this.nazivInstitucije = k.nazivInstitucije ?? '';
    this.adresaSedista = k.adresaSedista ?? '';
    this.maticniBroj = k.maticniBroj ?? '';
    this.pib = k.pib ?? '';
    this.verzijaSlike.set(Date.now());
  }

  /** Iste provere postoje i na serveru — ovo je samo brza povratna informacija. */
  private prvaGreska(): string | null {
    if (!this.ime.trim() || !this.prezime.trim()) {
      return 'Ime i prezime su obavezni.';
    }
    if (!/^[0-9+\s\-/()]{6,30}$/.test(this.telefon.trim())) {
      return 'Kontakt telefon nije u ispravnom formatu.';
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(this.mejl.trim())) {
      return 'Mejl adresa nije u ispravnom formatu.';
    }
    if (this.institucija()) {
      if (!this.nazivInstitucije.trim()) {
        return 'Naziv institucije je obavezan.';
      }
      if (!this.adresaSedista.trim()) {
        return 'Adresa sedišta je obavezna.';
      }
      if (!this.grad.trim()) {
        return 'Grad je obavezan.';
      }
      if (!/^\d{8}$/.test(this.maticniBroj.trim())) {
        return 'Matični broj mora imati tačno 8 cifara.';
      }
      if (!/^[1-9]\d{8}$/.test(this.pib.trim())) {
        return 'PIB mora imati 9 cifara i ne sme počinjati nulom.';
      }
    }
    return null;
  }
}
