package com.example.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.OcenaProizvodaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.dto.DetaljiProizvodaDto;
import com.example.backend.dto.DetaljiZaKlijentaDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.Proizvod;
import com.example.backend.models.VrednostOcene;

@Service
public class ProizvodService {

    private final ProizvodRepository proizvodRepository;
    private final OcenaProizvodaRepository ocenaRepository;

    public ProizvodService(ProizvodRepository proizvodRepository,
            OcenaProizvodaRepository ocenaRepository) {
        this.proizvodRepository = proizvodRepository;
        this.ocenaRepository = ocenaRepository;
    }

    /** Skraćeni prikaz, dostupan i neprijavljenom posetiocu. */
    @Transactional(readOnly = true)
    public DetaljiProizvodaDto detalji(Integer id) {
        Proizvod p = nadjiAktivan(id);
        return new DetaljiProizvodaDto(p, lajkovi(p), dislajkovi(p));
    }

    /** Prošireni prikaz za prijavljenog klijenta — sa bojama i uslugama štampe. */
    @Transactional(readOnly = true)
    public DetaljiZaKlijentaDto detaljiZaKlijenta(Integer id) {
        Proizvod p = nadjiAktivan(id);
        return new DetaljiZaKlijentaDto(p, lajkovi(p), dislajkovi(p));
    }

    private Proizvod nadjiAktivan(Integer id) {
        return proizvodRepository.nadjiAktivanSaDetaljima(id)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Proizvod nije pronađen."));
    }

    private long lajkovi(Proizvod p) {
        return ocenaRepository.countByProizvodIdAndVrednost(p.getId(), VrednostOcene.lajk);
    }

    private long dislajkovi(Proizvod p) {
        return ocenaRepository.countByProizvodIdAndVrednost(p.getId(), VrednostOcene.dislajk);
    }
}
