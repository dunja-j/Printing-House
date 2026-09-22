package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

/** Stavke korpe iz iste štamparije — od jedne grupe nastaje jedna narudžbina/faktura. */
public class GrupaKorpeDto {

    private final String stamparKorIme;
    private final String nazivStamparije;
    private final String grad;
    private final List<StavkaKorpeDto> stavke;
    private final BigDecimal iznos;

    public GrupaKorpeDto(String stamparKorIme, String nazivStamparije, String grad,
            List<StavkaKorpeDto> stavke) {
        this.stamparKorIme = stamparKorIme;
        this.nazivStamparije = nazivStamparije;
        this.grad = grad;
        this.stavke = stavke;
        this.iznos = stavke.stream()
                .map(StavkaKorpeDto::getUkupno)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    public List<StavkaKorpeDto> getStavke() {
        return stavke;
    }

    public BigDecimal getIznos() {
        return iznos;
    }
}
