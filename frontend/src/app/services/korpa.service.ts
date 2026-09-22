import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../environments/environment';
import { DodajUKorpu, Korpa } from '../models/korpa';

@Injectable({ providedIn: 'root' })
export class KorpaService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/klijent/korpa`;

  private _korpa = signal<Korpa | null>(null);

  korpa = this._korpa.asReadonly();
  brojStavki = computed(() => this._korpa()?.ukupnoStavki ?? 0);

  ucitaj(): Observable<Korpa> {
    return this.http
      .get<Korpa>(this.url, { withCredentials: true })
      .pipe(tap((k) => this._korpa.set(k)));
  }

  dodaj(stavka: DodajUKorpu): Observable<Korpa> {
    return this.http
      .post<Korpa>(this.url, stavka, { withCredentials: true })
      .pipe(tap((k) => this._korpa.set(k)));
  }

  promeniKolicinu(id: number, kolicina: number): Observable<Korpa> {
    return this.http
      .put<Korpa>(`${this.url}/${id}`, null, {
        params: new HttpParams().set('kolicina', kolicina),
        withCredentials: true
      })
      .pipe(tap((k) => this._korpa.set(k)));
  }

  ukloni(id: number): Observable<Korpa> {
    return this.http
      .delete<Korpa>(`${this.url}/${id}`, { withCredentials: true })
      .pipe(tap((k) => this._korpa.set(k)));
  }

  isprazni(): Observable<Korpa> {
    return this.http
      .delete<Korpa>(this.url, { withCredentials: true })
      .pipe(tap((k) => this._korpa.set(k)));
  }

  zakljuci(): Observable<{ narudzbine: number[]; poruka: string }> {
    return this.http
      .post<{ narudzbine: number[]; poruka: string }>(
        `${this.url}/zakljuci`,
        {},
        { withCredentials: true }
      )
      .pipe(tap(() => this._korpa.set(null)));
  }

  zaboravi(): void {
    this._korpa.set(null);
  }
}
