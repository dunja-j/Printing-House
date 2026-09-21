import { TipKorisnika } from './korisnik';

export interface RegistracijaPodaci {
  korIme: string;
  lozinka: string;
  ime: string;
  prezime: string;
  telefon: string;
  mejl: string;
  tip: TipKorisnika;
  nazivInstitucije?: string;
  adresaSedista?: string;
  grad?: string;
  maticniBroj?: string;
  pib?: string;
}
