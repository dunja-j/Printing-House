---
applyTo: "backend/**"
---

# Backend konvencije (Spring Boot + JPA)

Ovaj projekat nastavlja organizaciju paketa koju korisnica koristi u svojim ranijim
Spring Boot projektima (`com.example.backend`), prilagođenu za Spring Data JPA i
veći obim ovog projekta (jedan Controller.java kao u starim vežbama ovde NE bi
imao smisla — previše endpointa).

## Paketi

```
com.example.backend
├── BackendApplication.java
├── config/          // CORS config, PasswordEncoder bean, eventualni security config
├── controllers/      // po jedan REST kontroler po celini (ne jedan ogroman fajl)
│   ├── AuthController.java          // /login, /login-admin, /registracija, /zaboravljena-lozinka, /reset-lozinke
│   ├── KlijentController.java
│   ├── StamparController.java
│   ├── AdminController.java
│   ├── ProizvodController.java      // javna pretraga/detalji
│   └── ...
├── models/            // JPA @Entity klase
├── dto/               // request/response objekti kad se oblik razlikuje od entiteta
├── db/dao/            // Spring Data repozitorijumi (JpaRepository), zadržan naziv
│                       // paketa "dao" iz starih projekata radi kontinuiteta
├── service/           // poslovna logika (nova u odnosu na stare, sitne vežbe —
│                       // ovde ima smisla imati servisni sloj jer je logika veća:
│                       // npr. logika licitacije, generisanje fakture, slanje mejla)
└── security/          // JWT/session utili, ako se koristi tokenska autentifikacija
```

## Entiteti — JPA napomene

- Anotacije `@Entity`, `@Table(name = "...")`, `@Id` (matirati imena kolona iz
  `database/printing_house_db.sql` preko `@Column(name = "...")` kad se razlikuju
  od Java naming konvencije).
- **`spring.jpa.hibernate.ddl-auto=none`** (ili `validate`) u
  `application.properties` — baza se kreira isključivo preko SQL skripte, app je
  nikad ne kreira/menja sama (eksplicitan zahtev iz specifikacije projekta).
- Nazivi Java polja u `Proizvod` i `UslugaStampe` entitetima treba da prate isti
  koncept kao JSON iz Priloga 1 (`sifra`, `naziv`, `opis`, `kategorija`,
  `potkategorija`, `jedinicnaCena`, `kolicinaNaLageru`, `dostupneBoje`,
  `slikaUrl`, `dodatneSlike`, `uslugeStampe`) da bi uvoz iz JSON fajla bio
  jednostavan (Jackson deserijalizacija direktno u DTO istog oblika, pa mapiranje
  u entitet).
- Tri tipa korisnika (klijent-fizičko, klijent-pravno, štampar, administrator) —
  predloženo: `Korisnik` kao bazni entitet/tabela sa `tip` diskriminatorom
  (nasleđivanje: `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` ili
  `JOINED`), plus polja koja postoje samo za pravna lica/štamparije (naziv
  institucije, adresa, matični broj, PIB) kao nullable kolone. Ovo je predlog —
  ako se odluči drugačije, upisati u `DECISIONS.md`.

## Repozitorijumi (`db/dao`)

- Interfejsi koji nasleđuju `JpaRepository<Entitet, TipKljuca>`.
- Custom upiti preko `@Query` (JPQL) ili derived query metoda
  (`findByKorisnickoIme`, itd.) — izbegavati native SQL osim kad je zaista
  neophodno (npr. složena statistika za grafikone).

## Bezbednost / validacija

- Lozinka: hash pre upisa u bazu (`BCryptPasswordEncoder`), nikad plain text.
- Regex validacija lozinke i na frontu i na backu (ista pravila, videti glavni
  `copilot-instructions.md`).
- Server-side validacija za sve unose (Bean Validation `@Valid` + `@NotNull`,
  `@Pattern`, itd. na DTO-ovima), ne oslanjati se samo na frontend proveru.
- Upload slika: proveriti tip fajla (JPG/PNG/GIF) i dimenzije pre čuvanja.

## application.properties

- Konekcija ka MySQL (`spring.datasource.url/username/password`), ovi podaci se
  **ne commit-uju sa pravim kredencijalima** ako repo ide na javni GitHub — koristiti
  placeholder vrednosti i/ili `application-local.properties` (dodato u
  `.gitignore`) za stvarne kredencijale na razvojnoj mašini.

## Testiranje pri razvoju

- Kad dodaješ novu funkcionalnost, prvo proveri da li već postoji odgovarajuća
  tabela u `database/printing_house_db.sql` — ako ne postoji, dodaj je tamo (uz
  par redova test podataka), pa tek onda piši Entity/Repository/Controller kod.
