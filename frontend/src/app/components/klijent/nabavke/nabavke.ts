import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Nabavka, NAZIV_STATUSA_NABAVKE, StatusNabavke } from '../../../models/nabavka';
import { NabavkaService } from '../../../services/nabavka.service';

@Component({
  selector: 'app-klijent-nabavke',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './nabavke.html',
  styleUrl: './nabavke.css'
})
export class KlijentNabavke {
  private servis = inject(NabavkaService);

  nabavke = signal<Nabavka[]>([]);
  greska = signal<string | null>(null);
  ucitavanje = signal(true);

  constructor() {
    this.ucitaj();
  }

  ucitaj(): void {
    this.ucitavanje.set(true);
    this.servis.mojeNabavke().subscribe({
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

  nazivStatusa(status: StatusNabavke): string {
    return NAZIV_STATUSA_NABAVKE[status];
  }

  preostalo(sekundi: number): string {
    const minuti = Math.floor(sekundi / 60);
    const ostatak = sekundi % 60;
    return `${minuti} min ${ostatak.toString().padStart(2, '0')} s`;
  }
}
