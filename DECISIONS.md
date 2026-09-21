# Odluke o projektu (Printing House)

Log svih odluka koje specifikacija ne definiše precizno. Svaki novi red dodati
**čim** se odluka donese (ne čekati kraj projekta) — i ti i Copilot treba da
proveravate ovaj fajl pre nego što nešto dvosmisleno implementirate.

Format: `Datum | Oblast | Odluka | Zašto / alternative`

| Datum | Oblast | Odluka | Zašto / alternative |
|---|---|---|---|
| 2026-09-19 | Pristup bazi | Spring Data **JPA + Hibernate** | Specifikacija eksplicitno traži ORM. Alternativa (čist JDBC kao u `klk_jul25`/lab vežbama) je poznatija, ali tehnički ne ispunjava napisan zahtev. |
| 2026-09-19 | Relaciona baza | **MySQL** | Isto kao primer `lab2_2025_2026_DB_banka.sql` i `pom.xml` iz `klk_jul25` (mysql-connector-j). PostgreSQL je bio dozvoljena alternativa. |
| 2026-09-19 | Karakter set baze | **utf8mb4** (ne `utf16` kao u starom primeru skripte) | `utf8mb4` je standardan izbor za MySQL (podržava i emoji/šire skupove), `utf16` iz starog primera je neuobičajen/redak izbor bez posebne potrebe ovde. Lako promenljivo ako se ispostavi da treba drugačije. |
| 2026-09-19 | Struktura kontrolera | **Po jedan kontroler po celini** (AuthController, KlijentController, StamparController, AdminController, ProizvodController...), ne jedan `Contoller.java` kao u starim malim vežbama | Ovaj projekat ima ~30 endpointa kroz 4 tipa korisnika — jedan fajl bi bio nepregledan. |
| 2026-09-19 | Servisni sloj | Dodat `service/` paket (poslovna logika odvojena od kontrolera) | U starim, sitnim vežbama nije postojao (kontroler je pozivao DAO direktno); ovde ima smisla zbog složenije logike (licitacije, fakturisanje, mejlovi). |
| 2026-09-19 | Naziv paketa za repozitorijume | Zadržan naziv `db.dao` (iako su sada Spring Data `JpaRepository` interfejsi, ne ručni DAO) | Kontinuitet sa organizacijom na koju je korisnica navikla. Lako promenljivo u `db.repository` kasnije ako zasmeta. |
| 2026-09-19 | Model korisnika | Predlog: jedan `Korisnik` entitet/tabela sa `tip` diskriminatorom (klijent-fizičko/klijent-pravno/štampar/administrator) + nullable polja za pravna lica/štamparije (naziv institucije, adresa, matični broj, PIB) | Slično kao stara `korisnici` tabela (kolona `tip`) iz primer-a, samo prošireno poljima za institucije. **Nije konačno** — ako se ispostavi nezgodno (npr. previše nullable kolona), razdvojiti u zasebne tabele/entitete i ažurirati ovaj red. |
| 2026-09-19 | Backend URL na frontu | `environment.ts` / `environment.development.ts` sa `apiUrl`, umesto hardkodovanog `http://localhost:8080` po svakom servisu | Mala izmena u odnosu na stare projekte — lakše za promenu porta/domena kasnije. |
| 2026-09-19 | GitHub Copilot model | Videti napomenu na dnu ovog fajla | — |
| 2026-09-21 | Autentifikacija | **`HttpSession` na backendu + kopija korisnika u `localStorage` na frontu.** Angular servisi šalju `withCredentials: true`; zaštićeni endpointi čitaju korisnika iz sesije. | Front ostaje isti kao na vežbama (`localStorage.setItem/getItem`), ali backend stvarno zna ko je prijavljen — čista `localStorage` varijanta se može zaobići ručnim pozivom API-ja, a specifikacija traži serversku validaciju. JWT je bio alternativa: više koda (filter, secret, istek tokena, interceptor) bez stvarne koristi za ovaj obim. |
| 2026-09-21 | Spring Security | **Ne koristi se `spring-boot-starter-security`**, samo biblioteka `spring-security-crypto` zbog `BCryptPasswordEncoder`. | Starter podrazumevano zaključava sve rute, dodaje svoju login stranu i CSRF — trebalo bi pisati `SecurityFilterChain` samo da bi se to isključilo, dok proveru ionako radimo sami kroz sesiju. |
| 2026-09-21 | Ruta za admin prijavu | Angular: **`/admin-login`**; backend: `POST /api/auth/login-admin`. Ne linkuje se ni sa jedne javne strane. | Admin *stranice* idu pod prefiks `/admin/...` iza guard-a — da je login bio `/admin/login`, trebao bi izuzetak u guard-u za samu login stranu. |
| 2026-09-21 | Prefiks REST ruta | Svi endpointi pod **`/api/...`** (npr. `/api/auth/login`) | Jasno odvaja API od eventualnog statičkog sadržaja i pojednostavljuje CORS/proxy podešavanje. |
| 2026-09-21 | Konekcija ka bazi | Obrisan ručni `DataSource` bean (`db/DB.java`); konekcija se konfiguriše u `application.properties`, sa `${DB_USER:root}` / `${DB_PASS:}` placeholder-ima. | Ručni `DriverManagerDataSource` bean se sudara sa JPA auto-konfiguracijom i hardkoduje kredencijale u kod. Lokalno radi bez ikakvog podešavanja, a na drugoj mašini je dovoljno postaviti env promenljive. |
| 2026-09-21 | Test lozinka u seed podacima | Svi seed nalozi u `database/printing_house_db.sql` imaju lozinku **`Test123!`** (upisan pravi BCrypt hash). | Lozinka zadovoljava traženi regex (počinje slovom, 8–12 karaktera, veliko slovo, broj, specijalni karakter) — ista za sve naloge radi lakšeg testiranja. |
| 2026-09-21 | Prijava neodobrenih naloga | Korisnik sa `status_registracije` = `na_cekanju` / `odbijen` **ne može** da se prijavi — dobija jasnu poruku umesto pristupa. | Specifikacija kaže da registracija čeka odobrenje administratora; bez ove provere odobrenje ne bi imalo efekta. |
| 2026-09-21 | Admin prijava | Administrator se **ne može** prijaviti kroz javnu formu `/login`, niti se bilo koji drugi tip korisnika može prijaviti kroz `/admin-login`. | Specifikacija traži da admin prijava bude odvojena i javno nevidljiva. |

## Otvorena pitanja (popuniti kad se odluči)

- [ ] Tačna šema tabela za `proizvod` ↔ `usluga_stampe` (many-to-many preko
      spojne tabele `proizvod_usluga` sa cenom/dimenzijama po kombinaciji?).
- [ ] Kako se čuva "trenutna e-korpa" pre potvrde narudžbine — posebna tabela
      (`stavka_korpe`) ili samo state na frontu dok se ne pritisne "POTVRDI"?
- [ ] Koja biblioteka za generisanje PDF-a (fakture, izveštaji) — kad se dođe do
      te bonus stavke.
- [ ] Koja biblioteka/servis za slanje mejlova (reset lozinke, obaveštenje o
      javnoj nabavci, fakture) — Spring Mail + koji SMTP za test.
- [x] Format rute za admin prijavu — **`/admin-login`** (vidi tabelu, 2026-09-21).
- [ ] Tačna Java verzija za Spring Boot 3.5.x u laboratorijskom/ispitnom
      okruženju (`java.version=25` radi na razvojnoj mašini — OpenJDK 25.0.4.1 —
      ali proveriti da li je isto dostupno na fakultetskim računarima za odbranu;
      ako nije, spustiti na 21 u `pom.xml`).

## Napomena o GitHub Copilot modelu

Korisnica ima **Copilot Pro**. Preporuka (detaljno obrazloženje je i u poruci u
razgovoru): kao podrazumevani model za većinu rada koristiti jači model iz
Claude ili GPT porodice koji je dobar za multi-file/agentic rad i praćenje ovih
instrukcija (npr. Claude Sonnet 5 ili GPT-5.3-Codex — dostupnost i tačna imena
modela u Copilot padajućoj listi se s vremena na vreme menjaju, proveriti šta je
trenutno ponuđeno u VS Code Copilot Chat). Za rutinske/proste izmene (getteri,
sitne popravke) može i jeftiniji/"mini"/Auto model radi štednje mesečne kvote
premium zahteva.
