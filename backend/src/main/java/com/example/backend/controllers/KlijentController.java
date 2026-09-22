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

import com.example.backend.dto.ArhivaStavkaDto;
import com.example.backend.dto.DetaljiZaKlijentaDto;
import com.example.backend.dto.DodajUKorpuRequest;
import com.example.backend.dto.KomentarRequest;
import com.example.backend.dto.KorpaDto;
import com.example.backend.dto.NarudzbinaDto;
import com.example.backend.models.TipKorisnika;
import com.example.backend.models.VrednostOcene;
import com.example.backend.security.Sesija;
import com.example.backend.service.ArhivaService;
import com.example.backend.service.KorpaService;
import com.example.backend.service.NarudzbinaService;
import com.example.backend.service.ProizvodService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/klijent")
public class KlijentController {

    private final NarudzbinaService narudzbinaService;
    private final ProizvodService proizvodService;
    private final KorpaService korpaService;
    private final ArhivaService arhivaService;

    public KlijentController(NarudzbinaService narudzbinaService, ProizvodService proizvodService,
            KorpaService korpaService, ArhivaService arhivaService) {
        this.narudzbinaService = narudzbinaService;
        this.proizvodService = proizvodService;
        this.korpaService = korpaService;
        this.arhivaService = arhivaService;
    }

    /** Prosireni detalji — sa bojama i uslugama stampe potrebnim za porucivanje. */
    @GetMapping("/proizvodi/{id}")
    public DetaljiZaKlijentaDto detaljiProizvoda(@PathVariable Integer id, HttpSession session) {
        return proizvodService.detaljiZaKlijenta(id, klijent(session));
    }

    @GetMapping("/narudzbine")
    public List<NarudzbinaDto> narudzbine(HttpSession session) {
        return narudzbinaService.zaKlijenta(klijent(session));
    }

    @PostMapping("/narudzbine/{id}/otkazi")
    public NarudzbinaDto otkazi(@PathVariable Integer id, HttpSession session) {
        return narudzbinaService.otkazi(id, klijent(session));
    }

    @PostMapping("/narudzbine/{id}/primljeno")
    public NarudzbinaDto potvrdiPrijem(@PathVariable Integer id, HttpSession session) {
        return narudzbinaService.potvrdiPrijem(id, klijent(session));
    }

    @GetMapping("/arhiva")
    public List<ArhivaStavkaDto> arhiva(HttpSession session) {
        return arhivaService.arhiva(klijent(session));
    }

    @PostMapping("/proizvodi/{id}/ocena")
    public List<ArhivaStavkaDto> oceni(@PathVariable Integer id,
            @RequestParam VrednostOcene vrednost, HttpSession session) {
        return arhivaService.oceni(klijent(session), id, vrednost);
    }

    @PostMapping("/proizvodi/{id}/komentar")
    public List<ArhivaStavkaDto> komentarisi(@PathVariable Integer id,
            @Valid @RequestBody KomentarRequest zahtev, HttpSession session) {
        return arhivaService.komentarisi(klijent(session), id, zahtev.getTekst());
    }

    @GetMapping("/korpa")
    public KorpaDto korpa(HttpSession session) {
        return korpaService.korpa(klijent(session));
    }

    @PostMapping("/korpa")
    public KorpaDto dodajUKorpu(@Valid @RequestBody DodajUKorpuRequest zahtev, HttpSession session) {
        return korpaService.dodaj(klijent(session), zahtev);
    }

    @PutMapping("/korpa/{id}")
    public KorpaDto promeniKolicinu(@PathVariable Integer id,
            @RequestParam int kolicina, HttpSession session) {
        return korpaService.promeniKolicinu(klijent(session), id, kolicina);
    }

    @DeleteMapping("/korpa/{id}")
    public KorpaDto ukloniIzKorpe(@PathVariable Integer id, HttpSession session) {
        return korpaService.ukloni(klijent(session), id);
    }

    @DeleteMapping("/korpa")
    public KorpaDto isprazniKorpu(HttpSession session) {
        return korpaService.isprazni(klijent(session));
    }

    /** Vraca id-jeve napravljenih narudzbina — po jedna za svaku stampariju iz korpe. */
    @PostMapping("/korpa/zakljuci")
    public Map<String, Object> zakljuci(HttpSession session) {
        List<Integer> idjevi = korpaService.zakljuci(klijent(session));
        return Map.of("narudzbine", idjevi,
                "poruka", idjevi.size() == 1
                        ? "Narudžbina je uspešno poslata štampariji."
                        : "Poslato je " + idjevi.size() + " narudžbina — po jedna za svaku štampariju.");
    }

    private String klijent(HttpSession session) {
        return Sesija.zahtevajTip(session, TipKorisnika.klijent_fizicko, TipKorisnika.klijent_pravno);
    }
}
