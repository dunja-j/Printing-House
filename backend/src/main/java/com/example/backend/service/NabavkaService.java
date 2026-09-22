package com.example.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.JavnaNabavkaRepository;
import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.NarudzbinaRepository;
import com.example.backend.db.dao.PonudaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.db.dao.StavkaKorpeRepository;
import com.example.backend.dto.NabavkaDto;
import com.example.backend.dto.NabavkaZaStamparaDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.JavnaNabavka;
import com.example.backend.models.Korisnik;
import com.example.backend.models.Narudzbina;
import com.example.backend.models.Ponuda;
import com.example.backend.models.Proizvod;
import com.example.backend.models.StatusNabavke;
import com.example.backend.models.StatusNarudzbine;
import com.example.backend.models.StavkaKorpe;
import com.example.backend.models.StavkaNabavke;
import com.example.backend.models.StavkaNarudzbine;
import com.example.backend.models.TipKorisnika;

/**
 * Javne nabavke pravnih lica i licitacije štamparija. Nema tajmera ni
 * zakazanih poslova — istekle nabavke se zaključuju "lenjo", pri svakom
 * čitanju liste ili slanju ponude (videti DECISIONS.md).
 */
@Service
public class NabavkaService {

    private final JavnaNabavkaRepository nabavkaRepository;
    private final PonudaRepository ponudaRepository;
    private final StavkaKorpeRepository korpaRepository;
    private final ProizvodRepository proizvodRepository;
    private final NarudzbinaRepository narudzbinaRepository;
    private final KorisnikRepository korisnikRepository;

    private final int trajanjeMinuta;

    public NabavkaService(JavnaNabavkaRepository nabavkaRepository, PonudaRepository ponudaRepository,
            StavkaKorpeRepository korpaRepository, ProizvodRepository proizvodRepository,
            NarudzbinaRepository narudzbinaRepository, KorisnikRepository korisnikRepository,
            @Value("${app.nabavka.trajanje-minuta:10}") int trajanjeMinuta) {
        this.nabavkaRepository = nabavkaRepository;
        this.ponudaRepository = ponudaRepository;
        this.korpaRepository = korpaRepository;
        this.proizvodRepository = proizvodRepository;
        this.narudzbinaRepository = narudzbinaRepository;
        this.korisnikRepository = korisnikRepository;
        this.trajanjeMinuta = trajanjeMinuta;
    }

    // ------------------------------------------------------------------
    // Institucija (klijent — pravno lice)
    // ------------------------------------------------------------------

    @Transactional
    public NabavkaDto objaviIzKorpe(String korIme) {
        Korisnik institucija = korisnik(korIme);
        if (institucija.getTip() != TipKorisnika.klijent_pravno) {
            throw new PoslovnaGreska(HttpStatus.FORBIDDEN,
                    "Javnu nabavku može objaviti samo klijent — pravno lice.");
        }

        List<StavkaKorpe> korpa = korpaRepository.zaKlijenta(korIme);
        if (korpa.isEmpty()) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Korpa je prazna.");
        }

        LocalDateTime sada = LocalDateTime.now();
        JavnaNabavka nabavka = new JavnaNabavka();
        nabavka.setInstitucija(institucija);
        nabavka.setDatumObjave(sada);
        nabavka.setRokZaPonude(sada.plusMinutes(trajanjeMinuta));
        nabavka.setStatus(StatusNabavke.otvorena);

        for (StavkaKorpe s : korpa) {
            Proizvod p = s.getProizvod();
            StavkaNabavke sn = new StavkaNabavke();
            sn.setNabavka(nabavka);
            sn.setNazivProizvoda(p.getNaziv());
            sn.setKategorija(p.getKategorija());
            sn.setPotkategorija(p.getPotkategorija());
            sn.setKolicina(s.getKolicina());
            sn.setBoja(s.getBoja());
            sn.setTipStampe(s.getUsluga() == null ? null : s.getUsluga().getTipStampe());
            sn.setTekstZaStampu(s.getTekstZaStampu());
            nabavka.getStavke().add(sn);
        }

        JavnaNabavka sacuvana = nabavkaRepository.save(nabavka);
        korpaRepository.deleteAll(korpa);
        return new NabavkaDto(sacuvana, List.of());
    }

    @Transactional
    public List<NabavkaDto> mojeNabavke(String korIme) {
        zakljuciIstekle();

        List<JavnaNabavka> nabavke = nabavkaRepository.zaInstituciju(korIme);
        Map<Integer, List<Ponuda>> ponude = ponudePoNabavci(nabavke);
        return nabavke.stream()
                .map(n -> new NabavkaDto(n, ponude.getOrDefault(n.getId(), List.of())))
                .toList();
    }

    // ------------------------------------------------------------------
    // Štamparija (licitacija)
    // ------------------------------------------------------------------

    @Transactional
    public List<NabavkaZaStamparaDto> otvoreneNabavke(String stamparKorIme) {
        zakljuciIstekle();

        List<JavnaNabavka> nabavke = nabavkaRepository.saStatusom(StatusNabavke.otvorena);
        Map<Integer, List<Ponuda>> ponude = ponudePoNabavci(nabavke);
        List<Proizvod> mojiProizvodi = aktivniProizvodi(stamparKorIme);

        List<NabavkaZaStamparaDto> rezultat = new ArrayList<>();
        for (JavnaNabavka n : nabavke) {
            List<Ponuda> sve = ponude.getOrDefault(n.getId(), List.of());
            BigDecimal moja = sve.stream()
                    .filter(p -> p.getStampar().getKorIme().equals(stamparKorIme))
                    .map(Ponuda::getUkupnaCena)
                    .findFirst().orElse(null);

            boolean moze = moja == null && upari(mojiProizvodi, n.getStavke()) != null;
            String razlog = null;
            if (moja != null) {
                razlog = "Već ste poslali ponudu za ovu nabavku.";
            } else if (!moze) {
                razlog = "Nemate aktivne proizvode sa dovoljnom količinom na stanju za sve stavke.";
            }

            rezultat.add(new NabavkaZaStamparaDto(n, sve.size(), moja, moze, razlog));
        }
        return rezultat;
    }

    @Transactional
    public NabavkaZaStamparaDto posaljiPonudu(String stamparKorIme, Integer nabavkaId,
            BigDecimal ukupnaCena) {
        zakljuciIstekle();

        JavnaNabavka nabavka = nabavkaRepository.nadjiSaInstitucijom(nabavkaId)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Javna nabavka nije pronađena."));

        if (nabavka.getStatus() != StatusNabavke.otvorena) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Rok za slanje ponuda je istekao.");
        }
        if (ponudaRepository.existsByNabavkaIdAndStamparKorIme(nabavkaId, stamparKorIme)) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Već ste poslali ponudu za ovu nabavku.");
        }
        if (upari(aktivniProizvodi(stamparKorIme), nabavka.getStavke()) == null) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Nemate aktivne proizvode sa dovoljnom količinom na stanju za sve stavke ove nabavke.");
        }

        Ponuda ponuda = new Ponuda();
        ponuda.setNabavka(nabavka);
        ponuda.setStampar(korisnik(stamparKorIme));
        ponuda.setUkupnaCena(ukupnaCena);
        ponuda.setDatum(LocalDateTime.now());
        ponudaRepository.save(ponuda);

        List<Ponuda> sve = ponudaRepository.zaNabavku(nabavkaId);
        return new NabavkaZaStamparaDto(nabavka, sve.size(), ukupnaCena, false,
                "Već ste poslali ponudu za ovu nabavku.");
    }

    // ------------------------------------------------------------------
    // Zaključivanje isteklih licitacija
    // ------------------------------------------------------------------

    @Transactional
    public void zakljuciIstekle() {
        LocalDateTime sada = LocalDateTime.now();
        for (JavnaNabavka n : nabavkaRepository.saStatusom(StatusNabavke.otvorena)) {
            if (!n.getRokZaPonude().isAfter(sada)) {
                zakljuci(n);
            }
        }
    }

    /** Pobeđuje najniža ponuda čija štamparija i dalje ima sve na stanju. */
    private void zakljuci(JavnaNabavka nabavka) {
        for (Ponuda ponuda : ponudaRepository.zaNabavku(nabavka.getId())) {
            Map<Integer, Proizvod> izbor =
                    upari(aktivniProizvodi(ponuda.getStampar().getKorIme()), nabavka.getStavke());
            if (izbor == null) {
                continue;
            }

            ponuda.setPobednicka(true);
            ponudaRepository.save(ponuda);
            nabavka.setNarudzbina(napraviNarudzbinu(nabavka, ponuda, izbor));
            nabavka.setStatus(StatusNabavke.zakljucena);
            nabavkaRepository.save(nabavka);
            return;
        }

        nabavka.setStatus(StatusNabavke.neuspela);
        nabavkaRepository.save(nabavka);
    }

    private Narudzbina napraviNarudzbinu(JavnaNabavka nabavka, Ponuda pobednicka,
            Map<Integer, Proizvod> izbor) {
        Narudzbina n = new Narudzbina();
        n.setKlijent(nabavka.getInstitucija());
        n.setStampar(pobednicka.getStampar());
        // specifikacija: dobijena nabavka se odmah fakturiše i ide u štampu
        n.setStatus(StatusNarudzbine.u_stampi);
        n.setDatumNarudzbine(LocalDateTime.now());
        n.setUkupanIznos(pobednicka.getUkupnaCena());

        List<StavkaNabavke> stavke = nabavka.getStavke();
        BigDecimal redovnoUkupno = BigDecimal.ZERO;
        for (StavkaNabavke s : stavke) {
            redovnoUkupno = redovnoUkupno.add(redovnaCena(izbor.get(s.getId()), s.getKolicina()));
        }

        BigDecimal rasporedjeno = BigDecimal.ZERO;
        for (int i = 0; i < stavke.size(); i++) {
            StavkaNabavke s = stavke.get(i);
            Proizvod p = izbor.get(s.getId());

            BigDecimal cena;
            if (i == stavke.size() - 1) {
                cena = pobednicka.getUkupnaCena().subtract(rasporedjeno);
            } else if (redovnoUkupno.signum() == 0) {
                cena = BigDecimal.ZERO;
            } else {
                cena = pobednicka.getUkupnaCena()
                        .multiply(redovnaCena(p, s.getKolicina()))
                        .divide(redovnoUkupno, 2, RoundingMode.HALF_UP);
            }
            rasporedjeno = rasporedjeno.add(cena);

            StavkaNarudzbine sn = new StavkaNarudzbine();
            sn.setNarudzbina(n);
            sn.setProizvod(p);
            sn.setKolicina(s.getKolicina());
            sn.setBoja(s.getBoja());
            sn.setTekstZaStampu(s.getTekstZaStampu());
            sn.setCenaStavke(cena);
            n.getStavke().add(sn);

            p.setKolicinaNaLageru(p.getKolicinaNaLageru() - s.getKolicina());
        }

        return narudzbinaRepository.save(n);
    }

    // ------------------------------------------------------------------
    // Uparivanje traženih stavki sa proizvodima štamparije
    // ------------------------------------------------------------------

    /**
     * Za svaku stavku bira najjeftiniji aktivan proizvod štamparije iz tražene
     * potkategorije (ili kategorije, ako potkategorija nije zadata) koji ima
     * dovoljno na stanju. Vraća {@code null} ako makar jedna stavka ne može da
     * se pokrije — takva ponuda se odbija.
     */
    private Map<Integer, Proizvod> upari(List<Proizvod> ponuda, List<StavkaNabavke> stavke) {
        Map<Integer, Integer> preostalo = new HashMap<>();
        Map<Integer, Proizvod> izbor = new LinkedHashMap<>();

        for (StavkaNabavke s : stavke) {
            Proizvod najbolji = null;
            int lagerNajboljeg = 0;

            for (Proizvod p : ponuda) {
                if (!odgovara(p, s)) {
                    continue;
                }
                int naStanju = preostalo.getOrDefault(p.getId(), p.getKolicinaNaLageru());
                if (naStanju < s.getKolicina()) {
                    continue;
                }
                if (najbolji == null || p.getJedinicnaCena().compareTo(najbolji.getJedinicnaCena()) < 0) {
                    najbolji = p;
                    lagerNajboljeg = naStanju;
                }
            }

            if (najbolji == null) {
                return null;
            }
            preostalo.put(najbolji.getId(), lagerNajboljeg - s.getKolicina());
            izbor.put(s.getId(), najbolji);
        }
        return izbor;
    }

    private boolean odgovara(Proizvod p, StavkaNabavke s) {
        if (s.getPotkategorija() != null) {
            return p.getPotkategorija() != null
                    && p.getPotkategorija().getId().equals(s.getPotkategorija().getId());
        }
        return p.getKategorija().getId().equals(s.getKategorija().getId());
    }

    private List<Proizvod> aktivniProizvodi(String stamparKorIme) {
        return proizvodRepository.zaStampara(stamparKorIme).stream().filter(Proizvod::isAktivan).toList();
    }

    private Map<Integer, List<Ponuda>> ponudePoNabavci(List<JavnaNabavka> nabavke) {
        if (nabavke.isEmpty()) {
            return Map.of();
        }
        List<Integer> ids = nabavke.stream().map(JavnaNabavka::getId).toList();

        Map<Integer, List<Ponuda>> po = new HashMap<>();
        for (Ponuda p : ponudaRepository.zaNabavke(ids)) {
            po.computeIfAbsent(p.getNabavka().getId(), k -> new ArrayList<>()).add(p);
        }
        return po;
    }

    private static BigDecimal redovnaCena(Proizvod p, int kolicina) {
        return p.getJedinicnaCena().multiply(BigDecimal.valueOf(kolicina));
    }

    private Korisnik korisnik(String korIme) {
        return korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni."));
    }
}
