package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KomentarProizvodaRepository;
import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.NarudzbinaRepository;
import com.example.backend.db.dao.OcenaProizvodaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.dto.ArhivaStavkaDto;
import com.example.backend.dto.KomentarDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.KomentarProizvoda;
import com.example.backend.models.Korisnik;
import com.example.backend.models.OcenaProizvoda;
import com.example.backend.models.Proizvod;
import com.example.backend.models.StavkaNarudzbine;
import com.example.backend.models.VrednostOcene;

/**
 * Arhiva klijenta: isporučene i primljene stavke narudžbina. Isporučenu stavku
 * klijent prvo potvrđuje kao primljenu, pa tek onda sme da oceni i komentariše
 * proizvod.
 */
@Service
public class ArhivaService {

    private static final int BROJ_KOMENTARA = 3;
    private static final int MAX_DUZINA_KOMENTARA = 500;

    private final NarudzbinaRepository narudzbinaRepository;
    private final ProizvodRepository proizvodRepository;
    private final OcenaProizvodaRepository ocenaRepository;
    private final KomentarProizvodaRepository komentarRepository;
    private final KorisnikRepository korisnikRepository;

    public ArhivaService(NarudzbinaRepository narudzbinaRepository,
            ProizvodRepository proizvodRepository,
            OcenaProizvodaRepository ocenaRepository,
            KomentarProizvodaRepository komentarRepository,
            KorisnikRepository korisnikRepository) {
        this.narudzbinaRepository = narudzbinaRepository;
        this.proizvodRepository = proizvodRepository;
        this.ocenaRepository = ocenaRepository;
        this.komentarRepository = komentarRepository;
        this.korisnikRepository = korisnikRepository;
    }

    @Transactional(readOnly = true)
    public List<ArhivaStavkaDto> arhiva(String korIme) {
        List<StavkaNarudzbine> stavke = narudzbinaRepository.arhivaKlijenta(korIme);

        // isti proizvod može biti u više narudžbina — ocene i komentari se čitaju jednom
        Map<Integer, List<KomentarDto>> komentari = new HashMap<>();
        Map<Integer, VrednostOcene> ocene = new HashMap<>();
        Map<Integer, long[]> brojaci = new HashMap<>();

        for (StavkaNarudzbine s : stavke) {
            Integer proizvodId = s.getProizvod().getId();
            komentari.computeIfAbsent(proizvodId, id -> komentarRepository
                    .poslednjiZaProizvod(id, PageRequest.of(0, BROJ_KOMENTARA)).stream()
                    .map(k -> new KomentarDto(k, korIme))
                    .toList());
            ocene.computeIfAbsent(proizvodId, id -> ocenaRepository
                    .findByProizvodIdAndKlijentKorIme(id, korIme)
                    .map(OcenaProizvoda::getVrednost).orElse(null));
            brojaci.computeIfAbsent(proizvodId, id -> new long[] {
                    ocenaRepository.countByProizvodIdAndVrednost(id, VrednostOcene.lajk),
                    ocenaRepository.countByProizvodIdAndVrednost(id, VrednostOcene.dislajk) });
        }

        return stavke.stream().map(s -> {
            Integer id = s.getProizvod().getId();
            long[] broj = brojaci.get(id);
            return new ArhivaStavkaDto(s, broj[0], broj[1], ocene.get(id), komentari.get(id));
        }).toList();
    }

    /** Ponovni klik na istu ocenu je poništava. */
    @Transactional
    public List<ArhivaStavkaDto> oceni(String korIme, Integer proizvodId, VrednostOcene vrednost) {
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

        return arhiva(korIme);
    }

    @Transactional
    public List<ArhivaStavkaDto> komentarisi(String korIme, Integer proizvodId, String tekst) {
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

        return arhiva(korIme);
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
