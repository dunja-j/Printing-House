import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { DetaljiProizvoda, JavnaPocetna, Kategorija, PretragaRed } from '../models/javno';

@Injectable({ providedIn: 'root' })
export class JavnoService {
  private http = inject(HttpClient);

  pocetna(): Observable<JavnaPocetna> {
    return this.http.get<JavnaPocetna>(`${environment.apiUrl}/javno/pocetna`);
  }

  /** Samo kategorije u kojima ima aktivnih proizvoda na stanju. */
  kategorije(): Observable<Kategorija[]> {
    return this.http.get<Kategorija[]>(`${environment.apiUrl}/javno/kategorije`);
  }

  pretraga(naziv: string, kategorijaId: number | null): Observable<PretragaRed[]> {
    let parametri = new HttpParams();
    if (naziv.trim()) {
      parametri = parametri.set('naziv', naziv.trim());
    }
    if (kategorijaId !== null) {
      parametri = parametri.set('kategorijaId', kategorijaId);
    }
    return this.http.get<PretragaRed[]>(`${environment.apiUrl}/javno/proizvodi`, {
      params: parametri
    });
  }

  detalji(id: number): Observable<DetaljiProizvoda> {
    return this.http.get<DetaljiProizvoda>(`${environment.apiUrl}/javno/proizvodi/${id}`);
  }

  /** Puna putanja do slike proizvoda, sa rezervnom slikom ako je nema. */
  slikaProizvoda(naziv: string | null): string {
    return `${environment.fileUrl}/proizvodi/${naziv ?? 'default_product_image.jpg'}`;
  }
}
