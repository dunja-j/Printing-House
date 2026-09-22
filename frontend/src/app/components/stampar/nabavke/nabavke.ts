import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { NabavkaZaStampara } from '../../../models/nabavka';
import { NabavkaService } from '../../../services/nabavka.service';

@Component({
  selector: 'app-stampar-nabavke',
  imports: [RouterLink, DatePipe, DecimalPipe, FormsModule],
  templateUrl: './nabavke.html',
  styleUrl: './nabavke.css'
})
export class StamparNabavke {
  private servis = inject(NabavkaService);

  nabavke = signal<NabavkaZaStampara[]>([]);
  greska = signal<string | null>(null);
  uspeh = signal<string | null>(null);
  ucitavanje = signal(true);
  radiSe = signal<number | null>(null);

  cene: Record<number, number | null> = {};

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.otvorene().subscribe({
      next: (lista) => {
        this.nabavke.set(lista);
        this.ucitavanje.set(false);
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Javne nabavke trenutno nisu dostupne.');
      }
    });
  }

  posalji(n: NabavkaZaStampara): void {
    this.greska.set(null);
    this.uspeh.set(null);

    const cena = this.cene[n.id];
    if (cena == null || cena <= 0) {
      this.greska.set('Unesite ukupnu cenu ponude veću od nule.');
      return;
    }
    if (!confirm(`Poslati ponudu od ${cena.toFixed(2)} RSD za nabavku #${n.id}? Ponuda se ne menja.`)) {
      return;
    }

    this.radiSe.set(n.id);
    this.servis.posaljiPonudu(n.id, cena).subscribe({
      next: (izmenjena) => {
        this.radiSe.set(null);
        this.nabavke.update((lista) => lista.map((x) => (x.id === izmenjena.id ? izmenjena : x)));
        this.uspeh.set(`Ponuda za nabavku #${n.id} je poslata.`);
      },
      error: (err) => {
        this.radiSe.set(null);
        this.greska.set(err?.error?.poruka ?? 'Ponuda nije poslata.');
      }
    });
  }

  preostalo(sekundi: number): string {
    const minuti = Math.floor(sekundi / 60);
    const ostatak = sekundi % 60;
    return `${minuti} min ${ostatak.toString().padStart(2, '0')} s`;
  }
}
