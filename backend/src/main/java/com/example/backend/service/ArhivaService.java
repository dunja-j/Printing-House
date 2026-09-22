package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KomentarProizvodaRepository;
import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.OcenaProizvodaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.dto.ArhivaProizvodDto;
import com.example.backend.dto.KomentarDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.KomentarProizvoda;
import com.example.backend.models.Korisnik;
import com.example.backend.models.OcenaProizvoda;
import com.example.backend.models.Proizvod;
import com.example.backend.models.VrednostOcene;

/** Arhiva primljenih proizvoda: ocenjivanje i komentarisanje. */
@Service
public class ArhivaService {

    private static final int BROJ_KOMENTARA = 5;
    private static final int MAX_DUZINA_KOMENTARA = 500;

    private final ProizvodRepository proizvodRepository;
    private final OcenaProizvodaRepository ocenaRepository;
    private final KomentarProizvodaRepository komentarRepository;
    private final KorisnikRepository korisnikRepository;

    public ArhivaService(ProizvodRepository proizvodRepository,
            OcenaProizvodaRepository ocenaRepository,
            KomentarProizvodaRepository komentarRepository,
            KorisnikRepository korisnikRepository) {
        this.proizvodRepository = proizvodRepository;
        this.ocenaRepository = ocenaRepository;
        this.komentarRepository = komentarRepository;
        this.korisnikRepository = korisnikRepository;
    }

    @Transactional(readOnly = true)
    public List<ArhivaProizvodDto> arhiva(String korIme) {
        return proizvodRepository.primljeniOdKlijenta(korIme).stream()
                .map(p -> sastavi(p, korIme))
                .toList();
    }

    /** Ponovni klik na istu ocenu je poništava. */
    @Transactional
    public ArhivaProizvodDto oceni(String korIme, Integer proizvodId, VrednostOcene vrednost) {
        Proizvod p = primljenProizvod(korIme, proizvodId);

        ocenaRepository.findByProizvodIdAndKlijentKorIme(proizvodId, korIme).ifPresentOrElse(
                postojeca -> {
                    if (postojeca.getVrednost() == vrednost) {
                        ocenaRepository.delete(postojeca);
                    } else {
                        postojeca.setVrednost(vrednost);
                        postojeca.setDatum(LocalDateTime.now());
                        ocenaRepository.save(postojeca);
                    }
                },
                () -> {
                    OcenaProizvoda nova = new OcenaProizvoda();
                    nova.setProizvod(p);
                    nova.setKlijent(klijent(korIme));
                    nova.setVrednost(vrednost);
                    nova.setDatum(LocalDateTime.now());
                    ocenaRepository.save(nova);
                });

        return sastavi(p, korIme);
    }

    @Transactional
    public ArhivaProizvodDto komentarisi(String korIme, Integer proizvodId, String tekst) {
        Proizvod p = primljenProizvod(korIme, proizvodId);

        if (tekst == null || tekst.isBlank()) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Komentar ne sme biti prazan.");
        }
        if (tekst.trim().length() > MAX_DUZINA_KOMENTARA) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "Komentar sme imati najviše " + MAX_DUZINA_KOMENTARA + " karaktera.");
        }

        KomentarProizvoda k = new KomentarProizvoda();
        k.setProizvod(p);
        k.setKlijent(klijent(korIme));
        k.setTekst(tekst.trim());
        k.setDatum(LocalDateTime.now());
        komentarRepository.save(k);

        return sastavi(p, korIme);
    }

    private ArhivaProizvodDto sastavi(Proizvod p, String korIme) {
        List<KomentarDto> komentari = komentarRepository
                .poslednjiZaProizvod(p.getId(), PageRequest.of(0, BROJ_KOMENTARA)).stream()
                .map(k -> new KomentarDto(k, korIme))
                .toList();

        VrednostOcene moja = ocenaRepository.findByProizvodIdAndKlijentKorIme(p.getId(), korIme)
                .map(OcenaProizvoda::getVrednost)
                .orElse(null);

        return new ArhivaProizvodDto(p,
                ocenaRepository.countByProizvodIdAndVrednost(p.getId(), VrednostOcene.lajk),
                ocenaRepository.countByProizvodIdAndVrednost(p.getId(), VrednostOcene.dislajk),
                moja, komentari);
    }

    /** Oceniti i komentarisati sme samo onaj ko je proizvod stvarno primio. */
    private Proizvod primljenProizvod(String korIme, Integer proizvodId) {
        return proizvodRepository.primljeniOdKlijenta(korIme).stream()
                .filter(p -> p.getId().equals(proizvodId))
                .findFirst()
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.FORBIDDEN,
                        "Možete oceniti samo proizvod koji ste primili."));
    }

    private Korisnik klijent(String korIme) {
        return korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni."));
    }
}
