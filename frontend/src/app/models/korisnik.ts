export type TipKorisnika =
  | 'klijent_fizicko'
  | 'klijent_pravno'
  | 'stampar'
  | 'administrator';

export interface Korisnik {
  korIme: string;
  ime: string;
  prezime: string;
  mejl: string;
  telefon: string | null;
  tip: TipKorisnika;
  slikaUrl: string;
  nazivInstitucije: string | null;
  adresaSedista: string | null;
  grad: string | null;
}
