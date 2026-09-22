import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { DetaljiProizvoda } from '../../../models/javno';
import { JavnoService } from '../../../services/javno.service';

@Component({
  selector: 'app-detalji-proizvoda',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './detalji-proizvoda.html',
  styleUrl: './detalji-proizvoda.css'
})
export class DetaljiProizvodaKomponenta {
  private javno = inject(JavnoService);

  proizvod = signal<DetaljiProizvoda | null>(null);
  greska = signal<string | null>(null);

  constructor() {
    const id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.greska.set('Proizvod nije pronađen.');
      return;
    }

    this.javno.detalji(id).subscribe({
      next: (p) => this.proizvod.set(p),
      error: (err) => this.greska.set(err?.error?.poruka ?? 'Proizvod nije pronađen.')
    });
  }

  slika(naziv: string | null): string {
    return this.javno.slikaProizvoda(naziv);
  }
}
