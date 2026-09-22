export interface StavkaKorpe {
  id: number;
  proizvodId: number;
  nazivProizvoda: string;
  slikaUrl: string | null;
  boja: string | null;
  uslugaId: number | null;
  tipStampe: string | null;
  tekstZaStampu: string | null;
  kolicina: number;
  kolicinaNaLageru: number;
  jedinicnaCena: number;
  dodatnaCenaPoKomadu: number;
  cenaPoKomadu: number;
  ukupno: number;
}

export interface GrupaKorpe {
  stamparKorIme: string;
  nazivStamparije: string;
  grad: string | null;
  stavke: StavkaKorpe[];
  iznos: number;
}

export interface Korpa {
  grupe: GrupaKorpe[];
  ukupanIznos: number;
  ukupnoStavki: number;
  idePrekoJavneNabavke: boolean;
}

export interface DodajUKorpu {
  proizvodId: number;
  uslugaId: number | null;
  kolicina: number;
  boja: string | null;
  tekstZaStampu: string | null;
}
