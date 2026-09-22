import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Narudzbina } from '../models/narudzbina';

@Injectable({ providedIn: 'root' })
export class NarudzbinaService {
  private http = inject(HttpClient);

  mojeNarudzbine(): Observable<Narudzbina[]> {
    return this.http.get<Narudzbina[]>(`${environment.apiUrl}/klijent/narudzbine`, {
      withCredentials: true
    });
  }

  otkazi(id: number): Observable<Narudzbina> {
    return this.http.post<Narudzbina>(
      `${environment.apiUrl}/klijent/narudzbine/${id}/otkazi`,
      {},
      { withCredentials: true }
    );
  }
}
