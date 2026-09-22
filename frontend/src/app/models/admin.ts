import { TipKorisnika } from './korisnik';

export type StatusRegistracije = 'na_cekanju' | 'odobren' | 'odbijen';

export interface AdminKorisnik {
  korIme: string;
  ime: string;
  prezime: string;
  mejl: string;
  telefon: string | null;
  tip: TipKorisnika;
  statusRegistracije: StatusRegistracije;
  slikaUrl: string;
  nazivInstitucije: string | null;
  adresaSedista: string | null;
  grad: string | null;
  maticniBroj: string | null;
  pib: string | null;
  datumRegistracije: string;
  institucija: boolean;
}

export const NAZIV_TIPA: Record<TipKorisnika, string> = {
  klijent_fizicko: 'Klijent – fizičko lice',
  klijent_pravno: 'Klijent – pravno lice',
  stampar: 'Štamparija',
  administrator: 'Administrator'
};

export const NAZIV_STATUSA_REGISTRACIJE: Record<StatusRegistracije, string> = {
  na_cekanju: 'Na čekanju',
  odobren: 'Odobren',
  odbijen: 'Odbijen'
};
