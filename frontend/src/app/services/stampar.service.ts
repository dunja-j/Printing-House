import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { StatusNarudzbine } from '../models/narudzbina';
import { StamparNarudzbina } from '../models/stampar-narudzbina';
import {
  KategorijaSaPotkategorijama,
  NoviProizvod,
  StamparProizvod
} from '../models/stampar';

@Injectable({ providedIn: 'root' })
export class StamparService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/stampar`;

  kategorije(): Observable<KategorijaSaPotkategorijama[]> {
    return this.http.get<KategorijaSaPotkategorijama[]>(`${this.url}/kategorije`, {
      withCredentials: true
    });
  }

  proizvodi(): Observable<StamparProizvod[]> {
    return this.http.get<StamparProizvod[]>(`${this.url}/proizvodi`, { withCredentials: true });
  }

  dodaj(proizvod: NoviProizvod): Observable<StamparProizvod> {
    return this.http.post<StamparProizvod>(`${this.url}/proizvodi`, proizvod, {
      withCredentials: true
    });
  }

  promeniKolicinu(id: number, kolicina: number): Observable<StamparProizvod> {
    return this.http.put<StamparProizvod>(`${this.url}/proizvodi/${id}/kolicina`, null, {
      params: new HttpParams().set('kolicina', kolicina),
      withCredentials: true
    });
  }

  promeniDostupnost(id: number, aktivan: boolean): Observable<StamparProizvod> {
    return this.http.put<StamparProizvod>(`${this.url}/proizvodi/${id}/dostupnost`, null, {
      params: new HttpParams().set('aktivan', aktivan),
      withCredentials: true
    });
  }

  promeniSliku(id: number, slika: File): Observable<StamparProizvod> {
    const telo = new FormData();
    telo.append('slika', slika);
    return this.http.post<StamparProizvod>(`${this.url}/proizvodi/${id}/slika`, telo, {
      withCredentials: true
    });
  }

  narudzbine(): Observable<StamparNarudzbina[]> {
    return this.http.get<StamparNarudzbina[]>(`${this.url}/narudzbine`, { withCredentials: true });
  }

  promeniStatus(id: number, status: StatusNarudzbine): Observable<StamparNarudzbina> {
    return this.http.put<StamparNarudzbina>(`${this.url}/narudzbine/${id}/status`, null, {
      params: new HttpParams().set('status', status),
      withCredentials: true
    });
  }
}
