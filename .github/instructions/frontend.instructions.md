---
applyTo: "frontend/**"
---

# Frontend konvencije (Angular 20)

Bazirano na organizaciji koju korisnica koristi u ranijim projektima (standalone
komponente, folder-po-komponenti), proširenoj i organizovanoj po celinama jer ovaj
projekat ima znatno više ekrana nego njeni prethodni radovi.

## Struktura foldera

```
src/app/
├── app.routes.ts
├── app.config.ts
├── shared/                 // header, footer, navigacija — na svakoj strani
│   ├── header/
│   └── footer/
├── components/
│   ├── javno/               // početna, pretraga, detalji-proizvoda (nejavljeni korisnik)
│   ├── auth/                 // login-korisnik, login-admin, registracija, zaboravljena-lozinka, reset-lozinke
│   ├── klijent/               // profil, pretraga-proizvoda, priprema-proizvoda, e-korpa, arhiva-proizvoda, javne-nabavke
│   ├── stampar/                // profil, proizvodi-usluge, azuriranje-kolicina, uvoz-json, narudzbine, licitacije, izvestavanje
│   └── admin/                   // korisnici, zahtevi-registracije, kategorije, statistike
├── models/                        // TS klase/interfejsi — nazivi polja prate DB/JSON (npr. jedinicnaCena, kolicinaNaLageru)
├── services/                       // po jedan servis po resursu (kao u primeru lab vežbi: user.service.ts, itd.)
│   ├── auth.service.ts
│   ├── proizvod.service.ts
│   ├── stamparija.service.ts
│   ├── narudzbina.service.ts
│   ├── kategorija.service.ts
│   └── statistika.service.ts
└── guards/ i interceptors/         // auth guard po tipu korisnika, HTTP interceptor za token/greske (ako treba)
```

Svaka komponenta: folder u kebab-case sa `.ts` / `.html` / `.css` / `.spec.ts`
(standardni Angular CLI `ng generate component` izlaz) — standalone, bez
NgModule-a, isto kao u referentnim projektima.

## Servisi / HTTP pozivi

- Ne hardkodovati `http://localhost:8080` po svakom servisu (kao u starijem
  primeru) — koristiti `environment.ts` / `environment.development.ts` sa
  `apiUrl`, pa `${environment.apiUrl}/...` u servisima. (Vidi `DECISIONS.md` —
  ovo je mala izmena u odnosu na stare projekte, urađena zbog veličine ovog
  projekta.)
- Modeli (klase u `models/`) prate imena polja iz baze/JSON primera (npr.
  `jedinicnaCena`, `kolicinaNaLageru`, `dostupneBoje`, `maxSirinaMm`) da bi
  serijalizacija ka/od backend-a bila direktna, bez ručnog mapiranja.

## Rute

- Javno vidljiva prijava (`/login`) odvojena od admin prijave (`/admin-login` ili
  slično) — admin ruta se **ne linkuje** sa javnih stranica.
- Guard-ovi po tipu korisnika (klijent/štampar/admin) da se spreči pristup
  tuđim rutama direktnim unosom URL-a.

## UI / stil

- Zajednički `header`/`footer` (meni, link za odjavu, link "nazad na početak")
  na svakoj strani — ne ponavljati markup po komponenti, izvući u `shared/`.
- CSS uniforman kroz ceo sajt (zajedničke boje/fontovi/spacing — npr. CSS
  varijable u `styles.css`), responsive (media queries ili CSS grid/flex).
- Klijentski komentari koje je **sam korisnik ostavio** uokviriti tankom
  narandžastom linijom (eksplicitan zahtev iz specifikacije, deo "Arhiva
  proizvoda").
- Za pripremu proizvoda (dodavanje sličice/teksta na sliku proizvoda pre
  poručivanja): dozvoljeno HTML5 Canvas ili CSS `position: absolute/relative`
  preklapanje — nije potrebna serverska obrada slike.

## Validacija

- Osnovna JS validacija na formama (registracija, itd.) pre slanja ka backendu —
  server-side validacija je i dalje obavezna, front je samo UX poboljšanje.
