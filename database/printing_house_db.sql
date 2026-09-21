-- =====================================================================
-- Printing House — inicijalna skripta baze podataka
-- =====================================================================
-- Ovo NIJE kompletna šema za ceo projekat — sadrži tabele potrebne za prve
-- obavezne funkcionalnosti (prijava/registracija, katalog proizvoda,
-- narudžbine, lajkovi/komentari). Dopunjavati je kako se dodaju nove
-- funkcionalnosti (npr. javne nabavke/licitacije, statistika) — videti
-- FEATURES.md za plan i DECISIONS.md za obrazloženje odluka o šemi.
--
-- Aplikacija bazu NE kreira sama (spring.jpa.hibernate.ddl-auto=none) —
-- ova skripta je jedini izvor istine za strukturu baze.
-- =====================================================================

DROP DATABASE IF EXISTS `printing_house`;
CREATE DATABASE IF NOT EXISTS `printing_house` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `printing_house`;

-- ---------------------------------------------------------------------
-- Korisnici (klijent-fizičko lice, klijent-pravno lice, štampar, administrator)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `korisnik`;
CREATE TABLE IF NOT EXISTS `korisnik` (
  `kor_ime` VARCHAR(45) NOT NULL,
  `lozinka_hash` VARCHAR(100) NOT NULL,
  `ime` VARCHAR(45) NOT NULL,
  `prezime` VARCHAR(45) NOT NULL,
  `telefon` VARCHAR(30) NULL,
  `mejl` VARCHAR(100) NOT NULL,
  `tip` ENUM('klijent_fizicko','klijent_pravno','stampar','administrator') NOT NULL,
  `slika_url` VARCHAR(255) NOT NULL DEFAULT 'default_profile_image.jpg',
  -- polja samo za pravna lica (klijent_pravno) i štampare:
  `naziv_institucije` VARCHAR(150) NULL,
  `adresa_sedista` VARCHAR(200) NULL,
  `grad` VARCHAR(100) NULL,
  `maticni_broj` CHAR(8) NULL,
  `pib` CHAR(9) NULL,
  -- status registracije (klijent/štampar); administrator se ne registruje kroz UI:
  `status_registracije` ENUM('na_cekanju','odobren','odbijen') NOT NULL DEFAULT 'odobren',
  `datum_registracije` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`kor_ime`),
  UNIQUE KEY `uq_korisnik_mejl` (`mejl`),
  UNIQUE KEY `uq_korisnik_maticni_broj` (`maticni_broj`),
  UNIQUE KEY `uq_korisnik_pib` (`pib`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Kategorije / potkategorije proizvoda (predefinisane u opisu projekta)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `potkategorija`;
DROP TABLE IF EXISTS `kategorija`;

CREATE TABLE IF NOT EXISTS `kategorija` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `naziv` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_kategorija_naziv` (`naziv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `potkategorija` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `kategorija_id` INT NOT NULL,
  `naziv` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_potkategorija` (`kategorija_id`, `naziv`),
  CONSTRAINT `fk_potkategorija_kategorija` FOREIGN KEY (`kategorija_id`) REFERENCES `kategorija` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `kategorija` (`naziv`) VALUES
  ('Štampa malih formata'),
  ('Štampa velikih formata'),
  ('Kreativne štampe');

INSERT INTO `potkategorija` (`kategorija_id`, `naziv`) VALUES
  ((SELECT id FROM kategorija WHERE naziv='Štampa malih formata'), 'Olovke'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa malih formata'), 'Vizit karte'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa malih formata'), 'Flajeri'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa malih formata'), 'Zahvalnice'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa malih formata'), 'Pozivnice'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa malih formata'), 'Fascikle'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa velikih formata'), 'Posteri'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa velikih formata'), 'Rollups'),
  ((SELECT id FROM kategorija WHERE naziv='Štampa velikih formata'), 'Fototapete'),
  ((SELECT id FROM kategorija WHERE naziv='Kreativne štampe'), 'Šolje'),
  ((SELECT id FROM kategorija WHERE naziv='Kreativne štampe'), 'Štampa na majicama'),
  ((SELECT id FROM kategorija WHERE naziv='Kreativne štampe'), 'Štampa na duksevima'),
  ((SELECT id FROM kategorija WHERE naziv='Kreativne štampe'), 'Štampa na cegerima');

-- ---------------------------------------------------------------------
-- Proizvodi (nudi ih štampar), boje, dodatne slike, usluge štampe
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `usluga_stampe`;
DROP TABLE IF EXISTS `proizvod_slika`;
DROP TABLE IF EXISTS `proizvod_boja`;
DROP TABLE IF EXISTS `proizvod`;

CREATE TABLE IF NOT EXISTS `proizvod` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `sifra` VARCHAR(30) NOT NULL,
  `stampar_kor_ime` VARCHAR(45) NOT NULL,
  `naziv` VARCHAR(150) NOT NULL,
  `opis` TEXT NULL,
  `kategorija_id` INT NOT NULL,
  `potkategorija_id` INT NULL,
  `jedinicna_cena` DECIMAL(10,2) NOT NULL,
  `kolicina_na_lageru` INT NOT NULL DEFAULT 0,
  `slika_url` VARCHAR(255) NULL,
  `aktivan` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_proizvod_sifra_stampar` (`sifra`, `stampar_kor_ime`),
  CONSTRAINT `fk_proizvod_stampar` FOREIGN KEY (`stampar_kor_ime`) REFERENCES `korisnik` (`kor_ime`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_proizvod_kategorija` FOREIGN KEY (`kategorija_id`) REFERENCES `kategorija` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_proizvod_potkategorija` FOREIGN KEY (`potkategorija_id`) REFERENCES `potkategorija` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `proizvod_boja` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `proizvod_id` INT NOT NULL,
  `boja` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_boja_proizvod` FOREIGN KEY (`proizvod_id`) REFERENCES `proizvod` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `proizvod_slika` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `proizvod_id` INT NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `redosled` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_slika_proizvod` FOREIGN KEY (`proizvod_id`) REFERENCES `proizvod` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `usluga_stampe` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `proizvod_id` INT NOT NULL,
  `id_usluge` VARCHAR(30) NULL COMMENT 'spoljni kod iz JSON uvoza, npr. USL-01',
  `tip_stampe` VARCHAR(150) NOT NULL,
  `dodatna_cena_po_komadu` DECIMAL(10,2) NOT NULL DEFAULT 0,
  `max_sirina_mm` INT NULL,
  `max_visina_mm` INT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_usluga_proizvod` FOREIGN KEY (`proizvod_id`) REFERENCES `proizvod` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Narudžbine (jedna po štampariji, kako je traženo u opisu projekta)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `stavka_narudzbine`;
DROP TABLE IF EXISTS `narudzbina`;

CREATE TABLE IF NOT EXISTS `narudzbina` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `klijent_kor_ime` VARCHAR(45) NOT NULL,
  `stampar_kor_ime` VARCHAR(45) NOT NULL,
  `status` ENUM('naruceno','placeno','u_stampi','isporuceno','primljeno') NOT NULL DEFAULT 'naruceno',
  `datum_narudzbine` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ukupan_iznos` DECIMAL(10,2) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_narudzbina_klijent` FOREIGN KEY (`klijent_kor_ime`) REFERENCES `korisnik` (`kor_ime`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_narudzbina_stampar` FOREIGN KEY (`stampar_kor_ime`) REFERENCES `korisnik` (`kor_ime`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `stavka_narudzbine` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `narudzbina_id` INT NOT NULL,
  `proizvod_id` INT NOT NULL,
  `usluga_id` INT NULL,
  `kolicina` INT NOT NULL DEFAULT 1,
  `boja` VARCHAR(50) NULL,
  `tekst_za_stampu` TEXT NULL,
  `slicica_za_stampu_url` VARCHAR(255) NULL,
  `cena_stavke` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_stavka_narudzbina` FOREIGN KEY (`narudzbina_id`) REFERENCES `narudzbina` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_stavka_proizvod` FOREIGN KEY (`proizvod_id`) REFERENCES `proizvod` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_stavka_usluga` FOREIGN KEY (`usluga_id`) REFERENCES `usluga_stampe` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Lajk/dislajk i komentari na primljene proizvode
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `komentar_proizvoda`;
DROP TABLE IF EXISTS `ocena_proizvoda`;

CREATE TABLE IF NOT EXISTS `ocena_proizvoda` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `proizvod_id` INT NOT NULL,
  `klijent_kor_ime` VARCHAR(45) NOT NULL,
  `vrednost` ENUM('lajk','dislajk') NOT NULL,
  `datum` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_ocena` (`proizvod_id`, `klijent_kor_ime`),
  CONSTRAINT `fk_ocena_proizvod` FOREIGN KEY (`proizvod_id`) REFERENCES `proizvod` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ocena_klijent` FOREIGN KEY (`klijent_kor_ime`) REFERENCES `korisnik` (`kor_ime`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `komentar_proizvoda` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `proizvod_id` INT NOT NULL,
  `klijent_kor_ime` VARCHAR(45) NOT NULL,
  `tekst` VARCHAR(500) NOT NULL,
  `datum` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_komentar_proizvod` FOREIGN KEY (`proizvod_id`) REFERENCES `proizvod` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_komentar_klijent` FOREIGN KEY (`klijent_kor_ime`) REFERENCES `korisnik` (`kor_ime`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- TODO (dodati kad se dođe do tih funkcionalnosti — videti FEATURES.md):
--   - javna_nabavka, stavka_javne_nabavke, ponuda  (Javne nabavke / Licitacije)
--   - tabela za reset lozinke (token, korisnik, datum_isteka)
--   - eventualna tabela za "trenutnu e-korpu" ako se ne drži samo na frontu
-- =====================================================================

-- ---------------------------------------------------------------------
-- Test podaci (dovoljno za pregled svih trenutno modelovanih funkcionalnosti)
-- Lozinka za SVE naloge ispod je: Test123!
-- (u bazi je upisan pravi BCrypt hash te lozinke — videti DECISIONS.md)
-- ---------------------------------------------------------------------
INSERT INTO `korisnik`
  (`kor_ime`, `lozinka_hash`, `ime`, `prezime`, `telefon`, `mejl`, `tip`, `naziv_institucije`, `adresa_sedista`, `grad`, `maticni_broj`, `pib`, `status_registracije`)
VALUES
  ('admin', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Admin', 'Administratorski', NULL, 'admin@printinghouse.rs', 'administrator', NULL, NULL, NULL, NULL, NULL, 'odobren'),
  -- štamparije (odobrene):
  ('cstudio', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Marko', 'Markovic', '0641234567', 'kontakt@copystudio.rs', 'stampar', 'Copy Studio Kumanovska', 'Kumanovska 5', 'Beograd', '12345678', '123456789', 'odobren'),
  ('printmaster', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Ivana', 'Ivanovic', '0211234567', 'office@printmaster.rs', 'stampar', 'Print Master NS', 'Zmaj Jovina 18', 'Novi Sad', '23456781', '234567891', 'odobren'),
  ('artprint', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Stefan', 'Stefanovic', '0181234567', 'info@artprint.rs', 'stampar', 'Art Print Nis', 'Obrenoviceva 40', 'Nis', '34567812', '345678912', 'odobren'),
  -- klijenti (odobreni):
  ('ana.k', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Ana', 'Kostic', '0651112233', 'ana.k@gmail.com', 'klijent_fizicko', NULL, NULL, 'Novi Sad', NULL, NULL, 'odobren'),
  ('marko.p', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Marko', 'Petrovic', '0642223344', 'marko.p@gmail.com', 'klijent_fizicko', NULL, NULL, 'Beograd', NULL, NULL, 'odobren'),
  ('sara.j', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Sara', 'Jovanovic', '0663334455', 'sara.j@gmail.com', 'klijent_fizicko', NULL, NULL, 'Nis', NULL, NULL, 'odobren'),
  ('nikola.v', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Nikola', 'Vasic', '0624445566', 'nikola.v@gmail.com', 'klijent_fizicko', NULL, NULL, 'Kragujevac', NULL, NULL, 'odobren'),
  ('firma.doo', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Petar', 'Petrovic', '0661112233', 'nabavka@firma.rs', 'klijent_pravno', 'Firma DOO', 'Bulevar 10', 'Beograd', '87654321', '198765432', 'odobren'),
  -- nalozi za proveru da neodobreni korisnik ne moze da se prijavi:
  ('jovan.n', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Jovan', 'Nikolic', '0631112233', 'jovan.n@gmail.com', 'klijent_fizicko', NULL, NULL, 'Nis', NULL, NULL, 'na_cekanju'),
  ('brzastampa', '$2a$10$QcATPXv9/lDkhc3eUiPqG.I9rJccyKjOsjGZjrdgqviVVUo0aVqHm', 'Jelena', 'Jovanovic', '0621112233', 'info@brzastampa.rs', 'stampar', 'Brza Stampa DOO', 'Nemanjina 22', 'Beograd', '11223344', '112233445', 'odbijen');

INSERT INTO `proizvod` (`sifra`, `stampar_kor_ime`, `naziv`, `opis`, `kategorija_id`, `potkategorija_id`, `jedinicna_cena`, `kolicina_na_lageru`, `slika_url`)
VALUES
  ('PR-001', 'cstudio', 'Pamucna Polo Majica',
   'Kvalitetna pamučna polo majica 180g/m2, pogodna za brendiranje i korporativne uniforme.',
   (SELECT id FROM kategorija WHERE naziv='Kreativne štampe'),
   (SELECT id FROM potkategorija WHERE naziv='Štampa na majicama'),
   1200.00, 150, 'seed_pr001.jpg'),
  ('PR-002', 'cstudio', 'Keramička šolja 330ml',
   'Bela keramička šolja visokog sjaja, idealna za sublimacionu štampu visoke rezolucije.',
   (SELECT id FROM kategorija WHERE naziv='Kreativne štampe'),
   (SELECT id FROM potkategorija WHERE naziv='Šolje'),
   320.00, 500, 'seed_pr002.jpg'),
  ('PR-003', 'cstudio', 'Promotivni Roll-up Baner 85x200cm',
   'Lagan aluminijumski mehanizam sa torbom i štampom na kvalitetnom baner platnu.',
   (SELECT id FROM kategorija WHERE naziv='Štampa velikih formata'),
   (SELECT id FROM potkategorija WHERE naziv='Rollups'),
   4500.00, 20, 'seed_pr003.jpg'),
  ('PM-001', 'printmaster', 'Vizit karte 300g mat',
   'Obostrana štampa u punom koloru na mat kartonu 300g, cena po komadu za tiraž od 100.',
   (SELECT id FROM kategorija WHERE naziv='Štampa malih formata'),
   (SELECT id FROM potkategorija WHERE naziv='Vizit karte'),
   15.00, 5000, 'seed_pm001.jpg'),
  ('PM-002', 'printmaster', 'Flajer A5 pun kolor',
   'Flajer A5 na 135g sjajnoj hartiji, obostrana štampa, idealno za promocije.',
   (SELECT id FROM kategorija WHERE naziv='Štampa malih formata'),
   (SELECT id FROM potkategorija WHERE naziv='Flajeri'),
   12.00, 10000, 'seed_pm002.jpg'),
  ('PM-003', 'printmaster', 'Poster B1 (70x100cm)',
   'Poster velikog formata na 170g papiru, štampa visoke rezolucije.',
   (SELECT id FROM kategorija WHERE naziv='Štampa velikih formata'),
   (SELECT id FROM potkategorija WHERE naziv='Posteri'),
   950.00, 80, 'seed_pm003.jpg'),
  ('AP-001', 'artprint', 'Duks sa kapuljačom',
   'Unisex duks 320g/m2 sa kapuljačom i džepom, pogodan za DTG i flex štampu.',
   (SELECT id FROM kategorija WHERE naziv='Kreativne štampe'),
   (SELECT id FROM potkategorija WHERE naziv='Štampa na duksevima'),
   2400.00, 60, 'seed_ap001.jpg'),
  ('AP-002', 'artprint', 'Ceger od organskog pamuka',
   'Platnena torba od organskog pamuka 140g/m2 sa dugim ručkama.',
   (SELECT id FROM kategorija WHERE naziv='Kreativne štampe'),
   (SELECT id FROM potkategorija WHERE naziv='Štampa na cegerima'),
   550.00, 200, 'seed_ap002.jpg'),
  ('AP-003', 'artprint', 'Fototapeta po meri (m2)',
   'Zidna fototapeta po meri, mat samolepljiva folija, cena po kvadratnom metru.',
   (SELECT id FROM kategorija WHERE naziv='Štampa velikih formata'),
   (SELECT id FROM potkategorija WHERE naziv='Fototapete'),
   2800.00, 40, 'seed_ap003.jpg'),
  ('AP-004', 'artprint', 'Drvena olovka sa gravurom',
   'Drvena olovka sa laserskom gravurom logotipa, minimalna količina 100 komada.',
   (SELECT id FROM kategorija WHERE naziv='Štampa malih formata'),
   (SELECT id FROM potkategorija WHERE naziv='Olovke'),
   45.00, 3000, 'seed_ap004.jpg');

INSERT INTO `proizvod_boja` (`proizvod_id`, `boja`) VALUES
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Bela'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Crna'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Tamno plava'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Siva'),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'Bela'),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'Bela'),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'Crna'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'Crna'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'Siva'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'Bordo'),
  ((SELECT id FROM proizvod WHERE sifra='AP-002'), 'Natur'),
  ((SELECT id FROM proizvod WHERE sifra='AP-002'), 'Crna'),
  ((SELECT id FROM proizvod WHERE sifra='AP-004'), 'Natur'),
  ((SELECT id FROM proizvod WHERE sifra='PM-001'), 'Bela');

INSERT INTO `usluga_stampe` (`proizvod_id`, `id_usluge`, `tip_stampe`, `dodatna_cena_po_komadu`, `max_sirina_mm`, `max_visina_mm`) VALUES
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'USL-01', 'Direktna štampa na tekstil (DTG)', 350.00, 300, 400),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'USL-02', 'Preslikač (Sito preslikač)', 200.00, 280, 350),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'USL-03', 'Sublimaciona štampa', 150.00, 200, 85),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'USL-04', 'Eko-solventna štampa visoke rezolucije', 800.00, 850, 2000),
  ((SELECT id FROM proizvod WHERE sifra='PM-001'), 'USL-05', 'Ofset štampa pun kolor', 3.00, 90, 50),
  ((SELECT id FROM proizvod WHERE sifra='PM-002'), 'USL-06', 'Digitalna štampa pun kolor', 2.50, 148, 210),
  ((SELECT id FROM proizvod WHERE sifra='PM-003'), 'USL-07', 'Digitalna štampa velikog formata', 250.00, 700, 1000),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'USL-08', 'Direktna štampa na tekstil (DTG)', 450.00, 280, 350),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'USL-09', 'Vez', 900.00, 150, 150),
  ((SELECT id FROM proizvod WHERE sifra='AP-002'), 'USL-10', 'Sito štampa', 180.00, 250, 300),
  ((SELECT id FROM proizvod WHERE sifra='AP-003'), 'USL-11', 'Lateks štampa', 600.00, 1300, 3000),
  ((SELECT id FROM proizvod WHERE sifra='AP-004'), 'USL-12', 'Laserska gravura', 20.00, 100, 8);

-- ---------------------------------------------------------------------
-- Narudžbine, ocene i komentari (potrebni za TOP 5 listu na početnoj strani)
-- Lajkuje se samo proizvod koji je klijent stvarno primio.
-- ---------------------------------------------------------------------
INSERT INTO `narudzbina` (`klijent_kor_ime`, `stampar_kor_ime`, `status`, `datum_narudzbine`, `ukupan_iznos`) VALUES
  ('ana.k', 'cstudio', 'primljeno', '2026-08-04 10:15:00', 15500.00),
  ('marko.p', 'cstudio', 'primljeno', '2026-08-11 13:40:00', 6400.00),
  ('sara.j', 'artprint', 'primljeno', '2026-08-18 09:05:00', 14250.00),
  ('nikola.v', 'printmaster', 'primljeno', '2026-08-25 16:20:00', 9000.00),
  ('firma.doo', 'printmaster', 'primljeno', '2026-09-01 11:00:00', 30000.00),
  ('ana.k', 'artprint', 'isporuceno', '2026-09-12 12:30:00', 5700.00),
  ('marko.p', 'printmaster', 'u_stampi', '2026-09-16 15:10:00', 1500.00),
  ('sara.j', 'cstudio', 'naruceno', '2026-09-19 18:45:00', 3200.00);

INSERT INTO `stavka_narudzbine` (`narudzbina_id`, `proizvod_id`, `usluga_id`, `kolicina`, `boja`, `tekst_za_stampu`, `cena_stavke`) VALUES
  (1, (SELECT id FROM proizvod WHERE sifra='PR-001'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-01'), 10, 'Bela', 'Tim Building 2026', 15500.00),
  (2, (SELECT id FROM proizvod WHERE sifra='PR-002'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-03'), 20, 'Bela', 'Najbolji kolega', 9400.00),
  (3, (SELECT id FROM proizvod WHERE sifra='AP-001'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-08'), 5, 'Crna', 'Art Club', 14250.00),
  (4, (SELECT id FROM proizvod WHERE sifra='PM-001'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-05'), 500, 'Bela', 'Nikola Vasic, konsultant', 9000.00),
  (5, (SELECT id FROM proizvod WHERE sifra='PM-002'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-06'), 2000, NULL, 'Otvaranje nove poslovnice', 29000.00),
  (6, (SELECT id FROM proizvod WHERE sifra='AP-002'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-10'), 8, 'Natur', 'Eko akcija', 5840.00),
  (7, (SELECT id FROM proizvod WHERE sifra='PM-003'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-07'), 1, NULL, 'Koncert u parku', 1200.00),
  (8, (SELECT id FROM proizvod WHERE sifra='PR-002'), (SELECT id FROM usluga_stampe WHERE id_usluge='USL-03'), 6, 'Bela', 'Poklon paket', 2820.00);

INSERT INTO `ocena_proizvoda` (`proizvod_id`, `klijent_kor_ime`, `vrednost`, `datum`) VALUES
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'ana.k', 'lajk', '2026-08-10 09:00:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'marko.p', 'lajk', '2026-08-15 10:30:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'sara.j', 'lajk', '2026-08-20 14:10:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'nikola.v', 'lajk', '2026-08-28 17:25:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'firma.doo', 'lajk', '2026-09-02 08:45:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'sara.j', 'lajk', '2026-08-22 11:15:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'ana.k', 'lajk', '2026-08-29 19:00:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'marko.p', 'lajk', '2026-09-03 13:05:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'nikola.v', 'lajk', '2026-09-06 15:40:00'),
  ((SELECT id FROM proizvod WHERE sifra='PM-001'), 'nikola.v', 'lajk', '2026-08-30 12:00:00'),
  ((SELECT id FROM proizvod WHERE sifra='PM-001'), 'firma.doo', 'lajk', '2026-09-04 09:20:00'),
  ((SELECT id FROM proizvod WHERE sifra='PM-001'), 'ana.k', 'lajk', '2026-09-08 16:35:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'marko.p', 'lajk', '2026-08-16 18:10:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'sara.j', 'lajk', '2026-09-10 10:55:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'ana.k', 'lajk', '2026-09-13 20:15:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-002'), 'ana.k', 'lajk', '2026-09-14 08:30:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-002'), 'sara.j', 'lajk', '2026-09-15 12:45:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'firma.doo', 'lajk', '2026-09-05 14:00:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'marko.p', 'dislajk', '2026-09-07 17:30:00'),
  ((SELECT id FROM proizvod WHERE sifra='PM-002'), 'firma.doo', 'dislajk', '2026-09-09 11:20:00');

INSERT INTO `komentar_proizvoda` (`proizvod_id`, `klijent_kor_ime`, `tekst`, `datum`) VALUES
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'ana.k', 'Materijal je zaista kvalitetan, štampa se nije oštetila ni posle više pranja.', '2026-08-10 09:01:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'marko.p', 'Boje su tačno kao na pripremi. Isporuka za tri dana.', '2026-08-15 10:31:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'sara.j', 'Veličine odgovaraju tabeli, preporučujem.', '2026-08-20 14:11:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'sara.j', 'Duks je debeo i mekan, štampa izgleda odlično.', '2026-08-22 11:16:00'),
  ((SELECT id FROM proizvod WHERE sifra='AP-001'), 'marko.p', 'Malo veći kroj nego što sam očekivao, ali kvalitet je vrhunski.', '2026-09-03 13:06:00'),
  ((SELECT id FROM proizvod WHERE sifra='PM-001'), 'nikola.v', 'Mat karton izgleda vrlo profesionalno.', '2026-08-30 12:01:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'ana.k', 'Šolja je izdržala mašinsko pranje bez problema.', '2026-09-13 20:16:00'),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'marko.p', 'Mehanizam se malo teže sklapa, ali štampa je uredna.', '2026-09-07 17:31:00');

COMMIT;
