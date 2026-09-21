package com.example.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.Korisnik;
import com.example.backend.models.StatusRegistracije;
import com.example.backend.models.TipKorisnika;

@Service
public class AuthService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(KorisnikRepository korisnikRepository, PasswordEncoder passwordEncoder) {
        this.korisnikRepository = korisnikRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Prijava klijenta ili stampara kroz javnu formu. */
    public Korisnik prijaviKorisnika(String korIme, String lozinka) {
        Korisnik korisnik = proveriKredencijale(korIme, lozinka);

        if (korisnik.getTip() == TipKorisnika.administrator) {
            // administrator se prijavljuje iskljucivo na skrivenoj admin ruti;
            // poruka je namerno ista kao za pogresne kredencijale da se admin nalog ne otkrije
            throw new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka.");
        }
        proveriStatusRegistracije(korisnik);
        return korisnik;
    }

    /** Prijava administratora na skrivenoj ruti. */
    public Korisnik prijaviAdministratora(String korIme, String lozinka) {
        Korisnik korisnik = proveriKredencijale(korIme, lozinka);

        if (korisnik.getTip() != TipKorisnika.administrator) {
            throw new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka.");
        }
        return korisnik;
    }

    private Korisnik proveriKredencijale(String korIme, String lozinka) {
        Korisnik korisnik = korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.UNAUTHORIZED,
                        "Pogrešno korisničko ime ili lozinka."));

        if (!passwordEncoder.matches(lozinka, korisnik.getLozinkaHash())) {
            throw new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka.");
        }
        return korisnik;
    }

    private void proveriStatusRegistracije(Korisnik korisnik) {
        if (korisnik.getStatusRegistracije() == StatusRegistracije.na_cekanju) {
            throw new PoslovnaGreska(HttpStatus.FORBIDDEN,
                    "Vaša registracija još uvek čeka odobrenje administratora.");
        }
        if (korisnik.getStatusRegistracije() == StatusRegistracije.odbijen) {
            throw new PoslovnaGreska(HttpStatus.FORBIDDEN,
                    "Vaš zahtev za registraciju je odbijen. Obratite se administratoru.");
        }
    }
}
