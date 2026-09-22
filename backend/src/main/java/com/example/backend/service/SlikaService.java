package com.example.backend.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.PoslovnaGreska;

/** Cuvanje uploadovanih slika na disku, uz proveru formata i dimenzija. */
@Service
public class SlikaService {

    private static final Set<String> DOZVOLJENE_EKSTENZIJE = Set.of("jpg", "jpeg", "png", "gif");
    private static final long MAX_BAJTOVA = 2L * 1024 * 1024;
    private static final int PROFILNA_MIN = 100;
    private static final int PROFILNA_MAX = 250;
    private static final int PROIZVOD_MIN = 200;
    private static final int PROIZVOD_MAX = 2000;

    private final Path korenUploada;

    public SlikaService(@Value("${app.upload-dir}") String uploadDir) {
        this.korenUploada = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** Snima profilnu sliku i vraca naziv fajla za kolonu `korisnik.slika_url`. */
    public String sacuvajProfilnu(MultipartFile fajl, String korIme) {
        return sacuvaj(fajl, "profilne", korIme, PROFILNA_MIN, PROFILNA_MAX);
    }

    /** Snima sliku proizvoda i vraca naziv fajla za kolonu `proizvod.slika_url`. */
    public String sacuvajProizvod(MultipartFile fajl, String sifra) {
        return sacuvaj(fajl, "proizvodi", sifra, PROIZVOD_MIN, PROIZVOD_MAX);
    }

    private String sacuvaj(MultipartFile fajl, String podfolder, String prefiks, int min, int max) {
        if (fajl == null || fajl.isEmpty()) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Nije izabrana slika.");
        }
        if (fajl.getSize() > MAX_BAJTOVA) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Slika sme biti najviše 2 MB.");
        }

        String ekstenzija = ekstenzija(fajl.getOriginalFilename());
        if (!DOZVOLJENE_EKSTENZIJE.contains(ekstenzija)) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Dozvoljeni formati slike su JPG, PNG i GIF.");
        }

        try {
            BufferedImage slika;
            try (InputStream ulaz = fajl.getInputStream()) {
                slika = ImageIO.read(ulaz);
            }
            // ImageIO vraca null ako sadrzaj nije stvarna slika, bez obzira na ekstenziju
            if (slika == null) {
                throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Poslati fajl nije ispravna slika.");
            }
            proveriDimenzije(slika.getWidth(), slika.getHeight(), min, max);

            Path folder = korenUploada.resolve(podfolder);
            Files.createDirectories(folder);

            String naziv = bezbedanPrefiks(prefiks) + "_"
                    + UUID.randomUUID().toString().substring(0, 8) + "." + ekstenzija;
            Path odrediste = folder.resolve(naziv);
            if (!odrediste.getParent().equals(folder)) {
                throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Neispravan naziv fajla.");
            }
            try (InputStream ulaz = fajl.getInputStream()) {
                Files.copy(ulaz, odrediste, StandardCopyOption.REPLACE_EXISTING);
            }
            return naziv;
        } catch (IOException e) {
            throw new PoslovnaGreska(HttpStatus.INTERNAL_SERVER_ERROR, "Slika nije mogla da se sačuva.");
        }
    }

    private void proveriDimenzije(int sirina, int visina, int min, int max) {
        if (sirina < min || visina < min) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "Slika mora biti najmanje " + min + "x" + min + " piksela (poslata je "
                            + sirina + "x" + visina + ").");
        }
        if (sirina > max || visina > max) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "Slika sme biti najviše " + max + "x" + max + " piksela (poslata je "
                            + sirina + "x" + visina + ").");
        }
    }

    /** Naziv fajla se pravi od korisnickog unosa, pa se sve sem slova i cifara uklanja. */
    private static String bezbedanPrefiks(String s) {
        String ocisceno = s.replaceAll("[^A-Za-z0-9._-]", "");
        return ocisceno.isEmpty() ? "slika" : ocisceno;
    }

    private static String ekstenzija(String nazivFajla) {
        if (nazivFajla == null) {
            return "";
        }
        int tacka = nazivFajla.lastIndexOf('.');
        return tacka < 0 ? "" : nazivFajla.substring(tacka + 1).toLowerCase(Locale.ROOT);
    }
}
