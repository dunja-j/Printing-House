package com.example.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.DetaljiZaKlijentaDto;
import com.example.backend.dto.NarudzbinaDto;
import com.example.backend.models.TipKorisnika;
import com.example.backend.security.Sesija;
import com.example.backend.service.NarudzbinaService;
import com.example.backend.service.ProizvodService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/klijent")
public class KlijentController {

    private final NarudzbinaService narudzbinaService;
    private final ProizvodService proizvodService;

    public KlijentController(NarudzbinaService narudzbinaService, ProizvodService proizvodService) {
        this.narudzbinaService = narudzbinaService;
        this.proizvodService = proizvodService;
    }

    /** Prosireni detalji — sa bojama i uslugama stampe potrebnim za porucivanje. */
    @GetMapping("/proizvodi/{id}")
    public DetaljiZaKlijentaDto detaljiProizvoda(@PathVariable Integer id, HttpSession session) {
        klijent(session);
        return proizvodService.detaljiZaKlijenta(id);
    }

    @GetMapping("/narudzbine")
    public List<NarudzbinaDto> narudzbine(HttpSession session) {
        return narudzbinaService.zaKlijenta(klijent(session));
    }

    @PostMapping("/narudzbine/{id}/otkazi")
    public NarudzbinaDto otkazi(@PathVariable Integer id, HttpSession session) {
        return narudzbinaService.otkazi(id, klijent(session));
    }

    private String klijent(HttpSession session) {
        return Sesija.zahtevajTip(session, TipKorisnika.klijent_fizicko, TipKorisnika.klijent_pravno);
    }
}
