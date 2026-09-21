package com.example.backend.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.dto.RegistracijaRequest;
import com.example.backend.models.Korisnik;
import com.example.backend.models.StatusRegistracije;
import com.example.backend.models.TipKorisnika;

@Service
public class RegistracijaService {

    private static final String PODRAZUMEVANA_SLIKA = "default_profile_image.jpg";

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;
    private final SlikaService slikaService;

    public RegistracijaService(KorisnikRepository korisnikRepository, PasswordEncoder passwordEncoder,
            SlikaService slikaService) {
        this.korisnikRepository = korisnikRepository;
        this.passwordEncoder = passwordEncoder;
        this.slikaService = slikaService;
    }

    @Transactional
    public Korisnik registruj(RegistracijaRequest zahtev) {
        if (zahtev.getTip() == TipKorisnika.administrator) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Administratorski nalog se ne može registrovati.");
        }

        boolean institucija = zahtev.getTip() == TipKorisnika.klijent_pravno
                || zahtev.getTip() == TipKorisnika.stampar;
        if (institucija) {
            proveriPoljaInstitucije(zahtev);
        }
        proveriJedinstvenost(zahtev, institucija);

        Korisnik korisnik = new Korisnik();
        korisnik.setKorIme(zahtev.getKorIme().trim());
        korisnik.setLozinkaHash(passwordEncoder.encode(zahtev.getLozinka()));
        korisnik.setIme(zahtev.getIme().trim());
        korisnik.setPrezime(zahtev.getPrezime().trim());
        korisnik.setTelefon(zahtev.getTelefon().trim());
        korisnik.setMejl(zahtev.getMejl().trim());
        korisnik.setTip(zahtev.getTip());
        korisnik.setSlikaUrl(PODRAZUMEVANA_SLIKA);
        korisnik.setGrad(prazanKaoNull(zahtev.getGrad()));
        korisnik.setNazivInstitucije(institucija ? zahtev.getNazivInstitucije().trim() : null);
        korisnik.setAdresaSedista(institucija ? zahtev.getAdresaSedista().trim() : null);
        korisnik.setMaticniBroj(institucija ? zahtev.getMaticniBroj().trim() : null);
        korisnik.setPib(institucija ? zahtev.getPib().trim() : null);
        korisnik.setStatusRegistracije(StatusRegistracije.na_cekanju);
        korisnik.setDatumRegistracije(LocalDateTime.now());

        return korisnikRepository.save(korisnik);
    }

    /** Drugi korak registracije — vraca naziv sacuvanog fajla. */
    @Transactional
    public String postaviProfilnuSliku(String korIme, MultipartFile slika) {
        Korisnik korisnik = korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Nalog nije pronađen."));

        String naziv = slikaService.sacuvajProfilnu(slika, korIme);
        korisnik.setSlikaUrl(naziv);
        korisnikRepository.save(korisnik);
        return naziv;
    }

    private void proveriPoljaInstitucije(RegistracijaRequest z) {
        if (prazno(z.getNazivInstitucije())) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Naziv institucije je obavezan.");
        }
        if (prazno(z.getAdresaSedista())) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Adresa sedišta je obavezna.");
        }
        if (prazno(z.getGrad())) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Grad je obavezan.");
        }
        if (prazno(z.getMaticniBroj()) || !z.getMaticniBroj().trim().matches("^\\d{8}$")) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Matični broj mora imati tačno 8 cifara.");
        }
        if (prazno(z.getPib()) || !z.getPib().trim().matches("^[1-9]\\d{8}$")) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "PIB mora imati 9 cifara i ne sme počinjati nulom.");
        }
    }

    private void proveriJedinstvenost(RegistracijaRequest z, boolean institucija) {
        if (korisnikRepository.existsByKorIme(z.getKorIme().trim())) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Korisničko ime je već zauzeto.");
        }
        if (korisnikRepository.existsByMejlIgnoreCase(z.getMejl().trim())) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Nalog sa ovom mejl adresom već postoji.");
        }
        if (institucija) {
            if (korisnikRepository.existsByMaticniBroj(z.getMaticniBroj().trim())) {
                throw new PoslovnaGreska(HttpStatus.CONFLICT, "Matični broj je već registrovan.");
            }
            if (korisnikRepository.existsByPib(z.getPib().trim())) {
                throw new PoslovnaGreska(HttpStatus.CONFLICT, "PIB je već registrovan.");
            }
        }
    }

    private static boolean prazno(String s) {
        return s == null || s.isBlank();
    }

    private static String prazanKaoNull(String s) {
        return prazno(s) ? null : s.trim();
    }
}
