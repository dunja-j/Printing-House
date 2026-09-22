export interface TopProizvod {
  id: number;
  naziv: string;
  slikaUrl: string | null;
  nazivStamparije: string;
  grad: string | null;
  brojLajkova: number;
}

export interface JavnaPocetna {
  brojStamparija: number;
  topProizvodi: TopProizvod[];
}

export interface Kategorija {
  id: number;
  naziv: string;
}

export interface PretragaRed {
  id: number;
  naziv: string;
  nazivStamparije: string;
  grad: string | null;
  kategorija: string;
  jedinicnaCena: number;
  kolicinaNaLageru: number;
  brojLajkova: number;
}

export interface DetaljiProizvoda {
  id: number;
  sifra: string;
  naziv: string;
  opis: string | null;
  kategorija: string;
  potkategorija: string | null;
  jedinicnaCena: number;
  kolicinaNaLageru: number;
  slikaUrl: string | null;
  nazivStamparije: string;
  grad: string | null;
  adresaSedista: string | null;
  brojLajkova: number;
  brojDislajkova: number;
}

export interface UslugaStampe {
  id: number;
  tipStampe: string;
  dodatnaCenaPoKomadu: number;
  maxSirinaMm: number | null;
  maxVisinaMm: number | null;
}

/** Ono što vidi prijavljeni klijent — detalji + podaci potrebni za poručivanje. */
export interface DetaljiZaKlijenta extends DetaljiProizvoda {
  dostupneBoje: string[];
  uslugeStampe: UslugaStampe[];
}
