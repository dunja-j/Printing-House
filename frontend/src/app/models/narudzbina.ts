export type StatusNarudzbine =
  | 'naruceno'
  | 'placeno'
  | 'u_stampi'
  | 'isporuceno'
  | 'primljeno'
  | 'otkazano';

export interface StavkaNarudzbine {
  id: number;
  proizvodId: number;
  nazivProizvoda: string;
  tipStampe: string | null;
  kolicina: number;
  boja: string | null;
  tekstZaStampu: string | null;
  cenaStavke: number;
}

export interface Narudzbina {
  id: number;
  datumNarudzbine: string;
  status: StatusNarudzbine;
  nazivStamparije: string;
  gradStamparije: string | null;
  ukupanIznos: number;
  brojStavki: number;
  mozeOtkazati: boolean;
  mozePotvrditiPrijem: boolean;
  stavke: StavkaNarudzbine[];
}

export const NAZIV_STATUSA: Record<StatusNarudzbine, string> = {
  naruceno: 'Naručeno',
  placeno: 'Plaćeno',
  u_stampi: 'U štampi',
  isporuceno: 'Isporučeno',
  primljeno: 'Primljeno',
  otkazano: 'Otkazano'
};
