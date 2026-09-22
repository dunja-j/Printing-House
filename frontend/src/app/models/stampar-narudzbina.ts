import { StavkaNarudzbine, StatusNarudzbine } from './narudzbina';

export interface StamparNarudzbina {
  id: number;
  datumNarudzbine: string;
  status: StatusNarudzbine;
  klijentKorIme: string;
  imeKlijenta: string;
  telefonKlijenta: string | null;
  gradKlijenta: string | null;
  ukupanIznos: number;
  brojStavki: number;
  sledeciStatus: StatusNarudzbine | null;
  stavke: StavkaNarudzbine[];
}
