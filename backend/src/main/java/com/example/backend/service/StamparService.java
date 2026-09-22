package com.example.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.db.dao.KategorijaRepository;
import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.PotkategorijaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.db.dao.UslugaStampeRepository;
import com.example.backend.dto.KategorijaDto;
import com.example.backend.dto.KategorijaSaPotkategorijamaDto;
import com.example.backend.dto.NovaUslugaStampeRequest;
import com.example.backend.dto.NoviProizvodRequest;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.dto.StamparProizvodDto;
import com.example.backend.models.Kategorija;
import com.example.backend.models.Korisnik;
import com.example.backend.models.Potkategorija;
import com.example.backend.models.Proizvod;
import com.example.backend.models.UslugaStampe;

@Service
public class StamparService {

    private final ProizvodRepository proizvodRepository;
    private final KategorijaRepository kategorijaRepository;
    private final PotkategorijaRepository potkategorijaRepository;
    private final UslugaStampeRepository uslugaRepository;
    private final KorisnikRepository korisnikRepository;
    private final SlikaService slikaService;

    public StamparService(ProizvodRepository proizvodRepository,
            KategorijaRepository kategorijaRepository,
            PotkategorijaRepository potkategorijaRepository,
            UslugaStampeRepository uslugaRepository,
            KorisnikRepository korisnikRepository,
            SlikaService slikaService) {
        this.proizvodRepository = proizvodRepository;
        this.kategorijaRepository = kategorijaRepository;
        this.potkategorijaRepository = potkategorijaRepository;
        this.uslugaRepository = uslugaRepository;
        this.korisnikRepository = korisnikRepository;
        this.slikaService = slikaService;
    }

    /** Sve kategorije sa potkategorijama — štampar bira iz predefinisanog spiska. */
    @Transactional(readOnly = true)
    public List<KategorijaSaPotkategorijamaDto> kategorije() {
        List<KategorijaSaPotkategorijamaDto> rezultat = new ArrayList<>();
        for (Kategorija k : kategorijaRepository.findAll()) {
            List<KategorijaDto> pot = potkategorijaRepository
                    .findByKategorijaIdOrderByNazivAsc(k.getId()).stream()
                    .map(p -> new KategorijaDto(p.getId(), p.getNaziv()))
                    .toList();
            rezultat.add(new KategorijaSaPotkategorijamaDto(k.getId(), k.getNaziv(), pot));
        }
        rezultat.sort((a, b) -> a.getNaziv().compareTo(b.getNaziv()));
        return rezultat;
    }

    @Transactional(readOnly = true)
    public List<StamparProizvodDto> mojiProizvodi(String korIme) {
        return proizvodRepository.zaStampara(korIme).stream().map(StamparProizvodDto::new).toList();
    }

    @Transactional
    public StamparProizvodDto dodaj(String korIme, NoviProizvodRequest zahtev) {
        Korisnik stampar = korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni."));

        String sifra = zahtev.getSifra().trim();
        if (proizvodRepository.existsBySifraAndStamparKorIme(sifra, korIme)) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Već imate proizvod sa šifrom \"" + sifra + "\".");
        }

        Kategorija kategorija = kategorijaRepository.findById(zahtev.getKategorijaId())
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Kategorija ne postoji."));
        Potkategorija potkategorija = proveriPotkategoriju(zahtev.getPotkategorijaId(), kategorija);

        Proizvod p = new Proizvod();
        p.setSifra(sifra);
        p.setStampar(stampar);
        p.setNaziv(zahtev.getNaziv().trim());
        p.setOpis(prazanKaoNull(zahtev.getOpis()));
        p.setKategorija(kategorija);
        p.setPotkategorija(potkategorija);
        p.setJedinicnaCena(zahtev.getJedinicnaCena());
        p.setKolicinaNaLageru(zahtev.getKolicinaNaLageru());
        p.setAktivan(true);
        p.setDostupneBoje(ocisti(zahtev.getDostupneBoje()));
        Proizvod sacuvan = proizvodRepository.save(p);

        if (zahtev.getUslugeStampe() != null) {
            for (NovaUslugaStampeRequest u : zahtev.getUslugeStampe()) {
                UslugaStampe usluga = new UslugaStampe();
                usluga.setProizvod(sacuvan);
                usluga.setTipStampe(u.getTipStampe().trim());
                usluga.setDodatnaCenaPoKomadu(u.getDodatnaCenaPoKomadu());
                usluga.setMaxSirinaMm(u.getMaxSirinaMm());
                usluga.setMaxVisinaMm(u.getMaxVisinaMm());
                sacuvan.getUslugeStampe().add(uslugaRepository.save(usluga));
            }
        }
        return new StamparProizvodDto(sacuvan);
    }

    @Transactional
    public StamparProizvodDto promeniKolicinu(String korIme, Integer proizvodId, int kolicina) {
        if (kolicina < 0) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Količina ne može biti negativna.");
        }
        Proizvod p = mojProizvod(korIme, proizvodId);
        p.setKolicinaNaLageru(kolicina);
        return new StamparProizvodDto(proizvodRepository.save(p));
    }

    @Transactional
    public StamparProizvodDto promeniDostupnost(String korIme, Integer proizvodId, boolean aktivan) {
        Proizvod p = mojProizvod(korIme, proizvodId);
        p.setAktivan(aktivan);
        return new StamparProizvodDto(proizvodRepository.save(p));
    }

    @Transactional
    public StamparProizvodDto promeniSliku(String korIme, Integer proizvodId, MultipartFile slika) {
        Proizvod p = mojProizvod(korIme, proizvodId);
        p.setSlikaUrl(slikaService.sacuvajProizvod(slika, p.getSifra()));
        return new StamparProizvodDto(proizvodRepository.save(p));
    }

    private Proizvod mojProizvod(String korIme, Integer proizvodId) {
        Proizvod p = proizvodRepository.findById(proizvodId)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Proizvod nije pronađen."));

        // ista poruka kao za nepostojeci — stampar ne treba da saznaje za tudje proizvode
        if (!p.getStampar().getKorIme().equals(korIme)) {
            throw new PoslovnaGreska(HttpStatus.NOT_FOUND, "Proizvod nije pronađen.");
        }
        return p;
    }

    private Potkategorija proveriPotkategoriju(Integer potkategorijaId, Kategorija kategorija) {
        if (potkategorijaId == null) {
            return null;
        }
        Potkategorija pot = potkategorijaRepository.findById(potkategorijaId)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Potkategorija ne postoji."));

        if (!pot.getKategorija().getId().equals(kategorija.getId())) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "Potkategorija ne pripada izabranoj kategoriji.");
        }
        return pot;
    }

    private static List<String> ocisti(List<String> boje) {
        if (boje == null) {
            return new ArrayList<>();
        }
        return boje.stream()
                .filter(b -> b != null && !b.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private static String prazanKaoNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
