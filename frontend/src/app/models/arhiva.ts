export type VrednostOcene = 'lajk' | 'dislajk';

export interface Komentar {
  id: number;
  korIme: string;
  autor: string;
  tekst: string;
  datum: string;
  moj: boolean;
}

export interface ArhivaStavka {
  stavkaId: number;
  narudzbinaId: number;
  datumNarudzbine: string;
  status: 'isporuceno' | 'primljeno';
  proizvodId: number;
  naziv: string;
  slikaUrl: string | null;
  kolicina: number;
  boja: string | null;
  tipStampe: string | null;
  nazivStamparije: string;
  grad: string | null;
  brojLajkova: number;
  brojDislajkova: number;
  mojaOcena: VrednostOcene | null;
  poslednjiKomentari: Komentar[];
  mozePotvrditiPrijem: boolean;
  mozeOceniti: boolean;
}
