import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { KategorijaSaPotkategorijama, NovaUslugaStampe } from '../../../models/stampar';
import { StamparService } from '../../../services/stampar.service';

@Component({
  selector: 'app-novi-proizvod',
  imports: [FormsModule, RouterLink],
  templateUrl: './novi-proizvod.html',
  styleUrl: './novi-proizvod.css'
})
export class NoviProizvodKomponenta {
  private servis = inject(StamparService);
  private router = inject(Router);

  kategorije = signal<KategorijaSaPotkategorijama[]>([]);
  greska = signal<string | null>(null);
  slanje = signal(false);

  sifra = '';
  naziv = '';
  opis = '';
  kategorijaId = signal<number | null>(null);
  potkategorijaId: number | null = null;
  jedinicnaCena: number | null = null;
  kolicinaNaLageru: number | null = null;
  unosBoje = '';
  boje = signal<string[]>([]);
  usluge = signal<NovaUslugaStampe[]>([]);

  potkategorije = computed(
    () => this.kategorije().find((k) => k.id === this.kategorijaId())?.potkategorije ?? []
  );

  constructor() {
    this.servis.kategorije().subscribe({
      next: (k) => this.kategorije.set(k),
      error: () => this.greska.set('Kategorije trenutno nisu dostupne.')
    });
  }

  promeniKategoriju(id: number | null): void {
    this.kategorijaId.set(id);
    this.potkategorijaId = null;
  }

  dodajBoju(): void {
    const boja = this.unosBoje.trim();
    if (!boja) {
      return;
    }
    if (this.boje().some((b) => b.toLowerCase() === boja.toLowerCase())) {
      this.greska.set(`Boja "${boja}" je već dodata.`);
      return;
    }
    this.greska.set(null);
    this.boje.update((lista) => [...lista, boja]);
    this.unosBoje = '';
  }

  ukloniBoju(boja: string): void {
    this.boje.update((lista) => lista.filter((b) => b !== boja));
  }

  dodajUslugu(): void {
    this.usluge.update((lista) => [
      ...lista,
      { tipStampe: '', dodatnaCenaPoKomadu: null, maxSirinaMm: null, maxVisinaMm: null }
    ]);
  }

  ukloniUslugu(indeks: number): void {
    this.usluge.update((lista) => lista.filter((_, i) => i !== indeks));
  }

  sacuvaj(): void {
    const poruka = this.prvaGreska();
    if (poruka) {
      this.greska.set(poruka);
      return;
    }
    this.greska.set(null);
    this.slanje.set(true);

    this.servis
      .dodaj({
        sifra: this.sifra.trim(),
        naziv: this.naziv.trim(),
        opis: this.opis.trim() || null,
        kategorijaId: this.kategorijaId(),
        potkategorijaId: this.potkategorijaId,
        jedinicnaCena: this.jedinicnaCena,
        kolicinaNaLageru: this.kolicinaNaLageru,
        dostupneBoje: this.boje(),
        uslugeStampe: this.usluge().map((u) => ({
          ...u,
          tipStampe: u.tipStampe.trim(),
          dodatnaCenaPoKomadu: u.dodatnaCenaPoKomadu ?? 0
        }))
      })
      .subscribe({
        next: () => {
          this.slanje.set(false);
          this.router.navigate(['/stampar/proizvodi']);
        },
        error: (err) => {
          this.slanje.set(false);
          this.greska.set(err?.error?.poruka ?? 'Proizvod nije sačuvan. Pokušajte ponovo.');
        }
      });
  }

  /** Iste provere postoje i na serveru — ovo je samo brza povratna informacija. */
  private prvaGreska(): string | null {
    if (!/^[A-Za-z0-9._-]{2,30}$/.test(this.sifra.trim())) {
      return 'Šifra sme da sadrži samo slova, cifre, tačku, donju crtu i crticu (2-30 karaktera).';
    }
    if (!this.naziv.trim()) {
      return 'Naziv proizvoda je obavezan.';
    }
    if (this.kategorijaId() === null) {
      return 'Izaberite kategoriju.';
    }
    if (this.jedinicnaCena === null || this.jedinicnaCena <= 0) {
      return 'Jedinična cena mora biti veća od nule.';
    }
    if (this.kolicinaNaLageru === null || this.kolicinaNaLageru < 0) {
      return 'Količina na lageru ne može biti negativna.';
    }
    for (const u of this.usluge()) {
      if (!u.tipStampe.trim()) {
        return 'Svaka usluga štampe mora imati naziv tipa štampe.';
      }
      if (u.dodatnaCenaPoKomadu !== null && u.dodatnaCenaPoKomadu < 0) {
        return 'Doplata za uslugu štampe ne može biti negativna.';
      }
    }
    return null;
  }
}
