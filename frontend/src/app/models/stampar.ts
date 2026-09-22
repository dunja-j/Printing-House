import { UslugaStampe } from './javno';

export interface StamparProizvod {
  id: number;
  sifra: string;
  naziv: string;
  opis: string | null;
  kategorija: string;
  potkategorija: string | null;
  jedinicnaCena: number;
  kolicinaNaLageru: number;
  slikaUrl: string | null;
  aktivan: boolean;
  dostupneBoje: string[];
  uslugeStampe: UslugaStampe[];
}

export interface PotkategorijaOpcija {
  id: number;
  naziv: string;
}

export interface KategorijaSaPotkategorijama {
  id: number;
  naziv: string;
  potkategorije: PotkategorijaOpcija[];
}

export interface NovaUslugaStampe {
  tipStampe: string;
  dodatnaCenaPoKomadu: number | null;
  maxSirinaMm: number | null;
  maxVisinaMm: number | null;
}

export interface NoviProizvod {
  sifra: string;
  naziv: string;
  opis: string | null;
  kategorijaId: number | null;
  potkategorijaId: number | null;
  jedinicnaCena: number | null;
  kolicinaNaLageru: number | null;
  dostupneBoje: string[];
  uslugeStampe: NovaUslugaStampe[];
}
