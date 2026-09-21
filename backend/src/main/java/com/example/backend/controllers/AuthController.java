package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.dto.KorisnikDto;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.Korisnik;
import com.example.backend.security.Sesija;
import com.example.backend.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final KorisnikRepository korisnikRepository;

    public AuthController(AuthService authService, KorisnikRepository korisnikRepository) {
        this.authService = authService;
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
