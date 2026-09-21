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
