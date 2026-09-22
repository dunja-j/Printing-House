import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { DetaljiProizvoda, DetaljiZaKlijenta } from '../../../models/javno';
import { AuthService } from '../../../services/auth.service';
import { JavnoService } from '../../../services/javno.service';
import { KorpaService } from '../../../services/korpa.service';

@Component({
  selector: 'app-detalji-proizvoda',
  imports: [RouterLink, DecimalPipe, FormsModule],
  templateUrl: './detalji-proizvoda.html',
  styleUrl: './detalji-proizvoda.css'
})
export class DetaljiProizvodaKomponenta {
  private javno = inject(JavnoService);
  private auth = inject(AuthService);
  private korpa = inject(KorpaService);
  private router = inject(Router);

  proizvod = signal<DetaljiProizvoda | null>(null);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  slanje = signal(false);

  izabranaBoja = signal<string | null>(null);
  izabranaUslugaId = signal<number | null>(null);
  kolicina = signal(1);
  tekstZaStampu = '';

  /** Klijent dobija prošireni prikaz (boje, usluge štampe); ostali skraćeni. */
  jeKlijent = computed(() => {
    const tip = this.auth.korisnik()?.tip;
    return tip === 'klijent_fizicko' || tip === 'klijent_pravno';
  });

  prosireni = computed(() =>
    this.jeKlijent() ? (this.proizvod() as DetaljiZaKlijenta | null) : null
  );

  naStanju = computed(() => (this.proizvod()?.kolicinaNaLageru ?? 0) > 0);

  izabranaUsluga = computed(() =>
    this.prosireni()?.uslugeStampe.find((u) => u.id === this.izabranaUslugaId()) ?? null
  );

  cenaPoKomadu = computed(
    () => (this.proizvod()?.jedinicnaCena ?? 0) + (this.izabranaUsluga()?.dodatnaCenaPoKomadu ?? 0)
  );

  ukupnaCena = computed(() => this.cenaPoKomadu() * Math.max(this.kolicina(), 0));

  constructor() {
    const id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.greska.set('Proizvod nije pronađen.');
      return;
    }

    const zahtev = this.jeKlijent() ? this.javno.detaljiZaKlijenta(id) : this.javno.detalji(id);
    zahtev.subscribe({
      next: (p) => {
        this.proizvod.set(p);
        const k = this.prosireni();
        // ako postoji samo jedna opcija, nema razloga terati korisnika da je bira
        if (k?.dostupneBoje.length === 1) {
          this.izabranaBoja.set(k.dostupneBoje[0]);
        }
        if (k?.uslugeStampe.length === 1) {
          this.izabranaUslugaId.set(k.uslugeStampe[0].id);
        }
      },
      error: (err) => this.greska.set(err?.error?.poruka ?? 'Proizvod nije pronađen.')
    });
  }

  dodajUKorpu(): void {
    const p = this.prosireni();
    if (!p) {
      return;
    }

    if (p.dostupneBoje.length && !this.izabranaBoja()) {
      this.postaviGresku('Izaberite boju.');
      return;
    }
    if (p.uslugeStampe.length && this.izabranaUslugaId() === null) {
      this.postaviGresku('Izaberite uslugu štampe.');
      return;
    }
    if (!Number.isInteger(this.kolicina()) || this.kolicina() < 1) {
      this.postaviGresku('Količina mora biti najmanje 1.');
      return;
    }
    if (this.kolicina() > p.kolicinaNaLageru) {
      this.postaviGresku(`Na stanju je samo ${p.kolicinaNaLageru} kom.`);
      return;
    }

    this.greska.set(null);
    this.uspeh.set(null);
    this.slanje.set(true);

    this.korpa
      .dodaj({
        proizvodId: p.id,
        uslugaId: this.izabranaUslugaId(),
        kolicina: this.kolicina(),
        boja: this.izabranaBoja(),
        tekstZaStampu: this.tekstZaStampu.trim() || null
      })
      .subscribe({
        next: () => {
          this.slanje.set(false);
          this.uspeh.set('Proizvod je dodat u korpu.');
        },
        error: (err) => {
          this.slanje.set(false);
          this.greska.set(err?.error?.poruka ?? 'Dodavanje u korpu nije uspelo.');
        }
      });
  }

  naKorpu(): void {
    this.router.navigate(['/klijent/korpa']);
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }

  /** Cena komada sa izabranom uslugom štampe — osnovna cena + doplata. */
  cenaSaUslugom(dodatna: number): number {
    return (this.proizvod()?.jedinicnaCena ?? 0) + dodatna;
  }

  dimenzije(sirina: number | null, visina: number | null): string {
    if (sirina === null && visina === null) {
      return '—';
    }
    return `${sirina ?? '?'} × ${visina ?? '?'} mm`;
  }

  private postaviGresku(poruka: string): void {
    this.uspeh.set(null);
    this.greska.set(poruka);
  }
}
