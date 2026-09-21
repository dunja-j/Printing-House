package com.example.backend.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.dto.KorisnikDto;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.dto.RegistracijaRequest;
import com.example.backend.models.Korisnik;
import com.example.backend.security.Sesija;
import com.example.backend.service.AuthService;
import com.example.backend.service.RegistracijaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RegistracijaService registracijaService;
    private final KorisnikRepository korisnikRepository;

    public AuthController(AuthService authService, RegistracijaService registracijaService,
            KorisnikRepository korisnikRepository) {
        this.authService = authService;
        this.registracijaService = registracijaService;
        this.korisnikRepository = korisnikRepository;
    }

    @PostMapping("/login")
    public KorisnikDto login(@Valid @RequestBody LoginRequest zahtev, HttpSession session) {
        Korisnik korisnik = authService.prijaviKorisnika(zahtev.getKorIme(), zahtev.getLozinka());
        Sesija.prijavi(session, korisnik);
        return KorisnikDto.od(korisnik);
    }

    @PostMapping("/login-admin")
    public KorisnikDto loginAdmin(@Valid @RequestBody LoginRequest zahtev, HttpSession session) {
        Korisnik korisnik = authService.prijaviAdministratora(zahtev.getKorIme(), zahtev.getLozinka());
        Sesija.prijavi(session, korisnik);
        return KorisnikDto.od(korisnik);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        Sesija.odjavi(session);
        return ResponseEntity.noContent().build();
    }

    /** Prvi korak registracije — kreira nalog u statusu "na čekanju". */
    @PostMapping("/registracija")
    public Map<String, String> registracija(@Valid @RequestBody RegistracijaRequest zahtev,
            HttpSession session) {
        Korisnik korisnik = registracijaService.registruj(zahtev);
        Sesija.zapocetaRegistracija(session, korisnik.getKorIme());
        return Map.of("korIme", korisnik.getKorIme(),
                "poruka", "Registracija je primljena i čeka odobrenje administratora.");
    }

    /**
     * Drugi korak registracije — profilna slika. Nalog se uzima iz sesije, a ne iz
     * zahteva, da niko ne bi mogao da promeni tuđu sliku.
     */
    @PostMapping("/registracija/slika")
    public Map<String, String> registracijaSlika(@RequestParam("slika") MultipartFile slika,
            HttpSession session) {
        String korIme = Sesija.korImeRegistracije(session);
        if (korIme == null) {
            throw new PoslovnaGreska(HttpStatus.FORBIDDEN,
                    "Registracija nije započeta ili je sesija istekla.");
        }
        String naziv = registracijaService.postaviProfilnuSliku(korIme, slika);
        Sesija.zavrsiRegistraciju(session);
        return Map.of("slikaUrl", naziv);
    }

    /** Front na osvezavanju strane proverava da li sesija na serveru jos vazi. */
    @GetMapping("/trenutni")
    public KorisnikDto trenutni(HttpSession session) {
        String korIme = Sesija.korIme(session);
        if (korIme == null) {
            throw new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni.");
        }
        return korisnikRepository.findByKorIme(korIme)
                .map(KorisnikDto::od)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni."));
    }
}
