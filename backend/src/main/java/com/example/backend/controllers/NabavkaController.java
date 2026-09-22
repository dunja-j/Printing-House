package com.example.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.NabavkaDto;
import com.example.backend.dto.NabavkaZaStamparaDto;
import com.example.backend.dto.PonudaRequest;
import com.example.backend.models.TipKorisnika;
import com.example.backend.security.Sesija;
import com.example.backend.service.NabavkaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class NabavkaController {

    private final NabavkaService nabavkaService;

    public NabavkaController(NabavkaService nabavkaService) {
        this.nabavkaService = nabavkaService;
    }

    @PostMapping("/klijent/nabavke")
    public NabavkaDto objavi(HttpSession session) {
        return nabavkaService.objaviIzKorpe(
                Sesija.zahtevajTip(session, TipKorisnika.klijent_pravno));
    }

    @GetMapping("/klijent/nabavke")
    public List<NabavkaDto> mojeNabavke(HttpSession session) {
        return nabavkaService.mojeNabavke(
                Sesija.zahtevajTip(session, TipKorisnika.klijent_pravno));
    }

    @GetMapping("/stampar/nabavke")
    public List<NabavkaZaStamparaDto> otvorene(HttpSession session) {
        return nabavkaService.otvoreneNabavke(Sesija.zahtevajTip(session, TipKorisnika.stampar));
    }

    @PostMapping("/stampar/nabavke/{id}/ponuda")
    public NabavkaZaStamparaDto ponudi(@PathVariable Integer id,
            @Valid @RequestBody PonudaRequest zahtev, HttpSession session) {
        return nabavkaService.posaljiPonudu(Sesija.zahtevajTip(session, TipKorisnika.stampar), id,
                zahtev.getUkupnaCena());
    }
}
