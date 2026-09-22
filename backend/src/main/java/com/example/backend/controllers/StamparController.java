package com.example.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.KategorijaSaPotkategorijamaDto;
import com.example.backend.dto.NoviProizvodRequest;
import com.example.backend.dto.StamparNarudzbinaDto;
import com.example.backend.dto.StamparProizvodDto;
import com.example.backend.models.StatusNarudzbine;
import com.example.backend.models.TipKorisnika;
import com.example.backend.security.Sesija;
import com.example.backend.service.NarudzbinaService;
import com.example.backend.service.StamparService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stampar")
public class StamparController {

    private final StamparService stamparService;
    private final NarudzbinaService narudzbinaService;

    public StamparController(StamparService stamparService, NarudzbinaService narudzbinaService) {
        this.stamparService = stamparService;
        this.narudzbinaService = narudzbinaService;
    }

    @GetMapping("/kategorije")
    public List<KategorijaSaPotkategorijamaDto> kategorije(HttpSession session) {
        stampar(session);
        return stamparService.kategorije();
    }

    @GetMapping("/proizvodi")
    public List<StamparProizvodDto> proizvodi(HttpSession session) {
        return stamparService.mojiProizvodi(stampar(session));
    }

    @PostMapping("/proizvodi")
    public StamparProizvodDto dodaj(@Valid @RequestBody NoviProizvodRequest zahtev,
            HttpSession session) {
        return stamparService.dodaj(stampar(session), zahtev);
    }

    @PutMapping("/proizvodi/{id}/kolicina")
    public StamparProizvodDto promeniKolicinu(@PathVariable Integer id,
            @RequestParam int kolicina, HttpSession session) {
        return stamparService.promeniKolicinu(stampar(session), id, kolicina);
    }

    @PutMapping("/proizvodi/{id}/dostupnost")
    public StamparProizvodDto promeniDostupnost(@PathVariable Integer id,
            @RequestParam boolean aktivan, HttpSession session) {
        return stamparService.promeniDostupnost(stampar(session), id, aktivan);
    }

    @PostMapping("/proizvodi/{id}/slika")
    public StamparProizvodDto promeniSliku(@PathVariable Integer id,
            @RequestParam("slika") MultipartFile slika, HttpSession session) {
        return stamparService.promeniSliku(stampar(session), id, slika);
    }

    @GetMapping("/narudzbine")
    public List<StamparNarudzbinaDto> narudzbine(HttpSession session) {
        return narudzbinaService.zaStampara(stampar(session));
    }

    @PutMapping("/narudzbine/{id}/status")
    public StamparNarudzbinaDto promeniStatus(@PathVariable Integer id,
            @RequestParam StatusNarudzbine status, HttpSession session) {
        return narudzbinaService.promeniStatus(id, stampar(session), status);
    }

    private String stampar(HttpSession session) {
        return Sesija.zahtevajTip(session, TipKorisnika.stampar);
    }
}
