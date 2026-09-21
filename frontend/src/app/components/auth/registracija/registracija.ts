import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { TipKorisnika } from '../../../models/korisnik';
import { RegistracijaPodaci } from '../../../models/registracija';
import { AuthService } from '../../../services/auth.service';

/** Isti regex kao na backendu (videti RegistracijaRequest.REGEX_LOZINKE). */
const REGEX_LOZINKE = /^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d])[A-Za-z]\S{7,11}$/;
const REGEX_KOR_IMENA = /^[A-Za-z0-9._-]{3,45}$/;
const REGEX_MEJLA = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
const REGEX_TELEFONA = /^[0-9+\s\-/()]{6,30}$/;

@Component({
  selector: 'app-registracija',
  imports: [FormsModule, RouterLink],
  templateUrl: './registracija.html',
  styleUrl: './registracija.css'
})
export class Registracija {
  private auth = inject(AuthService);
  private router = inject(Router);

  korak = signal<1 | 2>(1);
  greska = signal<string | null>(null);
  ucitavanje = signal(false);

  tip = signal<TipKorisnika>('klijent_fizicko');
  korIme = '';
  lozinka = '';
  potvrdaLozinke = '';
  ime = '';
  prezime = '';
  telefon = '';
  mejl = '';
  nazivInstitucije = '';
  adresaSedista = '';
  grad = '';
  maticniBroj = '';
  pib = '';

  /** Pravna lica i štamparije unose i podatke o instituciji. */
  institucija = computed(() => this.tip() !== 'klijent_fizicko');

  izabranaSlika = signal<File | null>(null);
  pregledSlike = signal<string | null>(null);

  promeniTip(noviTip: string): void {
    this.tip.set(noviTip as TipKorisnika);
    this.greska.set(null);
  }

  posalji(): void {
    const poruka = this.prvaGreska();
    if (poruka) {
      this.greska.set(poruka);
      return;
    }
    this.greska.set(null);
    this.ucitavanje.set(true);

    this.auth.registracija(this.podaci()).subscribe({
      next: () => {
        this.ucitavanje.set(false);
        this.korak.set(2);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Registracija nije uspela. Pokušajte ponovo.');
      }
    });
  }

  izaberiSliku(dogadjaj: Event): void {
    const fajl = (dogadjaj.target as HTMLInputElement).files?.[0] ?? null;
    this.greska.set(null);
    this.izabranaSlika.set(fajl);
    this.pregledSlike.set(fajl ? URL.createObjectURL(fajl) : null);
  }

  posaljiSliku(): void {
    const fajl = this.izabranaSlika();
    if (!fajl) {
      this.greska.set('Izaberite sliku ili preskočite ovaj korak.');
      return;
    }
    this.greska.set(null);
    this.ucitavanje.set(true);

    this.auth.registracijaSlika(fajl).subscribe({
      next: () => {
        this.ucitavanje.set(false);
        this.zavrsi();
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Slika nije mogla da se sačuva.');
      }
    });
  }

  zavrsi(): void {
    this.router.navigate(['/login'], {
      queryParams: { poruka: 'Registracija je primljena i čeka odobrenje administratora.' }
    });
  }

  private podaci(): RegistracijaPodaci {
    const osnovno: RegistracijaPodaci = {
      korIme: this.korIme.trim(),
      lozinka: this.lozinka,
      ime: this.ime.trim(),
      prezime: this.prezime.trim(),
      telefon: this.telefon.trim(),
      mejl: this.mejl.trim(),
      tip: this.tip(),
      grad: this.grad.trim()
    };
    if (!this.institucija()) {
      return osnovno;
    }
    return {
      ...osnovno,
      nazivInstitucije: this.nazivInstitucije.trim(),
      adresaSedista: this.adresaSedista.trim(),
      maticniBroj: this.maticniBroj.trim(),
      pib: this.pib.trim()
    };
  }

  /** Iste provere postoje i na serveru — ovo je samo brza povratna informacija. */
  private prvaGreska(): string | null {
    if (!REGEX_KOR_IMENA.test(this.korIme.trim())) {
      return 'Korisničko ime sme da sadrži samo slova, cifre, tačku, donju crtu i crticu (3-45 karaktera).';
    }
    if (!REGEX_LOZINKE.test(this.lozinka)) {
      return 'Lozinka mora imati 8-12 karaktera, počinjati slovom i sadržati bar jedno veliko slovo, jednu cifru i jedan specijalni karakter.';
    }
    if (this.lozinka !== this.potvrdaLozinke) {
      return 'Lozinka i potvrda lozinke se ne poklapaju.';
    }
    if (!this.ime.trim() || !this.prezime.trim()) {
      return 'Ime i prezime su obavezni.';
    }
    if (!REGEX_TELEFONA.test(this.telefon.trim())) {
      return 'Kontakt telefon nije u ispravnom formatu.';
    }
    if (!REGEX_MEJLA.test(this.mejl.trim())) {
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
