# Printing House — kontekst za GitHub Copilot

Ovo je **projekat iz predmeta Programiranje internet apliakcija (PIA)**, ETF Beograd,
školska 2025/26, avgustovsko-septembarski rok. Radi se **samostalno** (uslov za
izlazak na usmeni ispit), a AI alati smeju da pomažu oko dizajna/pisanja koda, ali
student mora da razume i ume da objasni/izmeni svaki deo koda — na odbrani AI alati
neće biti dostupni.

## Radni proces — VAŽNO, uvek automatski, bez čekanja da te korisnica podseti

- **Pre nego što počneš da radiš na nekoj funkcionalnosti**, pogledaj `FEATURES.md`
  da vidiš prioritet/status, i `DECISIONS.md` da vidiš da li je nešto relevantno
  već dogovoreno.
- **Čim završiš (ili delimično uradiš) neku funkcionalnost iz `FEATURES.md`**,
  sam izmeni taj red u tabeli: status na `u radu` ili `gotovo`, po potrebi
  dopuni kolonu "Napomena" (npr. šta nedostaje, šta je pojednostavljeno). Ovo
  radiš odmah, u istom odgovoru u kom si napisao/la kod — korisnica ne treba
  ručno da ažurira ovaj fajl niti da te podseća da to uradiš.
- **Čim doneseš (ili predložiš, pa korisnica potvrdi) bilo koju odluku** o nečemu
  što specifikacija ne definiše precizno (naziv kolone/tabele, biblioteka,
  format rute, oblik JSON odgovora, itd.), **odmah dodaj novi red u
  `DECISIONS.md`** — ne čekaj da te neko pita, i ne ostavljaj to "za kasnije".
- Ako u toku rada primetiš da je neka ranija odluka iz `DECISIONS.md` postala
  neaktuelna ili pogrešna, ažuriraj taj red (dodaj napomenu ili izmeni), umesto
  da samo tiho radiš drugačije.
- Ukratko: `FEATURES.md` i `DECISIONS.md` su **radni dnevnik koji ti održavaš**,
  ne fajlovi koje samo čitaš.

## Git tok rada — jedan feature po grani (VAŽNO, prati uvek kad se radi na kodu)

Radimo isključivo **jedan feature odjednom**, sledećim ciklusom. Ne preskači
korake i ne radi više feature-a paralelno na istoj grani.

1. Proveri da je radno stablo čisto (nema nekomitovanih izmena) i da si na
   `main` grani. Ako nije čisto, pitaj korisnicu šta da se radi sa tim
   izmenama pre nego što nastaviš.
2. Izaberi **sledeći** feature iz `FEATURES.md`, po redosledu: prvo sve
   🔴 OBAVEZNO odozgo nadole (status `todo`), zatim 🟡 BONUS bez ⚠️, na kraju
   ⚠️ stavke — osim ako je korisnica eksplicitno tražila konkretan feature.
3. Napravi novu granu od `main`: `git checkout -b feature/<kratak-opisni-naziv>`
   (npr. `feature/registracija-klijenta`, `feature/e-korpa`, na engleskom ili
   srpskom bez šumnika, kebab-case).
4. Implementiraj feature (backend + frontend + DB izmene po potrebi), prateći
   `.github/instructions/backend.instructions.md` i `frontend.instructions.md`.
5. Ako feature menja šemu baze, ažuriraj `database/printing_house_db.sql` da i
   dalje bude **kompletna skripta koja pravi bazu od nule** (nove
   tabele/kolone/seed podaci) — ne dump postojeće baze, i ne posebna
   "migraciona" skripta pored nje (specifikacija traži jednu skriptu).
6. Ažuriraj status feature-a u `FEATURES.md` i upiši eventualne nove odluke u
   `DECISIONS.md` (kao što je već opisano gore).
7. **Stani** (ne commit-uj na main, ne kreni na sledeći feature) i javi
   korisnici jasnu poruku sa:
   - kratkim rezimeom šta je urađeno i na kojoj grani,
   - napomenom da ponovo pokrene `database/printing_house_db.sql` pre testiranja
     (ako je menjana),
   - **tačnim koracima kako da testira baš taj feature**: koje rute/ekrane da
     otvori, koji test-nalog/kredencijale iz seed podataka u SQL skripti da
     koristi, šta konkretno da unese/pritisne, i šta treba da vidi kao rezultat
     ako sve radi ispravno,
   - eventualnim poznatim ograničenjima/pojednostavljenjima tog feature-a.
8. **Čekaj eksplicitnu potvrdu korisnice** da feature radi, pre nego što
   uradiš bilo šta dalje. Ako prijavi problem: ispravi na **istoj** grani i
   ponovo zatraži testiranje (ne pravi novu granu za popravku).
9. Kad korisnica potvrdi da radi: `git checkout main`,
   `git merge --no-ff feature/<naziv>`, obriši granu
   (`git branch -d feature/<naziv>`), pa se vrati na korak 1 za sledeći
   feature — automatski, bez čekanja da te korisnica ponovo pokrene ceo opis
   procesa (dovoljno je da kaže npr. "kreni na sledeći").

## O sistemu

Web sistem **"Printing House"** — platforma koja povezuje **štamparije** (printer
shops) i **klijente** koji naručuju štampane proizvode (majice, šolje, roll-up
baneri, vizit karte, posteri...). Tri tipa korisnika:

1. **Klijent** — fizičko lice ili pravno lice (institucija). Naručuje proizvode.
2. **Štampar** — predstavlja jednu štampariju, nudi proizvode i usluge štampe.
3. **Administrator** — upravlja korisnicima, kategorijama, odobrava registracije,
   gleda statistiku. Prijavljuje se na posebnoj, javno nevidljivoj ruti
   (npr. `/admin/login`), ne kroz glavnu formu za prijavu.

## Ključni tok statusa narudžbine

```
naručeno → [plaćeno] → u štampi → isporučeno → primljeno
```

- `plaćeno` postoji samo ako se implementira servis za plaćanje (bonus stavka).
- Klijent može otkazati narudžbinu samo dok je u statusu `naručeno`.
- Štampar menja `naručeno → u štampi → isporučeno`.
- Klijent menja `isporučeno → primljeno` (i tada može lajkovati/komentarisati
  proizvod).
- Za **pravna lica** narudžbina ne ide direktno u fakturu — ide u **javnu nabavku**
  (poziv za licitaciju, traje 10 min, štamparije šalju ponude, najniža ukupna
  ponuda uz dovoljnu količinu na stanju pobeđuje). Nema potrebe za tajmerom u bazi
  ili WebSocket-om — dovoljno je da se pri sledećoj prijavi institucije proveri da
  li je vreme isteklo i tada se zaključi najbolja ponuda.

## Kategorije proizvoda (predefinisane, iz specifikacije)

- **Štampa malih formata**: olovke, vizit karte, flajeri, zahvalnice, pozivnice,
  fascikle...
- **Štampa velikih formata**: posteri, rollups, fototapete.
- **Kreativne štampe**: šolje, štampa na majicama, štampa na duksevima, štampa na
  cegerima.

Administrator može dodavati nove kategorije/potkategorije. Padajuće liste kategorija
(na javnoj strani i pretrazi) prikazuju samo kategorije u kojima trenutno postoje
aktivni proizvodi na stanju.

## Format JSON fajla za uvoz proizvoda (Prilog 1 iz specifikacije)

Štampar može dodati proizvode iz JSON fajla (bonus stavka "Dodavanje iz JSON
fajla"). **Nazivi polja u Proizvod/UslugaStampe modelu treba da prate ovaj tačan
oblik** (primer je i priložen u `primer-proizvodi.json` u korenu projekta), da bi
uvoz radio bez transformacija:

```json
{
  "stampaorijaId": "stampa_001",
  "nazivStamparije": "Copy Studio Kumanovska",
  "proizvodi": [
    {
      "sifra": "PR-001",
      "naziv": "Pamucna Polo Majica",
      "opis": "Kvalitetna pamučna polo majica 180g/m2, ...",
      "kategorija": "Kreativne štampe",
      "potkategorija": "Štampa na majicama",
      "jedinicnaCena": 1200.00,
      "kolicinaNaLageru": 150,
      "dostupneBoje": ["Bela", "Crna", "Tamno plava", "Siva"],
      "slikaUrl": "",
      "dodatneSlike": [],
      "uslugeStampe": [
        {
          "idUsluge": "USL-01",
          "tipStampe": "Direktna štampa na tekstil (DTG)",
          "dodatnaCenaPoKomadu": 350.00,
          "maxSirinaMm": 300,
          "maxVisinaMm": 400
        }
      ]
    }
  ]
}
```

Napomena: nakon učitavanja JSON-a, štampar u dodatnom koraku dodaje i stvarne
slike proizvoda (slike se ne uvoze iz `slikaUrl` linka — mora FileUpload).

## Autentifikacija i registracija

- Prijava: korisničko ime + lozinka. Pogrešan unos → jasna poruka greške.
- **Zaboravljena lozinka** (bonus): link vodi na formu (korisničko ime ili mejl) →
  generiše se privremeni web link za reset, **validan 5 minuta**.
- **Registracija** je moguća samo za klijente (fizička i pravna lica) i štamparije;
  administratora ne treba moći registrovati kroz UI.
  - Zajednička polja: korisničko ime (jedinstveno globalno), lozinka, ime, prezime
    (za pravna lica/štamparije — ime i prezime odgovornog lica), kontakt telefon,
    mejl (jedinstven), profilna slika.
  - Dodatno za **pravna lica** i **štamparije**: naziv institucije, adresa sedišta,
    matični broj (tačno 8 cifara, jedinstven), PIB (9 cifara, jedinstven, ne sme
    počinjati cifrom 0).
  - **Lozinka**: regex — min 8, max 12 karaktera, najmanje jedno veliko slovo,
    jedan broj, jedan specijalni karakter, mora počinjati slovom. Validacija i na
    frontu (JS) i na backu. **U bazi se čuva samo hash** (npr. BCrypt).
  - **Profilna slika**: FileUpload (ne link!), JPG/PNG/GIF, min 100×100, max
    250×250 px. Ako nije priložena → `default_profile_image.jpg`.
  - Nova registracija ide u status "na čekanju" dok je administrator ne
    prihvati/odbije (tabelarni pregled neodobrenih korisnika kod admina).

## Javne (nejavljene) stranice

- Broj registrovanih štamparija + TOP 5 ocenjenih proizvoda (po broju lajkova).
- Pretraga po nazivu proizvoda i/ili kategoriji (padajuća lista), rezultati u
  tabeli sa abecednim sortiranjem po nazivu (klik na zaglavlje kolone), dugme
  "DETALJI".
- Detalji proizvoda: naziv, štamparija, grad, broj lajkova/dislajkova, galerija
  (glavna slika + do 3 thumbnail-a bonus, izabrana slika pamti se u kolačiću kao
  trenutno glavna za taj proizvod).

## Napomene o tehničkim ograničenjima iz specifikacije

- Baza podataka **se ne kreira iz same aplikacije** — inicijalno kreiranje i
  popunjavanje ide nezavisno, preko SQL skripte (`database/printing_house_db.sql`
  u ovom repou). Ako koristimo Hibernate, `spring.jpa.hibernate.ddl-auto` mora biti
  `none` ili `validate`, **nikad** `update`/`create`.
- Aplikacija mora biti otporna na nekorektan unos, sa serverskom validacijom svuda
  gde ima smisla.
- Uniforman izgled (CSS), meni + header/footer na svakoj strani, link za povratak
  na početak i za odjavu na svim ekranima.
- Responsive design, testirati u bar 3 browsera.
- Fakture/izveštaji kao PDF (biblioteka po izboru — npr. `iText`/`OpenPDF` na
  backendu ili `jsPDF` na frontu), slanje mejlova (npr. `spring-boot-starter-mail`
  + npr. Mailtrap/Gmail SMTP za test).
- Plaćanje (bonus): Stripe Test Mode ili PayPal Sandbox — samo test/sandbox režim,
  nema pravog novca.
- Mape (u detaljima proizvoda, lokacija štamparije): spoljašnji API dozvoljen
  (npr. Leaflet + OpenStreetMap, ili Google Maps Embed).

## Tehnički stek (dogovoreno, videti `DECISIONS.md` za detalje)

- **Frontend**: Angular 20 (standalone komponente, bez NgModule-a).
- **Backend**: Spring Boot 3.5.x, Java 21+ (proveriti verziju dostupnu na
  fakultetskom sajtu/lab okruženju).
- **Baza**: MySQL, pristup preko **Spring Data JPA + Hibernate**.
- Struktura foldera/paketa: videti `.github/instructions/backend.instructions.md`
  i `.github/instructions/frontend.instructions.md` — bazirano na organizaciji
  koju korisnica već koristi u svojim ranijim projektima
  (`klk_jul25`, lab vežbe), prilagođeno za JPA i veći obim ovog projekta.

## Prioritet rada

1. Prvo završiti **sve obavezne (crne) stavke** iz `FEATURES.md` — to nosi
   minimalnih 15 poena i mora da radi kompletno i stabilno.
   Zatim redom, od lakših ka težim, bonus stavke.
2. Za bonus stavke označene kao **"kompleksno – ostaviti za kraj"** u
   `FEATURES.md` (plaćanje, PDF+mejl fakture, javne nabavke/licitacije,
   statistika-grafikoni, uvoz iz JSON-a): raditi ih tek kad su sve ostale
   funkcionalnosti gotove, i preskočiti ih ako nema vremena.
3. Ne izmišljati funkcionalnosti van specifikacije; ako nešto nije jasno, prvo
   predložiti 1-2 razumne opcije i sačekati potvrdu korisnice, pa upisati odluku
   u `DECISIONS.md`.
