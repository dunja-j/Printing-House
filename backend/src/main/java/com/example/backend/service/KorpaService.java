package com.example.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.NarudzbinaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.db.dao.StavkaKorpeRepository;
import com.example.backend.dto.DodajUKorpuRequest;
import com.example.backend.dto.GrupaKorpeDto;
import com.example.backend.dto.KorpaDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.dto.StavkaKorpeDto;
import com.example.backend.models.Korisnik;
import com.example.backend.models.Narudzbina;
import com.example.backend.models.Proizvod;
import com.example.backend.models.StatusNarudzbine;
import com.example.backend.models.StavkaKorpe;
import com.example.backend.models.StavkaNarudzbine;
import com.example.backend.models.TipKorisnika;
import com.example.backend.models.UslugaStampe;

@Service
public class KorpaService {

    private final StavkaKorpeRepository korpaRepository;
    private final ProizvodRepository proizvodRepository;
    private final KorisnikRepository korisnikRepository;
    private final NarudzbinaRepository narudzbinaRepository;

    public KorpaService(StavkaKorpeRepository korpaRepository, ProizvodRepository proizvodRepository,
            KorisnikRepository korisnikRepository, NarudzbinaRepository narudzbinaRepository) {
        this.korpaRepository = korpaRepository;
        this.proizvodRepository = proizvodRepository;
        this.korisnikRepository = korisnikRepository;
        this.narudzbinaRepository = narudzbinaRepository;
    }

    @Transactional(readOnly = true)
    public KorpaDto korpa(String korIme) {
        return sastaviKorpu(korIme);
    }

    @Transactional
    public KorpaDto dodaj(String korIme, DodajUKorpuRequest zahtev) {
        Korisnik klijent = klijent(korIme);
        Proizvod proizvod = proizvodRepository.nadjiAktivanSaDetaljima(zahtev.getProizvodId())
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Proizvod nije pronađen."));

        UslugaStampe usluga = proveriUslugu(proizvod, zahtev.getUslugaId());
        String boja = proveriBoju(proizvod, zahtev.getBoja());
        proveriKolicinu(proizvod, zahtev.getKolicina());

        StavkaKorpe stavka = new StavkaKorpe();
        stavka.setKlijent(klijent);
        stavka.setProizvod(proizvod);
        stavka.setUsluga(usluga);
        stavka.setBoja(boja);
        stavka.setKolicina(zahtev.getKolicina());
        stavka.setTekstZaStampu(prazanKaoNull(zahtev.getTekstZaStampu()));
        stavka.setDatumDodavanja(LocalDateTime.now());
        korpaRepository.save(stavka);

        return sastaviKorpu(korIme);
    }

    @Transactional
    public KorpaDto promeniKolicinu(String korIme, Integer stavkaId, int kolicina) {
        StavkaKorpe stavka = mojaStavka(korIme, stavkaId);
        proveriKolicinu(stavka.getProizvod(), kolicina);

        stavka.setKolicina(kolicina);
        korpaRepository.save(stavka);
        return sastaviKorpu(korIme);
    }

    @Transactional
    public KorpaDto ukloni(String korIme, Integer stavkaId) {
        korpaRepository.delete(mojaStavka(korIme, stavkaId));
        return sastaviKorpu(korIme);
    }

    @Transactional
    public KorpaDto isprazni(String korIme) {
        korpaRepository.deleteByKlijentKorIme(korIme);
        return sastaviKorpu(korIme);
    }

    /**
     * Zatvaranje narudzbine — od svake stamparije u korpi nastaje zasebna
     * narudzbina (tj. zasebna faktura), kako trazi specifikacija.
     */
    @Transactional
    public List<Integer> zakljuci(String korIme) {
        Korisnik klijent = klijent(korIme);

        if (klijent.getTip() == TipKorisnika.klijent_pravno) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Pravno lice ne zatvara narudžbinu direktno — porudžbina ide u javnu nabavku.");
        }

        List<StavkaKorpe> stavke = korpaRepository.zaKlijenta(korIme);
        if (stavke.isEmpty()) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Korpa je prazna.");
        }

        // lager se proverava ponovo — mogao se promeniti dok je korpa stajala
        for (StavkaKorpe s : stavke) {
            proveriKolicinu(s.getProizvod(), s.getKolicina());
        }

        Map<String, List<StavkaKorpe>> poStampariji = new LinkedHashMap<>();
        for (StavkaKorpe s : stavke) {
            poStampariji.computeIfAbsent(s.getProizvod().getStampar().getKorIme(), k -> new ArrayList<>())
                    .add(s);
        }

        List<Integer> napravljene = new ArrayList<>();
        for (List<StavkaKorpe> grupa : poStampariji.values()) {
            napravljene.add(napraviNarudzbinu(klijent, grupa).getId());
        }

        korpaRepository.deleteAll(stavke);
        return napravljene;
    }

    private Narudzbina napraviNarudzbinu(Korisnik klijent, List<StavkaKorpe> grupa) {
        Narudzbina n = new Narudzbina();
        n.setKlijent(klijent);
        n.setStampar(grupa.get(0).getProizvod().getStampar());
        n.setStatus(StatusNarudzbine.naruceno);
        n.setDatumNarudzbine(LocalDateTime.now());

        BigDecimal ukupno = BigDecimal.ZERO;
        for (StavkaKorpe s : grupa) {
            Proizvod p = s.getProizvod();
            BigDecimal poKomadu = p.getJedinicnaCena().add(
                    s.getUsluga() == null ? BigDecimal.ZERO : s.getUsluga().getDodatnaCenaPoKomadu());
            BigDecimal cenaStavke = poKomadu.multiply(BigDecimal.valueOf(s.getKolicina()));

            StavkaNarudzbine sn = new StavkaNarudzbine();
            sn.setNarudzbina(n);
            sn.setProizvod(p);
            sn.setUsluga(s.getUsluga());
            sn.setKolicina(s.getKolicina());
            sn.setBoja(s.getBoja());
            sn.setTekstZaStampu(s.getTekstZaStampu());
            sn.setSlicicaZaStampuUrl(s.getSlicicaZaStampuUrl());
            sn.setCenaStavke(cenaStavke);
            n.getStavke().add(sn);

            p.setKolicinaNaLageru(p.getKolicinaNaLageru() - s.getKolicina());
            ukupno = ukupno.add(cenaStavke);
        }
        n.setUkupanIznos(ukupno);
        return narudzbinaRepository.save(n);
    }

    private KorpaDto sastaviKorpu(String korIme) {
        Map<String, List<StavkaKorpe>> poStampariji = new LinkedHashMap<>();
        for (StavkaKorpe s : korpaRepository.zaKlijenta(korIme)) {
            poStampariji.computeIfAbsent(s.getProizvod().getStampar().getKorIme(), k -> new ArrayList<>())
                    .add(s);
        }

        List<GrupaKorpeDto> grupe = new ArrayList<>();
        for (List<StavkaKorpe> grupa : poStampariji.values()) {
            Korisnik stampar = grupa.get(0).getProizvod().getStampar();
            grupe.add(new GrupaKorpeDto(stampar.getKorIme(), stampar.getNazivInstitucije(),
                    stampar.getGrad(), grupa.stream().map(StavkaKorpeDto::new).toList()));
        }

        boolean pravnoLice = klijent(korIme).getTip() == TipKorisnika.klijent_pravno;
        return new KorpaDto(grupe, pravnoLice);
    }

    private Korisnik klijent(String korIme) {
        return korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni."));
    }

    private StavkaKorpe mojaStavka(String korIme, Integer stavkaId) {
        StavkaKorpe stavka = korpaRepository.findById(stavkaId)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Stavka nije pronađena."));

        // ista poruka kao za nepostojecu — klijent ne treba da sazna za tudje stavke
        if (!stavka.getKlijent().getKorIme().equals(korIme)) {
            throw new PoslovnaGreska(HttpStatus.NOT_FOUND, "Stavka nije pronađena.");
        }
        return stavka;
    }

    private UslugaStampe proveriUslugu(Proizvod proizvod, Integer uslugaId) {
        if (proizvod.getUslugeStampe().isEmpty()) {
            return null;
        }
        if (uslugaId == null) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Izaberite uslugu štampe.");
        }
        return proizvod.getUslugeStampe().stream()
                .filter(u -> u.getId().equals(uslugaId))
                .findFirst()
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                        "Izabrana usluga štampe ne pripada ovom proizvodu."));
    }

    private String proveriBoju(Proizvod proizvod, String boja) {
        if (proizvod.getDostupneBoje().isEmpty()) {
            return null;
        }
        if (boja == null || boja.isBlank()) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Izaberite boju.");
        }
        if (!proizvod.getDostupneBoje().contains(boja)) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Izabrana boja nije dostupna za ovaj proizvod.");
        }
        return boja;
    }

    private void proveriKolicinu(Proizvod proizvod, int kolicina) {
        if (kolicina < 1) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Količina mora biti najmanje 1.");
        }
        if (kolicina > proizvod.getKolicinaNaLageru()) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Na stanju je samo " + proizvod.getKolicinaNaLageru() + " kom. proizvoda \""
                            + proizvod.getNaziv() + "\".");
        }
    }

    private static String prazanKaoNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
