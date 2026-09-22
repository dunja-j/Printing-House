export type StatusNabavke = 'otvorena' | 'zakljucena' | 'neuspela';

export interface StavkaNabavke {
  id: number;
  nazivProizvoda: string;
  kategorija: string;
  potkategorija: string | null;
  kolicina: number;
  boja: string | null;
  tipStampe: string | null;
  tekstZaStampu: string | null;
}

export interface Ponuda {
  id: number;
  stamparKorIme: string;
  nazivStamparije: string;
  grad: string | null;
  ukupnaCena: number;
  datum: string;
  pobednicka: boolean;
}

/** Pogled institucije na sopstvenu nabavku. */
export interface Nabavka {
  id: number;
  datumObjave: string;
  rokZaPonude: string;
  status: StatusNabavke;
  preostaloSekundi: number;
  narudzbinaId: number | null;
  brojPonuda: number;
  stavke: StavkaNabavke[];
  ponude: Ponuda[];
}

/** Pogled štamparije na otvorenu nabavku. */
export interface NabavkaZaStampara {
  id: number;
  institucija: string;
  grad: string | null;
  datumObjave: string;
  rokZaPonude: string;
  preostaloSekundi: number;
  brojPonuda: number;
  mojaPonuda: number | null;
  mozeDaPonudi: boolean;
  razlog: string | null;
  stavke: StavkaNabavke[];
}

export const NAZIV_STATUSA_NABAVKE: Record<StatusNabavke, string> = {
  otvorena: 'Licitacija u toku',
  zakljucena: 'Zaključena',
  neuspela: 'Neuspela'
};
