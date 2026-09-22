package com.example.backend.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.models.JavnaNabavka;
import com.example.backend.models.Ponuda;
import com.example.backend.models.StatusNabavke;
import com.example.backend.models.StavkaNabavke;

/** Pogled institucije na sopstvenu nabavku. */
public class NabavkaDto {

    private final Integer id;
    private final LocalDateTime datumObjave;
    private final LocalDateTime rokZaPonude;
    private final StatusNabavke status;
    private final long preostaloSekundi;
    private final Integer narudzbinaId;
    private final int brojPonuda;
    private final List<StavkaNabavkeDto> stavke;
    private final List<PonudaDto> ponude;

    public NabavkaDto(JavnaNabavka n, List<Ponuda> ponude) {
        this.id = n.getId();
        this.datumObjave = n.getDatumObjave();
        this.rokZaPonude = n.getRokZaPonude();
        this.status = n.getStatus();
        this.preostaloSekundi = preostalo(n);
        this.narudzbinaId = n.getNarudzbina() == null ? null : n.getNarudzbina().getId();
        this.brojPonuda = ponude.size();
        this.stavke = n.getStavke().stream().map(StavkaNabavkeDto::new).toList();
        // dok licitacija traje, institucija vidi samo koliko je ponuda stiglo
        this.ponude = n.getStatus() == StatusNabavke.otvorena
                ? List.of()
                : ponude.stream().map(PonudaDto::new).toList();
    }

    static long preostalo(JavnaNabavka n) {
        if (n.getStatus() != StatusNabavke.otvorena) {
            return 0;
        }
        long sekundi = Duration.between(LocalDateTime.now(), n.getRokZaPonude()).getSeconds();
        return Math.max(sekundi, 0);
    }

    static List<StavkaNabavkeDto> stavke(List<StavkaNabavke> stavke) {
        return stavke.stream().map(StavkaNabavkeDto::new).toList();
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDatumObjave() {
        return datumObjave;
    }

    public LocalDateTime getRokZaPonude() {
        return rokZaPonude;
    }

    public StatusNabavke getStatus() {
        return status;
    }

    public long getPreostaloSekundi() {
        return preostaloSekundi;
    }

    public Integer getNarudzbinaId() {
        return narudzbinaId;
    }

    public int getBrojPonuda() {
        return brojPonuda;
    }

    public List<StavkaNabavkeDto> getStavke() {
        return stavke;
    }

    public List<PonudaDto> getPonude() {
        return ponude;
    }
}
