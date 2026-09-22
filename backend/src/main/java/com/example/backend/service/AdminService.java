package com.example.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KategorijaRepository;
import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.NarudzbinaRepository;
import com.example.backend.db.dao.PotkategorijaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.dto.AdminKorisnikDto;
import com.example.backend.dto.AzuriranjeProfilaRequest;
import com.example.backend.dto.KategorijaDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.Kategorija;
import com.example.backend.models.Korisnik;
import com.example.backend.models.Potkategorija;
import com.example.backend.models.StatusRegistracije;
import com.example.backend.models.TipKorisnika;

@Service
public class AdminService {

    private final KorisnikRepository korisnikRepository;
    private final ProizvodRepository proizvodRepository;
    private final NarudzbinaRepository narudzbinaRepository;
    private final KategorijaRepository kategorijaRepository;
    private final PotkategorijaRepository potkategorijaRepository;
    private final ProfilService profilService;

    public AdminService(KorisnikRepository korisnikRepository, ProizvodRepository proizvodRepository,
            NarudzbinaRepository narudzbinaRepository, KategorijaRepository kategorijaRepository,
            PotkategorijaRepository potkategorijaRepository, ProfilService profilService) {
        this.korisnikRepository = korisnikRepository;
        this.proizvodRepository = proizvodRepository;
        this.narudzbinaRepository = narudzbinaRepository;
        this.kategorijaRepository = kategorijaRepository;
        this.potkategorijaRepository = potkategorijaRepository;
        this.profilService = profilService;
    }

    /** Nalozi koji čekaju odluku administratora. */
    @Transactional(readOnly = true)
    public List<AdminKorisnikDto> zahteviZaRegistraciju() {
        return korisnikRepository
                .findByStatusRegistracijeOrderByDatumRegistracijeAsc(StatusRegistracije.na_cekanju)
                .stream().map(AdminKorisnikDto::new).toList();
    }

    @Transactional
    public AdminKorisnikDto odluciOZahtevu(String korIme, boolean prihvati) {
        Korisnik k = nadji(korIme);

        if (k.getStatusRegistracije() != StatusRegistracije.na_cekanju) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "O ovom zahtevu je već odlučeno.");
        }
        k.setStatusRegistracije(prihvati ? StatusRegistracije.odobren : StatusRegistracije.odbijen);
        return new AdminKorisnikDto(korisnikRepository.save(k));
    }

    /** Svi nalozi osim administratorskih. */
    @Transactional(readOnly = true)
    public List<AdminKorisnikDto> korisnici() {
        return korisnikRepository.findByTipNotOrderByKorImeAsc(TipKorisnika.administrator)
                .stream().map(AdminKorisnikDto::new).toList();
    }

    @Transactional
    public AdminKorisnikDto azuriraj(String korIme, AzuriranjeProfilaRequest zahtev) {
        Korisnik k = nadji(korIme);
        if (k.getTip() == TipKorisnika.administrator) {
            throw new PoslovnaGreska(HttpStatus.FORBIDDEN, "Administratorski nalog se ne menja ovde.");
        }
        return new AdminKorisnikDto(profilService.azuriraj(korIme, zahtev));
    }

    /**
     * Brisanje je moguće samo ako nalog nema proizvode ni narudžbine — strani
     * ključevi su namerno RESTRICT da bi istorija ostala ispravna.
     */
    @Transactional
    public void obrisi(String korIme, String adminKorIme) {
        Korisnik k = nadji(korIme);

        if (korIme.equals(adminKorIme)) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Ne možete obrisati sopstveni nalog.");
        }
        if (k.getTip() == TipKorisnika.administrator) {
            throw new PoslovnaGreska(HttpStatus.FORBIDDEN, "Administratorski nalog se ne može obrisati.");
        }

        if (k.getTip() == TipKorisnika.stampar && !proizvodRepository.zaStampara(korIme).isEmpty()) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Nalog ima proizvode u ponudi, pa se ne može obrisati. Prvo ih uklonite iz ponude.");
        }
        if (!narudzbinaRepository.zaKlijenta(korIme).isEmpty()
                || !narudzbinaRepository.zaStampara(korIme).isEmpty()) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Nalog ima narudžbine, pa se ne može obrisati bez gubitka istorije.");
        }

        korisnikRepository.delete(k);
    }

    @Transactional
    public KategorijaDto dodajKategoriju(String naziv) {
        String ocisceno = naziv.trim();
        boolean postoji = kategorijaRepository.findAll().stream()
                .anyMatch(k -> k.getNaziv().equalsIgnoreCase(ocisceno));
        if (postoji) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Kategorija sa tim nazivom već postoji.");
        }

        Kategorija k = new Kategorija();
        k.setNaziv(ocisceno);
        Kategorija sacuvana = kategorijaRepository.save(k);
        return new KategorijaDto(sacuvana.getId(), sacuvana.getNaziv());
    }

    @Transactional
    public KategorijaDto dodajPotkategoriju(Integer kategorijaId, String naziv) {
        Kategorija kategorija = kategorijaRepository.findById(kategorijaId)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Kategorija ne postoji."));

        String ocisceno = naziv.trim();
        boolean postoji = potkategorijaRepository.findByKategorijaIdOrderByNazivAsc(kategorijaId)
                .stream().anyMatch(p -> p.getNaziv().equalsIgnoreCase(ocisceno));
        if (postoji) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT,
                    "Ta kategorija već ima potkategoriju sa tim nazivom.");
        }

        Potkategorija p = new Potkategorija();
        p.setKategorija(kategorija);
        p.setNaziv(ocisceno);
        Potkategorija sacuvana = potkategorijaRepository.save(p);
        return new KategorijaDto(sacuvana.getId(), sacuvana.getNaziv());
    }

    private Korisnik nadji(String korIme) {
        return korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Nalog nije pronađen."));
    }
}
