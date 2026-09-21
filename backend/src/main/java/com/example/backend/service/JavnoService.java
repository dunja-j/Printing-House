package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.dto.JavnaPocetnaDto;
import com.example.backend.dto.TopProizvodDto;
import com.example.backend.models.StatusRegistracije;
import com.example.backend.models.TipKorisnika;

/** Podaci za strane koje vidi i neprijavljeni korisnik. */
@Service
public class JavnoService {

    private static final int VELICINA_TOP_LISTE = 5;

    private final KorisnikRepository korisnikRepository;
    private final ProizvodRepository proizvodRepository;

    public JavnoService(KorisnikRepository korisnikRepository, ProizvodRepository proizvodRepository) {
        this.korisnikRepository = korisnikRepository;
        this.proizvodRepository = proizvodRepository;
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
}
