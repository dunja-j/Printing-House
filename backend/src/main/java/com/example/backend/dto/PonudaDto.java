package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.backend.models.Ponuda;

public class PonudaDto {

    private final Integer id;
    private final String stamparKorIme;
    private final String nazivStamparije;
    private final String grad;
    private final BigDecimal ukupnaCena;
    private final LocalDateTime datum;
    private final boolean pobednicka;

    public PonudaDto(Ponuda p) {
        this.id = p.getId();
        this.stamparKorIme = p.getStampar().getKorIme();
        this.nazivStamparije = p.getStampar().getNazivInstitucije();
        this.grad = p.getStampar().getGrad();
        this.ukupnaCena = p.getUkupnaCena();
        this.datum = p.getDatum();
        this.pobednicka = p.isPobednicka();
    }

    public Integer getId() {
        return id;
    }

    public String getStamparKorIme() {
        return stamparKorIme;
    }

    public String getNazivStamparije() {
        return nazivStamparije;
    }

    public String getGrad() {
        return grad;
    }

    public BigDecimal getUkupnaCena() {
        return ukupnaCena;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public boolean isPobednicka() {
        return pobednicka;
    }
}
