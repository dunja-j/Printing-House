import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../environments/environment';
import { Korisnik } from '../models/korisnik';
import { AuthService } from './auth.service';

export interface ProfilPodaci {
  ime: string;
  prezime: string;
  telefon: string;
  mejl: string;
  grad?: string;
  nazivInstitucije?: string;
  adresaSedista?: string;
  maticniBroj?: string;
  pib?: string;
}

@Injectable({ providedIn: 'root' })
export class ProfilService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  ucitaj(): Observable<Korisnik> {
    return this.http
      .get<Korisnik>(`${environment.apiUrl}/profil`, { withCredentials: true })
      .pipe(tap((k) => this.auth.postaviKorisnika(k)));
  }

  azuriraj(podaci: ProfilPodaci): Observable<Korisnik> {
    return this.http
      .put<Korisnik>(`${environment.apiUrl}/profil`, podaci, { withCredentials: true })
      .pipe(tap((k) => this.auth.postaviKorisnika(k)));
  }

  promeniSliku(slika: File): Observable<Korisnik> {
    const telo = new FormData();
    telo.append('slika', slika);
    return this.http
      .post<Korisnik>(`${environment.apiUrl}/profil/slika`, telo, { withCredentials: true })
      .pipe(tap((k) => this.auth.postaviKorisnika(k)));
  }

  /** Puna putanja do profilne slike. */
  slikaProfila(naziv: string | null): string {
    return `${environment.fileUrl}/profilne/${naziv ?? 'default_profile_image.jpg'}`;
  }
}
