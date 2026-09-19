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
-- Lozinke ovde NISU stvarni hash-evi — zameniti pravim BCrypt hash-em čim
-- registracija/login proradi (npr. hash za "Test123!" generisan iz aplikacije).
-- ---------------------------------------------------------------------
INSERT INTO `korisnik`
  (`kor_ime`, `lozinka_hash`, `ime`, `prezime`, `telefon`, `mejl`, `tip`, `naziv_institucije`, `adresa_sedista`, `grad`, `maticni_broj`, `pib`, `status_registracije`)
VALUES
  ('admin', '$2a$10$replaceWithRealBCryptHash', 'Admin', 'Administratorski', NULL, 'admin@printinghouse.rs', 'administrator', NULL, NULL, NULL, NULL, NULL, 'odobren'),
  ('cstudio', '$2a$10$replaceWithRealBCryptHash', 'Marko', 'Markovic', '0641234567', 'kontakt@copystudio.rs', 'stampar', 'Copy Studio Kumanovska', 'Kumanovska 5', 'Beograd', '12345678', '123456789', 'odobren'),
  ('ana.k', '$2a$10$replaceWithRealBCryptHash', 'Ana', 'Kostic', '0651112233', 'ana.k@gmail.com', 'klijent_fizicko', NULL, NULL, 'Novi Sad', NULL, NULL, 'odobren'),
  ('firma.doo', '$2a$10$replaceWithRealBCryptHash', 'Petar', 'Petrovic', '0661112233', 'nabavka@firma.rs', 'klijent_pravno', 'Firma DOO', 'Bulevar 10', 'Beograd', '87654321', '198765432', 'odobren');

INSERT INTO `proizvod` (`sifra`, `stampar_kor_ime`, `naziv`, `opis`, `kategorija_id`, `potkategorija_id`, `jedinicna_cena`, `kolicina_na_lageru`, `slika_url`)
VALUES
  ('PR-001', 'cstudio', 'Pamucna Polo Majica',
   'Kvalitetna pamučna polo majica 180g/m2, pogodna za brendiranje i korporativne uniforme.',
   (SELECT id FROM kategorija WHERE naziv='Kreativne štampe'),
   (SELECT id FROM potkategorija WHERE naziv='Štampa na majicama'),
   1200.00, 150, NULL),
  ('PR-002', 'cstudio', 'Keramička šolja 330ml',
   'Bela keramička šolja visokog sjaja, idealna za sublimacionu štampu visoke rezolucije.',
   (SELECT id FROM kategorija WHERE naziv='Kreativne štampe'),
   (SELECT id FROM potkategorija WHERE naziv='Šolje'),
   320.00, 500, NULL),
  ('PR-003', 'cstudio', 'Promotivni Roll-up Baner 85x200cm',
   'Lagan aluminijumski mehanizam sa torbom i štampom na kvalitetnom baner platnu.',
   (SELECT id FROM kategorija WHERE naziv='Štampa velikih formata'),
   (SELECT id FROM potkategorija WHERE naziv='Rollups'),
   4500.00, 20, NULL);

INSERT INTO `proizvod_boja` (`proizvod_id`, `boja`) VALUES
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Bela'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Crna'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Tamno plava'),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'Siva'),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'Bela'),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'Bela'),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'Crna');

INSERT INTO `usluga_stampe` (`proizvod_id`, `id_usluge`, `tip_stampe`, `dodatna_cena_po_komadu`, `max_sirina_mm`, `max_visina_mm`) VALUES
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'USL-01', 'Direktna štampa na tekstil (DTG)', 350.00, 300, 400),
  ((SELECT id FROM proizvod WHERE sifra='PR-001'), 'USL-02', 'Preslikač (Sito preslikač)', 200.00, 280, 350),
  ((SELECT id FROM proizvod WHERE sifra='PR-002'), 'USL-03', 'Sublimaciona štampa', 150.00, 200, 85),
  ((SELECT id FROM proizvod WHERE sifra='PR-003'), 'USL-04', 'Eko-solventna štampa visoke rezolucije', 800.00, 850, 2000);

COMMIT;
