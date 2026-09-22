package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.models.JavnaNabavka;

/** Pogled štamparije na otvorenu nabavku — bez tuđih ponuda. */
public class NabavkaZaStamparaDto {

    private final Integer id;
    private final String institucija;
    private final String grad;
    private final LocalDateTime datumObjave;
    private final LocalDateTime rokZaPonude;
    private final long preostaloSekundi;
    private final int brojPonuda;
    private final BigDecimal mojaPonuda;
    private final boolean mozeDaPonudi;
    private final String razlog;
    private final List<StavkaNabavkeDto> stavke;

    public NabavkaZaStamparaDto(JavnaNabavka n, int brojPonuda, BigDecimal mojaPonuda,
            boolean mozeDaPonudi, String razlog) {
        this.id = n.getId();
        this.institucija = n.getInstitucija().getNazivInstitucije();
        this.grad = n.getInstitucija().getGrad();
        this.datumObjave = n.getDatumObjave();
        this.rokZaPonude = n.getRokZaPonude();
        this.preostaloSekundi = NabavkaDto.preostalo(n);
        this.brojPonuda = brojPonuda;
        this.mojaPonuda = mojaPonuda;
        this.mozeDaPonudi = mozeDaPonudi;
        this.razlog = razlog;
        this.stavke = NabavkaDto.stavke(n.getStavke());
    }

    public Integer getId() {
        return id;
    }

    public String getInstitucija() {
        return institucija;
    }

    public String getGrad() {
        return grad;
    }

    public LocalDateTime getDatumObjave() {
        return datumObjave;
    }

    public LocalDateTime getRokZaPonude() {
        return rokZaPonude;
    }

    public long getPreostaloSekundi() {
        return preostaloSekundi;
    }

    public int getBrojPonuda() {
        return brojPonuda;
    }

    public BigDecimal getMojaPonuda() {
        return mojaPonuda;
    }

    public boolean isMozeDaPonudi() {
        return mozeDaPonudi;
    }

    public String getRazlog() {
        return razlog;
    }

    public List<StavkaNabavkeDto> getStavke() {
        return stavke;
    }
}
