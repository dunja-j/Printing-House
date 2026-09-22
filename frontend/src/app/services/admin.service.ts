import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { AdminKorisnik } from '../models/admin';
import { KategorijaSaPotkategorijama, PotkategorijaOpcija } from '../models/stampar';
import { ProfilPodaci } from './profil.service';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/admin`;

  zahtevi(): Observable<AdminKorisnik[]> {
    return this.http.get<AdminKorisnik[]>(`${this.url}/zahtevi`, { withCredentials: true });
  }

  odluci(korIme: string, prihvati: boolean): Observable<AdminKorisnik> {
    return this.http.post<AdminKorisnik>(`${this.url}/zahtevi/${korIme}`, null, {
      params: new HttpParams().set('prihvati', prihvati),
      withCredentials: true
    });
  }

  korisnici(): Observable<AdminKorisnik[]> {
    return this.http.get<AdminKorisnik[]>(`${this.url}/korisnici`, { withCredentials: true });
  }

  azuriraj(korIme: string, podaci: ProfilPodaci): Observable<AdminKorisnik> {
    return this.http.put<AdminKorisnik>(`${this.url}/korisnici/${korIme}`, podaci, {
      withCredentials: true
    });
  }

  obrisi(korIme: string): Observable<{ poruka: string }> {
    return this.http.delete<{ poruka: string }>(`${this.url}/korisnici/${korIme}`, {
      withCredentials: true
    });
  }

  kategorije(): Observable<KategorijaSaPotkategorijama[]> {
    return this.http.get<KategorijaSaPotkategorijama[]>(`${this.url}/kategorije`, {
      withCredentials: true
    });
  }

  dodajKategoriju(naziv: string): Observable<PotkategorijaOpcija> {
    return this.http.post<PotkategorijaOpcija>(
      `${this.url}/kategorije`,
      { naziv },
      { withCredentials: true }
    );
  }

  dodajPotkategoriju(kategorijaId: number, naziv: string): Observable<PotkategorijaOpcija> {
    return this.http.post<PotkategorijaOpcija>(
      `${this.url}/kategorije/${kategorijaId}/potkategorije`,
      { naziv },
      { withCredentials: true }
    );
  }
}
