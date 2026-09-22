import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { TipKorisnika } from '../models/korisnik';
import { AuthService } from '../services/auth.service';

/**
 * Propusta samo prijavljene korisnike navedenih tipova. Ovo je UX zastita
 * (da se rutom ne moze "prosetati" kroz tudji deo sajta) — prava provera je
 * na backendu, kroz HTTP sesiju.
 */
export function dozvoljenTip(tipovi: TipKorisnika[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const korisnik = auth.korisnik();

    if (!korisnik) {
      return router.createUrlTree(['/login']);
    }
    if (!tipovi.includes(korisnik.tip)) {
      return router.createUrlTree([auth.pocetnaRuta(korisnik.tip)]);
    }
    return true;
  };
}

/** Prijavljenog korisnika vraca na njegovu pocetnu umesto na login formu. */
export const samoGost: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const korisnik = auth.korisnik();

  return korisnik ? router.createUrlTree([auth.pocetnaRuta(korisnik.tip)]) : true;
};

/** Propusta bilo kog prijavljenog korisnika, bez obzira na tip. */
export const samoPrijavljeni: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.korisnik() ? true : router.createUrlTree(['/login']);
};
