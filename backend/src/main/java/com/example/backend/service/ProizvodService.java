package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.KomentarProizvodaRepository;
import com.example.backend.db.dao.OcenaProizvodaRepository;
import com.example.backend.db.dao.ProizvodRepository;
import com.example.backend.dto.DetaljiProizvodaDto;
import com.example.backend.dto.DetaljiZaKlijentaDto;
import com.example.backend.dto.KomentarDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.Proizvod;
import com.example.backend.models.VrednostOcene;

@Service
public class ProizvodService {

    private static final int BROJ_KOMENTARA = 5;

    private final ProizvodRepository proizvodRepository;
    private final OcenaProizvodaRepository ocenaRepository;
    private final KomentarProizvodaRepository komentarRepository;

    public ProizvodService(ProizvodRepository proizvodRepository,
            OcenaProizvodaRepository ocenaRepository,
            KomentarProizvodaRepository komentarRepository) {
        this.proizvodRepository = proizvodRepository;
        this.ocenaRepository = ocenaRepository;
        this.komentarRepository = komentarRepository;
    }

    /** Skraćeni prikaz, dostupan i neprijavljenom posetiocu. */
    @Transactional(readOnly = true)
    public DetaljiProizvodaDto detalji(Integer id) {
        Proizvod p = nadjiAktivan(id);
        return new DetaljiProizvodaDto(p, lajkovi(p), dislajkovi(p));
    }

    /** Prošireni prikaz za prijavljenog klijenta — sa bojama, uslugama štampe i komentarima. */
    @Transactional(readOnly = true)
    public DetaljiZaKlijentaDto detaljiZaKlijenta(Integer id, String korIme) {
        Proizvod p = nadjiAktivan(id);

        List<KomentarDto> komentari = komentarRepository
                .poslednjiZaProizvod(p.getId(), PageRequest.of(0, BROJ_KOMENTARA)).stream()
                .map(k -> new KomentarDto(k, korIme))
                .toList();

        return new DetaljiZaKlijentaDto(p, lajkovi(p), dislajkovi(p), komentari);
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
