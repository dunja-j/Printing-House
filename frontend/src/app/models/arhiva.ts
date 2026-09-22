export type VrednostOcene = 'lajk' | 'dislajk';

export interface Komentar {
  id: number;
  korIme: string;
  autor: string;
  tekst: string;
  datum: string;
  moj: boolean;
}

export interface ArhivaProizvod {
  proizvodId: number;
  naziv: string;
  slikaUrl: string | null;
  nazivStamparije: string;
  grad: string | null;
  brojLajkova: number;
  brojDislajkova: number;
  mojaOcena: VrednostOcene | null;
  poslednjiKomentari: Komentar[];
}
