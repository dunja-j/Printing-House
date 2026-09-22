package com.example.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.AdminKorisnikDto;
import com.example.backend.dto.AzuriranjeProfilaRequest;
import com.example.backend.dto.KategorijaDto;
import com.example.backend.dto.KategorijaSaPotkategorijamaDto;
import com.example.backend.dto.NazivRequest;
import com.example.backend.models.TipKorisnika;
import com.example.backend.security.Sesija;
import com.example.backend.service.AdminService;
import com.example.backend.service.StamparService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final StamparService stamparService;

    public AdminController(AdminService adminService, StamparService stamparService) {
        this.adminService = adminService;
        this.stamparService = stamparService;
    }

    @GetMapping("/zahtevi")
    public List<AdminKorisnikDto> zahtevi(HttpSession session) {
        admin(session);
        return adminService.zahteviZaRegistraciju();
    }

    @PostMapping("/zahtevi/{korIme}")
    public AdminKorisnikDto odluci(@PathVariable String korIme,
            @RequestParam boolean prihvati, HttpSession session) {
        admin(session);
        return adminService.odluciOZahtevu(korIme, prihvati);
    }

    @GetMapping("/korisnici")
    public List<AdminKorisnikDto> korisnici(HttpSession session) {
        admin(session);
        return adminService.korisnici();
    }

    @PutMapping("/korisnici/{korIme}")
    public AdminKorisnikDto azuriraj(@PathVariable String korIme,
            @Valid @RequestBody AzuriranjeProfilaRequest zahtev, HttpSession session) {
        admin(session);
        return adminService.azuriraj(korIme, zahtev);
    }

    @DeleteMapping("/korisnici/{korIme}")
    public Map<String, String> obrisi(@PathVariable String korIme, HttpSession session) {
        adminService.obrisi(korIme, admin(session));
        return Map.of("poruka", "Nalog \"" + korIme + "\" je obrisan.");
    }

    @GetMapping("/kategorije")
    public List<KategorijaSaPotkategorijamaDto> kategorije(HttpSession session) {
        admin(session);
        return stamparService.kategorije();
    }

    @PostMapping("/kategorije")
    public KategorijaDto dodajKategoriju(@Valid @RequestBody NazivRequest zahtev,
            HttpSession session) {
        admin(session);
        return adminService.dodajKategoriju(zahtev.getNaziv());
    }

    @PostMapping("/kategorije/{id}/potkategorije")
    public KategorijaDto dodajPotkategoriju(@PathVariable Integer id,
            @Valid @RequestBody NazivRequest zahtev, HttpSession session) {
        admin(session);
        return adminService.dodajPotkategoriju(id, zahtev.getNaziv());
    }

    private String admin(HttpSession session) {
        return Sesija.zahtevajTip(session, TipKorisnika.administrator);
    }
}
