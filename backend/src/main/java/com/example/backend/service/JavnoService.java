package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KategorijaRepository;
import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.OcenaProizvodaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.dto.DetaljiProizvodaDto;
import com.example.backend.dto.JavnaPocetnaDto;
import com.example.backend.dto.KategorijaDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.dto.PretragaRedDto;
import com.example.backend.dto.TopProizvodDto;
import com.example.backend.models.StatusRegistracije;
import com.example.backend.models.TipKorisnika;
import com.example.backend.models.VrednostOcene;

/** Podaci za strane koje vidi i neprijavljeni korisnik. */
@Service
public class JavnoService {

    private static final int VELICINA_TOP_LISTE = 5;

    private final KorisnikRepository korisnikRepository;
    private final ProizvodRepository proizvodRepository;
    private final KategorijaRepository kategorijaRepository;
    private final OcenaProizvodaRepository ocenaRepository;

    public JavnoService(KorisnikRepository korisnikRepository, ProizvodRepository proizvodRepository,
            KategorijaRepository kategorijaRepository, OcenaProizvodaRepository ocenaRepository) {
        this.korisnikRepository = korisnikRepository;
        this.proizvodRepository = proizvodRepository;
        this.kategorijaRepository = kategorijaRepository;
        this.ocenaRepository = ocenaRepository;
    }

    @Transactional(readOnly = true)
    public JavnaPocetnaDto pocetna() {
        // broje se samo odobrene stamparije — one na cekanju jos nisu deo platforme
        long brojStamparija = korisnikRepository.countByTipAndStatusRegistracije(
                TipKorisnika.stampar, StatusRegistracije.odobren);

        List<TopProizvodDto> top = proizvodRepository
                .najboljeOceneni(PageRequest.of(0, VELICINA_TOP_LISTE));

        return new JavnaPocetnaDto(brojStamparija, top);
    }

    @Transactional(readOnly = true)
    public List<KategorijaDto> kategorije() {
        return kategorijaRepository.saDostupnimProizvodima();
    }

    @Transactional(readOnly = true)
    public List<PretragaRedDto> pretraga(String naziv, Integer kategorijaId) {
        String trazeniNaziv = (naziv == null || naziv.isBlank()) ? null : naziv.trim();
        return proizvodRepository.pretrazi(trazeniNaziv, kategorijaId);
    }

    @Transactional(readOnly = true)
    public DetaljiProizvodaDto detalji(Integer id) {
        return proizvodRepository.nadjiAktivanSaDetaljima(id)
                .map(p -> new DetaljiProizvodaDto(p,
                        ocenaRepository.countByProizvodIdAndVrednost(p.getId(), VrednostOcene.lajk),
                        ocenaRepository.countByProizvodIdAndVrednost(p.getId(), VrednostOcene.dislajk)))
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Proizvod nije pronađen."));
    }
}
