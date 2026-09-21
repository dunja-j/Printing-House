import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';

import { environment } from '../../environments/environment';
import { Korisnik, TipKorisnika } from '../models/korisnik';

const KLJUC = 'korisnik';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  /** Kopija prijavljenog korisnika iz localStorage-a; server i dalje cuva pravu sesiju. */
  private _korisnik = signal<Korisnik | null>(this.procitajIzLocalStorage());

  korisnik = this._korisnik.asReadonly();
  prijavljen = computed(() => this._korisnik() !== null);

  prijava(korIme: string, lozinka: string): Observable<Korisnik> {
    return this.http
      .post<Korisnik>(
        `${environment.apiUrl}/auth/login`,
        { korIme, lozinka },
        { withCredentials: true }
      )
      .pipe(tap((k) => this.zapamti(k)));
  }

  prijavaAdmina(korIme: string, lozinka: string): Observable<Korisnik> {
    return this.http
      .post<Korisnik>(
        `${environment.apiUrl}/auth/login-admin`,
        { korIme, lozinka },
        { withCredentials: true }
      )
      .pipe(tap((k) => this.zapamti(k)));
  }

  odjava(): Observable<unknown> {
    return this.http
      .post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true })
      .pipe(tap(() => this.zaboravi()));
  }

  /**
   * Pri pokretanju aplikacije poredi localStorage sa stvarnom sesijom na serveru —
   * ako je sesija istekla ili je neko rucno ubacio korisnika u localStorage, cisti ga.
   */
  osveziSesiju(): Observable<Korisnik | null> {
    return this.http
      .get<Korisnik>(`${environment.apiUrl}/auth/trenutni`, { withCredentials: true })
      .pipe(
        tap((k) => this.zapamti(k)),
        catchError(() => {
          this.zaboravi();
          return of(null);
        })
      );
  }

  /** Pocetna strana za dati tip korisnika posle uspesne prijave. */
  pocetnaRuta(tip: TipKorisnika): string {
    switch (tip) {
      case 'administrator':
        return '/admin';
      case 'stampar':
        return '/stampar';
      default:
        return '/klijent';
    }
  }

  private zapamti(k: Korisnik): void {
    localStorage.setItem(KLJUC, JSON.stringify(k));
    this._korisnik.set(k);
  }

  private zaboravi(): void {
    localStorage.removeItem(KLJUC);
    this._korisnik.set(null);
  }

  private procitajIzLocalStorage(): Korisnik | null {
    const sacuvano = localStorage.getItem(KLJUC);
    if (!sacuvano) {
      return null;
    }
    try {
      return JSON.parse(sacuvano) as Korisnik;
    } catch {
      localStorage.removeItem(KLJUC);
      return null;
    }
  }
}
