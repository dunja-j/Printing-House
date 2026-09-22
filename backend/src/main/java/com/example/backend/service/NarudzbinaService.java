package com.example.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.db.dao.NarudzbinaRepository;
import com.example.backend.dto.NarudzbinaDto;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.Narudzbina;
import com.example.backend.models.StatusNarudzbine;

@Service
public class NarudzbinaService {

    private final NarudzbinaRepository narudzbinaRepository;

    public NarudzbinaService(NarudzbinaRepository narudzbinaRepository) {
        this.narudzbinaRepository = narudzbinaRepository;
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
}
