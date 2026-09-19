# Evidencija funkcionalnosti — Printing House

Izvor: `pia_2526_avgsept_min.pdf` (crno = obavezno, min 15 poena; crveno = bonus,
do 30 poena ukupno) + opis projekta. Ažuriraj status čim se nešto uradi ili
promeni odluka o tome da li se radi. Status vrednosti: `todo`, `u radu`, `gotovo`,
`preskočeno (svesna odluka)`.

Legenda prioriteta: 🔴 **OBAVEZNO** (mora da radi za odbranu) · 🟡 **BONUS**
(radi se posle svega obaveznog, redom od lakših ka težim) · ⚠️ = kompleksnija
bonus stavka — po dogovoru sa korisnicom, raditi je poslednju, i preskočiti ako
nema vremena.

## Početna strana

| # | Funkcionalnost | Prioritet | Status | Napomena |
|---|---|---|---|---|
| 1 | Prijava korisnika (klijent/štampar javna forma + posebna admin ruta) | 🔴 OBAVEZNO | todo | |
| 2 | Registracija svih tipova korisnika (fiz./prav. lice, štampar) | 🔴 OBAVEZNO | todo | ide u status "na čekanju" do odobrenja admina |
| 3 | Zaboravljena lozinka (reset link, važi 5 min) | 🟡 BONUS | todo | |

## Javno vidljive web strane (nejavljen korisnik)

| # | Funkcionalnost | Prioritet | Status | Napomena |
|---|---|---|---|---|
| 4 | Ukupan broj štamparija + TOP 5 najbolje ocenjenih proizvoda | 🔴 OBAVEZNO | todo | |
| 5 | Pretraga po više parametara (naziv/kategorija) + rezultati sa sortiranjem | 🔴 OBAVEZNO | todo | abecedno sortiranje klikom na zaglavlje kolone |
| 6 | Detalji proizvoda (sa jednom slikom) | 🔴 OBAVEZNO | todo | |
| 7 | Galerija sa dodatnim slikama u detaljima (max 3 thumbnail-a) | 🟡 BONUS | todo | pamćenje izabrane slike u kolačiću |

## Deo za klijente

| # | Funkcionalnost | Prioritet | Status | Napomena |
|---|---|---|---|---|
| 8 | Prikaz i ažuriranje profila | 🔴 OBAVEZNO | todo | uklj. promenu profilne slike |
| 9 | Tabela sa prethodnim i aktuelnim narudžbinama (sa sortiranjem) | 🔴 OBAVEZNO | todo | |
| 10 | Otkazivanje narudžbine u statusu "naručeno" | 🟡 BONUS | todo | lako, raditi rano |
| 11 | Pretraživanje proizvoda i (prošireni) detalji | 🔴 OBAVEZNO | todo | boja, tip štampe, max dimenzije, cena po komadu |
| 12 | Mapa gde je štamparija (u detaljima proizvoda) | 🟡 BONUS | todo | npr. Leaflet + OpenStreetMap, relativno lako |
| 13 | Dodavanje usluge štampe i teksta/količine za poručivanje | 🔴 OBAVEZNO | todo | |
| 14 | Priprema proizvoda (dodavanje sličice, prikaz na slici proizvoda) | 🟡 BONUS ⚠️ | todo | Canvas/CSS overlay, srednje kompleksno |
| 15 | E-korpa: trenutni prikaz + zatvaranje narudžbine (grupisano po štampariji) | 🔴 OBAVEZNO | todo | jedna faktura po štampariji |
| 16 | Dostavljanje PDF fakture na mejl | 🟡 BONUS ⚠️ | todo | zavisi od PDF + mejl biblioteke, raditi kasnije |
| 17 | Servis za plaćanje (Stripe test / PayPal sandbox) | 🟡 BONUS ⚠️ | todo | najkompleksnija bonus stavka — raditi poslednju, prva kandidat za preskakanje |
| 18 | Javne nabavke (klijent - pravno lice, licitacija 10 min) | 🔴 OBAVEZNO | todo | ne treba pravi tajmer/WebSocket, provera pri sledećoj prijavi |
| 19 | Lajkovanje/dislajkovanje i komentarisanje primljenih proizvoda | 🔴 OBAVEZNO | todo | prikaz poslednjih 5 komentara; sopstveni komentar uokviren narandžasto |

## Deo za štampare

| # | Funkcionalnost | Prioritet | Status | Napomena |
|---|---|---|---|---|
| 20 | Prikaz i ažuriranje profila | 🔴 OBAVEZNO | todo | |
| 21 | Proizvodi i usluge — dodavanje | 🔴 OBAVEZNO | todo | predefinisane kategorije/potkategorije |
| 22 | Ažuriranje količina postojećih proizvoda | 🔴 OBAVEZNO | todo | |
| 23 | Dodavanje iz JSON fajla (+ naknadno dodavanje slika) | 🟡 BONUS ⚠️ | todo | format u `primer-proizvodi.json` — srednje/visoko kompleksno |
| 24 | Naručeni proizvodi — promena statusa (naručeno→u štampi→isporučeno) | 🔴 OBAVEZNO | todo | samo za klijente - fizička lica |
| 25 | Licitacije — slanje ponuda za otvorene javne nabavke | 🔴 OBAVEZNO | todo | jedna ponuda po javnoj nabavci |
| 26 | Izveštavanje — PDF izveštaj o svim ponudama i pobedniku | 🟡 BONUS ⚠️ | todo | zavisi od #18/#25, raditi posle njih |

## Administratorski deo

| # | Funkcionalnost | Prioritet | Status | Napomena |
|---|---|---|---|---|
| 27 | Upravljanje korisničkim nalozima (pregled, ažuriranje, brisanje) | 🔴 OBAVEZNO | todo | |
| 28 | Obrada zahteva za registraciju (prihvati/odbaci) | 🔴 OBAVEZNO | todo | |
| 29 | Upravljanje kategorijama proizvoda (dodavanje kategorija/potkategorija) | 🔴 OBAVEZNO | todo | |
| 30 | Statistike u vidu grafikona (bar/pita/linijski) | 🟡 BONUS ⚠️ | todo | 3 različita grafikona — raditi kasnije, kandidat za preskakanje ako fali vremena |

## Ostale karakteristike (baseline kvalitet — nema posebnu listu poena u minimalnim zahtevima, ali su deo opisa projekta)

| # | Funkcionalnost | Prioritet | Status | Napomena |
|---|---|---|---|---|
| 31 | Otpornost na nekorektan unos + serverska validacija svuda | 🔴 OBAVEZNO | todo | |
| 32 | Uniforman CSS izgled, header/footer/meni na svakoj strani | 🔴 OBAVEZNO | todo | |
| 33 | Responsive design (manji/veći ekrani) | 🔴 OBAVEZNO | todo | |
| 34 | Testirano u bar 3 browsera | 🔴 OBAVEZNO | todo | Chrome, Firefox, Edge npr. |
| 35 | Baza popunjena sa dovoljno podataka za odbranu | 🔴 OBAVEZNO | todo | u suprotnom -5 poena po specifikaciji! |

## Predlog redosleda rada

1. Sve 🔴 OBAVEZNO stavke (1–9, 11, 13, 15, 18–22, 24–25, 27–35) — ovo je prag za
   prolaz (min 15 poena) i mora da radi pouzdano.
2. Lakše 🟡 BONUS stavke bez ⚠️ (10 — otkazivanje narudžbine, 12 — mapa).
3. Stavke sa ⚠️, jedna po jedna, poslednje, po slobodnoj proceni vremena
   (14, 16, 17, 23, 26, 30). Ako vremena nema, mirno preskočiti — samo upisati u
   ovaj fajl status `preskočeno (svesna odluka)` i razlog.
