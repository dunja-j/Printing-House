export type StatusNarudzbine =
  | 'naruceno'
  | 'placeno'
  | 'u_stampi'
  | 'isporuceno'
  | 'primljeno'
  | 'nije_stiglo'
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
  mozePrijavitiNedostavljeno: boolean;
  stavke: StavkaNarudzbine[];
}

export const NAZIV_STATUSA: Record<StatusNarudzbine, string> = {
  naruceno: 'Naručeno',
  placeno: 'Plaćeno',
  u_stampi: 'U štampi',
  isporuceno: 'Isporučeno',
  primljeno: 'Primljeno',
  nije_stiglo: 'Nije stiglo',
  otkazano: 'Otkazano'
};
