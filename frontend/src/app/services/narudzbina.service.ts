import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { ArhivaProizvod, VrednostOcene } from '../models/arhiva';
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

  potvrdiPrijem(id: number): Observable<Narudzbina> {
    return this.http.post<Narudzbina>(
      `${environment.apiUrl}/klijent/narudzbine/${id}/primljeno`,
      {},
      { withCredentials: true }
    );
  }

  arhiva(): Observable<ArhivaProizvod[]> {
    return this.http.get<ArhivaProizvod[]>(`${environment.apiUrl}/klijent/arhiva`, {
      withCredentials: true
    });
  }

  oceni(proizvodId: number, vrednost: VrednostOcene): Observable<ArhivaProizvod> {
    return this.http.post<ArhivaProizvod>(
      `${environment.apiUrl}/klijent/proizvodi/${proizvodId}/ocena`,
      null,
      { params: new HttpParams().set('vrednost', vrednost), withCredentials: true }
    );
  }

  komentarisi(proizvodId: number, tekst: string): Observable<ArhivaProizvod> {
    return this.http.post<ArhivaProizvod>(
      `${environment.apiUrl}/klijent/proizvodi/${proizvodId}/komentar`,
      { tekst },
      { withCredentials: true }
    );
  }
}
