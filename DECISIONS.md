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
| 2026-09-21 | Čuvanje uploadovanih slika | **Fajl na disku**, u bazi samo naziv fajla. Folder `backend/uploads/profilne/`, Spring ga servira preko `/uploads/profilne/...`. | Baza ostaje čitljiva (SQL skripta se održava ručno), a isti mehanizam se koristi i za slike proizvoda. Alternativa (BLOB kolona) bi skriptu učinila ogromnom. |
| 2026-09-21 | URL do slika na frontu | Dodat `environment.fileUrl` (`http://localhost:8080/uploads` u razvoju) pored `apiUrl`. | Slike se ne serviraju pod `/api` prefiksom, pa im treba zasebna bazna putanja. |
| 2026-09-21 | Tok registracije | **Dva koraka**: `POST /api/auth/registracija` (JSON) kreira nalog, pa `POST /api/auth/registracija/slika` (multipart) dodaje profilnu sliku. Slika je opciona — ako se preskoči, ostaje `default_profile_image.jpg`. | Isti mehanizam za upload slike treba i kod ažuriranja profila (#8) i kod slika proizvoda (#21), pa se odvajanjem izbegava dupliranje logike. |
| 2026-09-21 | Zaštita drugog koraka registracije | Prvi korak upisuje `korIme` u `HttpSession` (atribut `registracija`); drugi korak sliku vezuje isključivo za to ime iz sesije, ne za ono iz URL-a/forme. | Bez ovoga bi svako mogao da pošalje sliku za tuđe korisničko ime, jer korisnik u ovom trenutku još nije prijavljen (nalog čeka odobrenje). |
| 2026-09-21 | Polje `grad` pri registraciji | **Obavezno** za štamparije i pravna lica, **neobavezno** za fizička lica. | Specifikacija ga ne navodi u spisku polja, ali ga traži kod detalja proizvoda (#6: "naziv, štamparija, grad") i mape lokacije štamparije (#12) — mora odnekud da se unese. |
| 2026-09-21 | Regex za lozinku | `^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d])[A-Za-z]\S{7,11}$` — identičan na frontu i backu. | Pokriva sve uslove iz specifikacije (8–12 karaktera, veliko slovo, cifra, specijalan karakter, počinje slovom) i dodatno zabranjuje razmak. |
| 2026-09-21 | Ostali regex-i pri registraciji | Korisničko ime `^[A-Za-z0-9._-]{3,45}$`, matični broj `^\d{8}$`, PIB `^[1-9]\d{8}$`, telefon `^[0-9+\s\-/()]{6,30}$`. | PIB regex direktno pokriva zahtev "9 cifara, ne sme počinjati nulom". Ostali su razumna ograničenja da bi unos bio čist. |
| 2026-09-21 | Validacija profilne slike | Dozvoljeni JPG/PNG/GIF, dimenzije između 100×100 i 250×250 px, max 2 MB; provera se radi **na serveru** čitanjem same slike (`ImageIO`), ne samo po ekstenziji. | Ekstenzija i `Content-Type` se lako lažiraju; čitanjem slike se istovremeno dobijaju i stvarne dimenzije koje specifikacija ograničava. |
| 2026-09-21 | Broj štamparija na početnoj | Broje se **samo odobrene** štamparije (`status_registracije = 'odobren'`). | Štamparija na čekanju/odbijena još nije deo platforme i nema proizvode u ponudi. |
| 2026-09-21 | TOP 5 proizvoda | Rangiranje po broju **lajkova** opadajuće, pri izjednačenom broju abecedno po nazivu. Ulaze samo proizvodi sa `aktivan = 1`, bez obzira na količinu na lageru. | Specifikacija kaže "po broju lajkova" — dislajkovi se ne oduzimaju. Abecedni tie-break daje stabilan (uvek isti) redosled, umesto nasumičnog. Lager se menja iz sata u sat, pa bi lista stalno "treperila". |
| 2026-09-21 | Slike proizvoda u seed podacima | U repo su commitovane placeholder slike `backend/uploads/proizvodi/seed_*.jpg` + `default_product_image.jpg`; ostale uploadovane slike su u `.gitignore`. | Bez njih bi SQL skripta na novoj mašini pokazivala polomljene slike. Zamenljive pravim fotografijama pred odbranu. |
| 2026-09-21 | Sortiranje rezultata pretrage | Radi se **na klijentu** (Angular `computed`), server uvek vraća rezultat sortiran po nazivu rastuće. | Rezultat je već u memoriji pregledača, pa je sortiranje trenutno i bez novog HTTP poziva. Broj proizvoda po štampariji je mali, nema potrebe za stranicenjem. |
| 2026-09-21 | Šta pretraga vraća | Rezultati sadrže sve **aktivne** proizvode, i one sa nulom na lageru (količina je prikazana kao kolona). Padajuća lista kategorija, međutim, nudi samo kategorije sa proizvodima **na stanju**. | Za listu kategorija je to eksplicitan zahtev specifikacije; za same rezultate nije propisano, a korisno je videti i trenutno rasprodat proizvod. |
| 2026-09-21 | Pretraga po nazivu | Delimično poklapanje (`LIKE %...%`), neosetljivo na velika/mala slova. Zahvaljujući `utf8mb4_unicode_ci` kolaciji, "solja" pronalazi i "šolja". | Korisnik retko zna tačan pun naziv, a na tastaturi često nema naših slova. |
| 2026-09-21 | Javni detalji proizvoda | Prikazuju naziv, šifru, štampariju, grad/adresu, lajkove/dislajkove, kategoriju, cenu, lager, opis i jednu sliku. Boje, usluge štampe i max dimenzije **nisu** tu — to je prošireni prikaz za prijavljenog klijenta (#11). | Specifikacija za javnu stranu navodi baš taj uži skup podataka, a detalje za poručivanje vezuje za deo za klijente. |
| 2026-09-22 | Profil korisnika | **Jedna** komponenta `components/profil/` i jedna ruta `/profil` za sve tipove naloga (features #8 i #20), umesto zasebnih pod `klijent/` i `stampar/`. Polja institucije se prikazuju uslovno, prema `tip`-u. | Forma i endpointi su identični za klijenta i štampara — razdvajanje bi značilo dupliran kod na dva mesta koji se mora održavati paralelno. |
| 2026-09-22 | Šta se može menjati na profilu | Sve osim **korisničkog imena** (primarni ključ) i **tipa naloga**. Matični broj i PIB se mogu ispraviti, uz proveru jedinstvenosti koja ignoriše sam nalog. | Korisničko ime je strani ključ u tabelama proizvoda, narudžbina i ocena — promena bi zahtevala kaskadno ažuriranje bez stvarne koristi. Tip naloga menja skup obaveznih polja i prava pristupa. |
| 2026-09-22 | Promena lozinke | **Nije** deo profila. | Specifikacija je ne navodi ni u opisu profila ni u minimalnim zahtevima. Može se dodati naknadno ako ostane vremena. |
| 2026-09-22 | Keširanje profilne slike | Uz URL slike se šalje `?v=<timestamp>` koji se menja posle svakog uploada. | Naziv fajla se menja pri svakoj izmeni, ali pregledač je ipak znao da prikaže staru sliku iz keša; ovako je prikaz uvek svež. |
| 2026-09-22 | Otkazivanje narudžbine | Dodat status **`otkazano`** u `narudzbina.status` ENUM; narudžbina se **ne briše** iz baze. | Brisanje bi uništilo istoriju i referencirane stavke. Otkazana narudžbina ostaje vidljiva klijentu i štampariji, samo je precrtana. |
| 2026-09-22 | Zaštita tuđih narudžbina | Pokušaj otkazivanja tuđe narudžbine vraća **404 sa istom porukom** kao nepostojeća, a ne 403. | Različite poruke bi otkrile koji ID-jevi postoje u sistemu. |
| 2026-09-22 | Provera tipa naloga na backendu | Dodate `Sesija.zahtevajPrijavu()` i `Sesija.zahtevajTip()` — svaki zaštićen endpoint ih poziva na početku. | Ista provera se ponavljala po kontrolerima; ovako je na jednom mestu i teže je zaboraviti je. |
| 2026-09-22 | Vraćanje na lager pri otkazivanju | **Ne radi se** za sada. | Lager se još nigde ne umanjuje — to počinje tek sa e-korpom (#15). Vraćanje će se dodati zajedno sa umanjenjem, da logika bude na jednom mestu. |
| 2026-09-22 | Prošireni detalji proizvoda | **Ista ruta** `/proizvod/:id` i ista komponenta za sve — ako je prijavljen klijent, poziva se `GET /api/klijent/proizvodi/{id}` i prikazuje dodatna sekcija; inače `GET /api/javno/proizvodi/{id}`. | Duplirana strana bi značila dva šablona sa 90% istog sadržaja. Boje i usluge štampe ne izlaze na javni endpoint, pa ih neprijavljen posetilac ne može dobiti ni direktnim pozivom API-ja. |
| 2026-09-22 | Pretraga za klijenta | Klijent koristi **istu** pretragu `/pretraga` kao i javni posetilac (#5); razlika je samo u detaljima proizvoda. | Specifikacija za klijenta traži "pretraživanje proizvoda i detalje" — sami kriterijumi pretrage su isti, pa nema razloga za drugu stranu. |
| 2026-09-22 | `ProizvodService` | Detalji proizvoda (javni i prošireni) izmesteni iz `JavnoService` u novi `ProizvodService`. | Obe varijante dele isto traženje proizvoda i brojanje lajkova/dislajkova; ovako je računanje na jednom mestu. |
| 2026-09-22 | Čuvanje e-korpe | **Tabela `stavka_korpe` u bazi**, ne stanje na frontu. | Korpa preživljava odjavu i zatvaranje pregledača, a cene računa isključivo server — klijent ne može da pošalje svoju cenu. Alternativa (`localStorage`) bi bila kraća za pisanje, ali bi zahtevala da se ceo sadržaj korpe ionako ponovo validira na serveru pri zatvaranju. |
| 2026-09-22 | Cene u korpi | Cena stavke se **nikad ne šalje sa fronta** — server je računa kao `(jedinična cena + doplata za uslugu) × količina` i pri prikazu i pri zatvaranju narudžbine. | Kad bi cena stizala sa klijenta, mogla bi se izmeniti kroz alatke pregledača. |
| 2026-09-22 | Zatvaranje narudžbine | Od svake **štamparije** u korpi nastaje zasebna `narudzbina` (= zasebna faktura), sve u jednoj transakciji. Lager se umanjuje tek tada. | Specifikacija traži "jedna faktura po štampariji". Rezervisanje lagera pri dodavanju u korpu bi zahtevalo i logiku oslobadjanja zaboravljenih korpi. |
| 2026-09-22 | Pravno lice i korpa | Pravno lice može da puni korpu, ali **ne može** da zatvori narudžbinu — dobija poruku da porudžbina ide u javnu nabavku. | Specifikacija izričito kaže da za pravna lica narudžbina ide na licitaciju (#18), a ne direktno u fakturu. |
| 2026-09-22 | Obavezna polja pri poručivanju | Boja je obavezna **samo ako** proizvod ima definisane boje; usluga štampe **samo ako** proizvod ima definisane usluge. Tekst za štampu je uvek opcion. | Neki proizvodi (npr. flajer) nemaju boje — tražiti izbor bi blokiralo poručivanje. |

## Otvorena pitanja (popuniti kad se odluči)

- [ ] Tačna šema tabela za `proizvod` ↔ `usluga_stampe` (many-to-many preko
      spojne tabele `proizvod_usluga` sa cenom/dimenzijama po kombinaciji?).
- [x] Kako se čuva "trenutna e-korpa" — **tabela `stavka_korpe` u bazi** (vidi tabelu, 2026-09-22).
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
