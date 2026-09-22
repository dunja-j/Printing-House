import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ArhivaProizvod, VrednostOcene } from '../../../models/arhiva';
import { JavnoService } from '../../../services/javno.service';
import { NarudzbinaService } from '../../../services/narudzbina.service';

@Component({
  selector: 'app-arhiva',
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './arhiva.html',
  styleUrl: './arhiva.css'
})
export class Arhiva {
  private servis = inject(NarudzbinaService);
  private javno = inject(JavnoService);

  proizvodi = signal<ArhivaProizvod[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal<number | null>(null);

  /** Tekst komentara po proizvodu, da svaki proizvod ima svoje polje. */
  noviKomentari: Record<number, string> = {};

  constructor() {
    this.servis.arhiva().subscribe({
      next: (lista) => {
        this.proizvodi.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Arhiva trenutno nije dostupna.');
      }
    });
  }

  oceni(p: ArhivaProizvod, vrednost: VrednostOcene): void {
    this.pripremi(p.proizvodId);
    this.servis.oceni(p.proizvodId, vrednost).subscribe({
      next: (izmenjen) => this.zameni(izmenjen),
      error: (err) => this.neuspeh(err)
    });
  }

  posaljiKomentar(p: ArhivaProizvod): void {
    const tekst = (this.noviKomentari[p.proizvodId] ?? '').trim();
    if (!tekst) {
      this.uspeh.set(null);
      this.greska.set('Komentar ne sme biti prazan.');
      return;
    }

    this.pripremi(p.proizvodId);
    this.servis.komentarisi(p.proizvodId, tekst).subscribe({
      next: (izmenjen) => {
        this.noviKomentari[p.proizvodId] = '';
        this.zameni(izmenjen);
        this.uspeh.set('Komentar je objavljen.');
      },
      error: (err) => this.neuspeh(err)
    });
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }

  private pripremi(id: number): void {
    this.greska.set(null);
    this.uspeh.set(null);
    this.radiSe.set(id);
  }

  private zameni(izmenjen: ArhivaProizvod): void {
    this.proizvodi.update((lista) =>
      lista.map((x) => (x.proizvodId === izmenjen.proizvodId ? izmenjen : x))
    );
    this.radiSe.set(null);
  }

  private neuspeh(err: { error?: { poruka?: string } }): void {
    this.radiSe.set(null);
    this.greska.set(err?.error?.poruka ?? 'Radnja nije uspela.');
  }
}
