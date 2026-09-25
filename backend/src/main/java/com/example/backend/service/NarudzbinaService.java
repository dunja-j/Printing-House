package com.example.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.NarudzbinaRepository;
import com.example.backend.dto.NarudzbinaDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.dto.StamparNarudzbinaDto;
import com.example.backend.models.Korisnik;
import com.example.backend.models.Narudzbina;
import com.example.backend.models.StatusNarudzbine;

@Service
public class NarudzbinaService {

    /** Posle ovoliko prijava da narudzbina nije stigla stamparija se vise ne prikazuje. */
    public static final int DOZVOLJENO_NEDOSTAVLJENIH = 3;

    private final NarudzbinaRepository narudzbinaRepository;
    private final KorisnikRepository korisnikRepository;

    public NarudzbinaService(NarudzbinaRepository narudzbinaRepository,
            KorisnikRepository korisnikRepository) {
        this.narudzbinaRepository = narudzbinaRepository;
        this.korisnikRepository = korisnikRepository;
    }

    @Transactional(readOnly = true)
    public List<NarudzbinaDto> zaKlijenta(String korIme) {
        return narudzbinaRepository.zaKlijenta(korIme).stream().map(NarudzbinaDto::new).toList();
    }

    @Transactional
    public NarudzbinaDto otkazi(Integer id, String korIme) {
        Narudzbina n = narudzbinaRepository.findById(id)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena."));

        // ista poruka kao za nepostojecu narudzbinu — klijent ne treba da sazna da tudja postoji
        if (!n.getKlijent().getKorIme().equals(korIme)) {
            throw new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena.");
        }
        if (n.getStatus() != StatusNarudzbine.naruceno) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Narudžbinu je moguće otkazati samo dok je u statusu \"naručeno\".");
        }

        n.setStatus(StatusNarudzbine.otkazano);
        return new NarudzbinaDto(narudzbinaRepository.save(n));
    }

    @Transactional(readOnly = true)
    public List<StamparNarudzbinaDto> zaStampara(String korIme) {
        return narudzbinaRepository.zaStampara(korIme).stream().map(StamparNarudzbinaDto::new).toList();
    }

    /** Klijent potvrđuje prijem: isporučeno → primljeno. Tek tada sme da oceni proizvod. */
    @Transactional
    public NarudzbinaDto potvrdiPrijem(Integer id, String korIme) {
        Narudzbina n = narudzbinaRepository.findById(id)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena."));

        if (!n.getKlijent().getKorIme().equals(korIme)) {
            throw new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena.");
        }
        if (n.getStatus() != StatusNarudzbine.isporuceno) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Prijem se potvrđuje samo za narudžbinu u statusu \"isporučeno\".");
        }

        n.setStatus(StatusNarudzbine.primljeno);
        return new NarudzbinaDto(narudzbinaRepository.save(n));
    }

    /**
     * Klijent prijavljuje da narudžbina nije stigla: isporučeno → nije stiglo.
     * Štamparija sa {@link #DOZVOLJENO_NEDOSTAVLJENIH} ovakvih prijava više se ne
     * prikazuje na javnim stranama (njeni proizvodi ispadaju iz pretrage).
     */
    @Transactional
    public NarudzbinaDto prijaviNedostavljeno(Integer id, String korIme) {
        Narudzbina n = narudzbinaRepository.findById(id)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena."));

        if (!n.getKlijent().getKorIme().equals(korIme)) {
            throw new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena.");
        }
        if (n.getStatus() != StatusNarudzbine.isporuceno) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Nedostavljenu pošiljku prijavljujete samo za narudžbinu u statusu \"isporučeno\".");
        }

        n.setStatus(StatusNarudzbine.nije_stiglo);

        Korisnik stampar = n.getStampar();
        stampar.setBrojNedostavljenih(stampar.getBrojNedostavljenih() + 1);
        korisnikRepository.save(stampar);

        return new NarudzbinaDto(narudzbinaRepository.save(n));
    }

    /** Štamparija pomera narudžbinu samo unapred: naručeno → u štampi → isporučeno. */
    @Transactional
    public StamparNarudzbinaDto promeniStatus(Integer id, String korIme, StatusNarudzbine noviStatus) {
        Narudzbina n = narudzbinaRepository.findById(id)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena."));

        if (!n.getStampar().getKorIme().equals(korIme)) {
            throw new PoslovnaGreska(HttpStatus.NOT_FOUND, "Narudžbina nije pronađena.");
        }

        StatusNarudzbine dozvoljeni = sledeciStatus(n.getStatus());
        if (dozvoljeni == null) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Narudžbini u statusu \"" + naziv(n.getStatus()) + "\" štamparija ne može da menja status.");
        }
        if (noviStatus != dozvoljeni) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Iz statusa \"" + naziv(n.getStatus()) + "\" moguće je preći samo u \""
                            + naziv(dozvoljeni) + "\".");
        }

        n.setStatus(noviStatus);
        return new StamparNarudzbinaDto(narudzbinaRepository.save(n));
    }

    private static StatusNarudzbine sledeciStatus(StatusNarudzbine trenutni) {
        return switch (trenutni) {
            case naruceno, placeno -> StatusNarudzbine.u_stampi;
            case u_stampi -> StatusNarudzbine.isporuceno;
            default -> null;
        };
    }

    private static String naziv(StatusNarudzbine status) {
        return switch (status) {
            case naruceno -> "naručeno";
            case placeno -> "plaćeno";
            case u_stampi -> "u štampi";
            case isporuceno -> "isporučeno";
            case primljeno -> "primljeno";
            case nije_stiglo -> "nije stiglo";
            case otkazano -> "otkazano";
        };
    }
}
