package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.AzuriranjeProfilaRequest;
import com.example.backend.dto.KorisnikDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.security.Sesija;
import com.example.backend.service.ProfilService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/** Profil uvek radi sa nalogom iz sesije — korisnik ne može da dohvati ni izmeni tuđi. */
@RestController
@RequestMapping("/api/profil")
public class ProfilController {

    private final ProfilService profilService;

    public ProfilController(ProfilService profilService) {
        this.profilService = profilService;
    }

    @GetMapping
    public KorisnikDto profil(HttpSession session) {
        return KorisnikDto.od(profilService.nadji(prijavljen(session)));
    }

    @PutMapping
    public KorisnikDto azuriraj(@Valid @RequestBody AzuriranjeProfilaRequest zahtev,
            HttpSession session) {
        return KorisnikDto.od(profilService.azuriraj(prijavljen(session), zahtev));
    }

    @PostMapping("/slika")
    public KorisnikDto promeniSliku(@RequestParam("slika") MultipartFile slika, HttpSession session) {
        return KorisnikDto.od(profilService.promeniSliku(prijavljen(session), slika));
    }

    private String prijavljen(HttpSession session) {
        String korIme = Sesija.korIme(session);
        if (korIme == null) {
            throw new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni.");
        }
        return korIme;
    }
}
