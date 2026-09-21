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

/** Cuvanje profilnih slika na disku, uz proveru formata i dimenzija. */
@Service
public class SlikaService {

    private static final Set<String> DOZVOLJENE_EKSTENZIJE = Set.of("jpg", "jpeg", "png", "gif");
    private static final long MAX_BAJTOVA = 2L * 1024 * 1024;
    private static final int MIN_PIKSELA = 100;
    private static final int MAX_PIKSELA = 250;

    private final Path folder;

    public SlikaService(@Value("${app.upload-dir}") String uploadDir) {
        this.folder = Paths.get(uploadDir, "profilne").toAbsolutePath().normalize();
    }

    /** Snima sliku i vraca naziv fajla koji treba upisati u kolonu `slika_url`. */
    public String sacuvajProfilnu(MultipartFile fajl, String korIme) {
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
            proveriDimenzije(slika.getWidth(), slika.getHeight());

            Files.createDirectories(folder);
            String naziv = korIme + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ekstenzija;
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

    private void proveriDimenzije(int sirina, int visina) {
        if (sirina < MIN_PIKSELA || visina < MIN_PIKSELA) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "Slika mora biti najmanje " + MIN_PIKSELA + "x" + MIN_PIKSELA + " piksela (poslata je "
                            + sirina + "x" + visina + ").");
        }
        if (sirina > MAX_PIKSELA || visina > MAX_PIKSELA) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "Slika sme biti najviše " + MAX_PIKSELA + "x" + MAX_PIKSELA + " piksela (poslata je "
                            + sirina + "x" + visina + ").");
        }
    }

    private static String ekstenzija(String nazivFajla) {
        if (nazivFajla == null) {
            return "";
        }
        int tacka = nazivFajla.lastIndexOf('.');
        return tacka < 0 ? "" : nazivFajla.substring(tacka + 1).toLowerCase(Locale.ROOT);
    }
}
