# Printing House — arhitektura i tok podataka

Dokument za pripremu odbrane: šta koji fajl radi i kako jedan klik u pregledaču
dođe do baze i nazad.

---

## 1. Tehnološki stek

| Sloj | Tehnologija |
|---|---|
| Frontend | Angular 20 (standalone komponente, signals, `@if`/`@for`) na portu **4200** |
| Backend | Spring Boot 3.5.8, Java 25, REST API na portu **8080** |
| ORM | Spring Data JPA + Hibernate 6.6 |
| Baza | MySQL 8, šema `printing_house` |
| Lozinke | BCrypt (`spring-security-crypto`) |
| Sesija | `HttpSession` (kolačić `JSESSIONID`) |

Frontend i backend su **dve odvojene aplikacije** koje komuniciraju preko HTTP-a
i razmenjuju JSON. Nema server-side renderovanja — Angular je čisto klijentska
aplikacija koja poziva REST rute.

---

## 2. Opšti tok jednog zahteva

```
[Pregledač]
    │  klik / unos u formu
    ▼
[Komponenta .ts]                  drži stanje strane (signals), reaguje na događaje
    │  poziva metodu servisa
    ▼
[Angular servis .ts]              sastavlja URL i HTTP poziv (HttpClient)
    │  HTTP GET/POST/PUT/DELETE + JSON
    ▼ ─────────── mreža, localhost:8080 ───────────
[CorsConfig]                      propušta zahtev sa porta 4200 (+ kolačić sesije)
    │
    ▼
[Controller .java]                @RestController, mapira rutu, čita parametre
    │  (uz Sesija.zahtevajTip za zaštićene rute)
    ▼
[Service .java]                   poslovna logika, validacija, @Transactional
    │
    ▼
[Repository .java]                Spring Data JPA — SQL se generiše iz metode/@Query
    │
    ▼
[MySQL]                           tabele
    │  entiteti (models/*.java)
    ▼
[DTO .java]                       ono što stvarno ide klijentu (bez lozinki i lenjih veza)
    │  Jackson → JSON
    ▼ ─────────── mreža ───────────
[Angular servis]  →  [Komponenta]  →  [HTML šablon]  →  ekran
```

Ako bilo gde u lancu pukne `PoslovnaGreska`, **`GlobalExceptionHandler`** je
presretne i vrati JSON `{"poruka": "..."}` sa odgovarajućim HTTP statusom
(400/401/403/404/409/500), a komponenta taj tekst prikaže korisniku.

---

## 3. Backend — uloga svakog paketa

`backend/src/main/java/com/example/backend/`

### 3.1 `BackendApplication.java`
Ulazna tačka. `@SpringBootApplication` pokreće ugrađeni Tomcat, skenira pakete i
pravi sve `@Component`/`@Service`/`@RestController`/`@Repository` objekte.

### 3.2 `config/` — podešavanja koja važe za celu aplikaciju

| Fajl | Uloga |
|---|---|
| `AppConfig.java` | Definiše `PasswordEncoder` bean (BCrypt), koji se ubrizgava u servise |
| `CorsConfig.java` | Dozvoljava pozive sa `http://localhost:4200` uz kolačiće (`allowCredentials`), i servira folder `uploads/` na ruti `/uploads/**` |
| `GlobalExceptionHandler.java` | `@RestControllerAdvice` — pretvara izuzetke u JSON `{"poruka": ...}`; hvata `PoslovnaGreska`, greške validacije, pogrešan tip parametra, nepostojeću rutu, preveliki fajl i sve ostalo |

### 3.3 `models/` — JPA entiteti (slika tabela u bazi)

Svaka klasa je `@Entity` i mapira se na jednu tabelu.

| Entitet | Tabela | Ključne veze |
|---|---|---|
| `Korisnik` | `korisnik` | `tip` (enum `TipKorisnika`), `statusRegistracije` |
| `Kategorija`, `Potkategorija` | `kategorija`, `potkategorija` | potkategorija → kategorija |
| `Proizvod` | `proizvod` | → `Korisnik` (štampar), → `Kategorija`, `@ElementCollection dostupneBoje`, `@OneToMany uslugeStampe` |
| `UslugaStampe` | `usluga_stampe` | → `Proizvod` |
| `Narudzbina` | `narudzbina` | → klijent, → štampar, `@OneToMany stavke` |
| `StavkaNarudzbine` | `stavka_narudzbine` | → `Narudzbina`, → `Proizvod`, → `UslugaStampe` |
| `StavkaKorpe` | `stavka_korpe` | e-korpa se čuva u bazi, ne u pregledaču |
| `OcenaProizvoda`, `KomentarProizvoda` | `ocena_proizvoda`, `komentar_proizvoda` | → proizvod, → klijent |
| `JavnaNabavka`, `StavkaNabavke`, `Ponuda` | `javna_nabavka`, `stavka_nabavke`, `ponuda` | nabavka → stavke i ponude |

Enumi: `TipKorisnika`, `StatusRegistracije`, `StatusNarudzbine`, `StatusNabavke`,
`VrednostOcene` — čuvaju se kao tekst (`@Enumerated(EnumType.STRING)`) da bi baza
bila čitljiva.

**Važno:** aplikacija ne pravi tabele (`spring.jpa.hibernate.ddl-auto=none`).
Jedini izvor istine za šemu je `database/printing_house_db.sql`.

### 3.4 `db/dao/` — repozitorijumi (pristup bazi)

Interfejsi koji nasleđuju `JpaRepository`. Spring im pravi implementaciju u letu.
SQL nastaje na dva načina:

1. **Iz imena metode** — `existsByKorIme(String)` → `SELECT ... WHERE kor_ime = ?`
2. **Iz `@Query`** — JPQL upit napisan ručno, kad je potreban `JOIN FETCH`,
   agregacija ili uslov koji se ne da izraziti imenom metode.

| Repozitorijum | Šta radi |
|---|---|
| `KorisnikRepository` | prijava, provere jedinstvenosti, spiskovi za administratora |
| `ProizvodRepository` | TOP 5 po lajkovima, pretraga, detalji, proizvodi štamparije, primljeni proizvodi klijenta |
| `KategorijaRepository`, `PotkategorijaRepository` | padajuće liste |
| `NarudzbinaRepository` | narudžbine klijenta, narudžbine štamparije, arhiva |
| `StavkaKorpeRepository` | e-korpa |
| `OcenaProizvodaRepository`, `KomentarProizvodaRepository` | lajkovi i komentari |
| `JavnaNabavkaRepository`, `PonudaRepository` | javne nabavke i licitacije |
| `UslugaStampeRepository` | usluge štampe uz proizvod |

### 3.5 `service/` — poslovna logika

Tu je sve „pametno": provere, računanje cena, prelazi statusa, transakcije.
Kontroleri ne smeju da sadrže logiku, a repozitorijumi ne znaju za pravila.

| Servis | Odgovornost |
|---|---|
| `AuthService` | provera lozinke (BCrypt), odbijanje neodobrenih naloga, posebna prijava administratora |
| `RegistracijaService` | dvokoračna registracija, validacija MB/PIB-a, status `na_cekanju` |
| `ProfilService` | prikaz i izmena ličnih podataka, provera jedinstvenosti mejla/MB/PIB-a |
| `SlikaService` | upload slika: tip, dimenzije, jedinstveno ime fajla |
| `JavnoService` | početna strana i pretraga |
| `ProizvodService` | detalji proizvoda (javni i prošireni za klijenta) |
| `KorpaService` | e-korpa i zatvaranje narudžbine (jedna faktura po štampariji) |
| `NarudzbinaService` | otkazivanje, potvrda prijema, promena statusa kod štampara |
| `ArhivaService` | isporučeni/primljeni proizvodi, lajkovi, komentari |
| `StamparService` | proizvodi, usluge, količine, aktivnost proizvoda |
| `NabavkaService` | javne nabavke, ponude, zaključivanje licitacije |
| `AdminService` | zahtevi za registraciju, upravljanje nalozima, kategorije |

### 3.6 `controllers/` — REST rute

| Kontroler | Prefiks | Ko sme |
|---|---|---|
| `AuthController` | `/api/auth` | svi (prijava, odjava, registracija) |
| `JavnoController` | `/api/javno` | svi, i neprijavljeni |
| `ProfilController` | `/api/profil` | svaki prijavljen korisnik |
| `KlijentController` | `/api/klijent` | klijenti |
| `StamparController` | `/api/stampar` | štamparije |
| `AdminController` | `/api/admin` | administrator |
| `NabavkaController` | `/api/klijent/nabavke`, `/api/stampar/nabavke` | pravno lice / štamparija |

### 3.7 `dto/` — šta ide preko mreže

Entiteti se **nikad** ne šalju direktno klijentu (u `Korisnik` je hash lozinke, a
lenje veze bi pucale pri serijalizaciji). Umesto toga:

- `*Dto` — odgovor ka klijentu (`KorisnikDto`, `PretragaRedDto`, `NarudzbinaDto`…)
- `*Request` — telo zahteva od klijenta, sa Bean Validation anotacijama
  (`LoginRequest`, `RegistracijaRequest`, `DodajUKorpuRequest`…)
- `PoslovnaGreska` — izuzetak koji nosi HTTP status i poruku za korisnika

### 3.8 `security/Sesija.java`
Pomoćna klasa za rad sa `HttpSession`:
`prijavi`, `odjavi`, `korIme`, `tip`, `zahtevajPrijavu` (401),
`zahtevajTip(...)` (403). Svaki zaštićeni kontroler počinje pozivom ove klase.

### 3.9 `resources/application.properties`
Adresa baze i ime šeme, korisnik i lozinka, `ddl-auto=none`, trajanje sesije,
folder za slike, trajanje licitacije.

---

## 4. Frontend — uloga svakog dela

`frontend/src/`

### 4.1 Pokretanje
| Fajl | Uloga |
|---|---|
| `index.html` | jedina HTML strana; sadrži `<app-root>` |
| `main.ts` | podiže Angular aplikaciju sa `appConfig` |
| `app/app.config.ts` | registruje ruter, `HttpClient` i **`provideAppInitializer`** koji pre prvog rutiranja proveri sesiju na serveru |
| `app/app.ts` / `app.html` | korenska komponenta: `<app-header>`, `<router-outlet>`, `<app-footer>` |
| `app/app.routes.ts` | spisak ruta i koji guard ih čuva |
| `environments/environment*.ts` | adrese backenda (`apiUrl`, `fileUrl`) — razvojna verzija gađa `http://localhost:8080` |
| `styles.css` | globalni CSS: promenljive (`--ph-primarna`…) i klase `.ph-omot`, `.ph-kartica`, `.ph-dugme`… |

### 4.2 `app/models/` — TypeScript tipovi
Ogledalo backend DTO-ova (`Korisnik`, `PretragaRed`, `Narudzbina`, `Nabavka`…).
Nemaju logiku, služe da TypeScript proveri da komponenta koristi polja koja
zaista stižu sa servera.

### 4.3 `app/services/` — komunikacija sa backendom
Jedini sloj koji zna za HTTP. Svaki servis je `@Injectable({providedIn:'root'})`,
dakle jedan primerak za celu aplikaciju.

| Servis | Rute koje poziva |
|---|---|
| `auth.service.ts` | `/auth/*` — prijava, odjava, čuva prijavljenog korisnika u signalu i u `localStorage` |
| `javno.service.ts` | `/javno/*` — početna, kategorije, pretraga, detalji |
| `profil.service.ts` | `/profil` |
| `korpa.service.ts` | `/klijent/korpa*` — drži i brojač stavki za meni |
| `narudzbina.service.ts` | `/klijent/narudzbine*`, `/klijent/arhiva`, ocene i komentari |
| `stampar.service.ts` | `/stampar/*` |
| `nabavka.service.ts` | `/klijent/nabavke`, `/stampar/nabavke` |
| `admin.service.ts` | `/admin/*` |

Svi pozivi ka zaštićenim rutama idu sa `withCredentials: true` — bez toga
pregledač ne bi poslao kolačić sesije.

### 4.4 `app/guards/auth.guard.ts`
Tri funkcije koje ruter zove pre ulaska na rutu:
- `samoGost` — ako je već prijavljen, vodi ga na njegovu početnu
- `samoPrijavljeni` — traži bilo koji nalog
- `dozvoljenTip([...])` — traži tačno određene tipove naloga

Ovo je **samo udobnost za korisnika**; prava zaštita je na serveru
(`Sesija.zahtevajTip`), jer se guard može zaobići.

### 4.5 `app/components/` — strane
Jedna komponenta = jedna strana (ili deo strane). Svaka ima tri fajla:
`.ts` (stanje i logika), `.html` (šablon), `.css` (izgled).

```
components/
├─ javno/        pocetna, pretraga, detalji-proizvoda
├─ auth/         login, login-admin, registracija
├─ profil/       profil (uključuje i tabelu narudžbina)
├─ klijent/      pocetna, korpa, narudzbine (tabela), arhiva, nabavke
├─ stampar/      pocetna, proizvodi, novi-proizvod, narudzbine, nabavke
└─ admin/        pocetna, zahtevi, korisnici, kategorije
```

### 4.6 `app/shared/`
`header` (meni, ime korisnika, brojač korpe, odjava) i `footer`. Ugrađeni su u
`app.html`, pa se vide na svakoj strani.

---

## 5. Baza podataka

Sve pravi `database/printing_house_db.sql` — tabele, strani ključevi i test
podaci. Skripta počinje sa `DROP DATABASE IF EXISTS printing_house`, pa se može
pokrenuti više puta i uvek vraća isto početno stanje.

Tabele: `korisnik`, `kategorija`, `potkategorija`, `proizvod`, `proizvod_boja`,
`proizvod_slika`, `usluga_stampe`, `narudzbina`, `stavka_narudzbine`,
`stavka_korpe`, `ocena_proizvoda`, `komentar_proizvoda`, `javna_nabavka`,
`stavka_nabavke`, `ponuda`.

Lozinka za sve test naloge: `Test123!`

---

## 6. Presečne teme

### Prijava i sesija
1. `POST /api/auth/login` → `AuthService` proverava BCrypt hash i status naloga.
2. `Sesija.prijavi` upisuje korisničko ime i tip u `HttpSession`.
3. Tomcat vrati kolačić `JSESSIONID`; pregledač ga dalje šalje sam.
4. Angular kopiju korisnika drži u signalu i `localStorage` — **samo za prikaz**;
   pri svakom pokretanju `provideAppInitializer` pozove `/api/auth/ja` da proveri
   da li sesija još važi.

### Slike
Ne čuvaju se u bazi, nego u folderu `backend/uploads/` (`profilne/`, `proizvodi/`).
U bazi stoji samo naziv fajla. `CorsConfig` ih servira na `/uploads/**`, a
frontend ih traži preko `environment.fileUrl`.

### Greške
Servisi bacaju `PoslovnaGreska(HttpStatus, poruka)`. `GlobalExceptionHandler` to
pretvara u JSON, a komponenta prikaže `err.error.poruka`. Zato korisnik nigde ne
vidi Java stack trace.

---

## 7. Tri primera toka kroz fajlove

### A) Pretraga proizvoda (bez prijave)

| # | Fajl | Šta se dešava |
|---|---|---|
| 1 | `components/javno/pretraga/pretraga.html` | korisnik ukuca naziv i pritisne „Pretraži" → `(ngSubmit)="pretrazi()"` |
| 2 | `components/javno/pretraga/pretraga.ts` | metoda `pretrazi()` zove servis |
| 3 | `services/javno.service.ts` | `GET http://localhost:8080/api/javno/proizvodi?naziv=...&kategorijaId=...` |
| 4 | `config/CorsConfig.java` | propušta zahtev sa porta 4200 |
| 5 | `controllers/JavnoController.java` | `@GetMapping("/proizvodi")`, čita `@RequestParam` |
| 6 | `service/JavnoService.java` | prazan naziv pretvara u `null` i prosleđuje dalje |
| 7 | `db/dao/ProizvodRepository.java` | `@Query` `pretrazi(...)` — JPQL sa `LEFT JOIN` na ocene, uslovima `aktivan = true` i `kolicinaNaLageru > 0` |
| 8 | MySQL | vrati redove |
| 9 | `dto/PretragaRedDto.java` | JPQL `SELECT new ...` puni DTO direktno |
| 10 | Jackson | DTO → JSON niz |
| 11 | `services/javno.service.ts` | `Observable` emituje `PretragaRed[]` |
| 12 | `pretraga.ts` | `rezultati.set(redovi)` — signal |
| 13 | `pretraga.html` | `@for` iscrtava tabelu; klik na zaglavlje sortira preko `computed()` |

### B) Dodavanje u korpu (prijavljen klijent)

`detalji-proizvoda.html` → `detalji-proizvoda.ts` → `korpa.service.ts`
(`POST /api/klijent/korpa`, `withCredentials: true`) → `KlijentController`
(`Sesija.zahtevajTip(klijent_fizicko, klijent_pravno)`) → `KorpaService.dodaj`
(proverava boju, uslugu i lager) → `StavkaKorpeRepository.save` → MySQL →
`KorpaDto` → servis ažurira signal sa brojem stavki → **header odmah promeni
brojač na korpi**, jer čita isti signal.

### C) Zaključivanje javne nabavke

Institucija otvori `/klijent/nabavke` → `nabavka.service.ts`
(`GET /api/klijent/nabavke`) → `NabavkaController` → `NabavkaService.mojeNabavke`
→ prvo pozove `zakljuciIstekle()`, koje za svaku nabavku kojoj je istekao rok
uzme ponude sortirane po ceni, proveri da li štamparija ima dovoljno proizvoda na
stanju, i za prvu koja ima napravi `Narudzbina` u statusu `u_stampi`, umanji
lager i označi ponudu kao pobedničku → tek onda vrati listu. Zato nije potreban
tajmer ni WebSocket.
