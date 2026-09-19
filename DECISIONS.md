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

## Otvorena pitanja (popuniti kad se odluči)

- [ ] Tačna šema tabela za `proizvod` ↔ `usluga_stampe` (many-to-many preko
      spojne tabele `proizvod_usluga` sa cenom/dimenzijama po kombinaciji?).
- [ ] Kako se čuva "trenutna e-korpa" pre potvrde narudžbine — posebna tabela
      (`stavka_korpe`) ili samo state na frontu dok se ne pritisne "POTVRDI"?
- [ ] Koja biblioteka za generisanje PDF-a (fakture, izveštaji) — kad se dođe do
      te bonus stavke.
- [ ] Koja biblioteka/servis za slanje mejlova (reset lozinke, obaveštenje o
      javnoj nabavci, fakture) — Spring Mail + koji SMTP za test.
- [ ] Format rute za admin prijavu (npr. `/admin-login`) — finalizovati naziv.
- [ ] Tačna Java verzija za Spring Boot 3.5.x u laboratorijskom/ispitnom
      okruženju (stari primer koristi `java.version=25` — proveriti da li je to
      dostupno i na fakultetskim računarima za odbranu).

## Napomena o GitHub Copilot modelu

Korisnica ima **Copilot Pro**. Preporuka (detaljno obrazloženje je i u poruci u
razgovoru): kao podrazumevani model za većinu rada koristiti jači model iz
Claude ili GPT porodice koji je dobar za multi-file/agentic rad i praćenje ovih
instrukcija (npr. Claude Sonnet 5 ili GPT-5.3-Codex — dostupnost i tačna imena
modela u Copilot padajućoj listi se s vremena na vreme menjaju, proveriti šta je
trenutno ponuđeno u VS Code Copilot Chat). Za rutinske/proste izmene (getteri,
sitne popravke) može i jeftiniji/"mini"/Auto model radi štednje mesečne kvote
premium zahteva.
