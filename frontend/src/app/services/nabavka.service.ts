import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Nabavka, NabavkaZaStampara } from '../models/nabavka';

@Injectable({ providedIn: 'root' })
export class NabavkaService {
  private http = inject(HttpClient);
  private url = environment.apiUrl;

  objavi(): Observable<Nabavka> {
    return this.http.post<Nabavka>(`${this.url}/klijent/nabavke`, null, {
      withCredentials: true
    });
  }

  mojeNabavke(): Observable<Nabavka[]> {
    return this.http.get<Nabavka[]>(`${this.url}/klijent/nabavke`, { withCredentials: true });
  }

  otvorene(): Observable<NabavkaZaStampara[]> {
    return this.http.get<NabavkaZaStampara[]>(`${this.url}/stampar/nabavke`, {
      withCredentials: true
    });
  }

  posaljiPonudu(nabavkaId: number, ukupnaCena: number): Observable<NabavkaZaStampara> {
    return this.http.post<NabavkaZaStampara>(
      `${this.url}/stampar/nabavke/${nabavkaId}/ponuda`,
      { ukupnaCena },
      { withCredentials: true }
    );
  }
}
