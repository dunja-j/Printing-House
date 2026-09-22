package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class KorpaDto {

    private final List<GrupaKorpeDto> grupe;
    private final BigDecimal ukupanIznos;
    private final int ukupnoStavki;
    private final boolean idePrekoJavneNabavke;

    public KorpaDto(List<GrupaKorpeDto> grupe, boolean idePrekoJavneNabavke) {
        this.grupe = grupe;
        this.ukupanIznos = grupe.stream()
                .map(GrupaKorpeDto::getIznos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.ukupnoStavki = grupe.stream().mapToInt(g -> g.getStavke().size()).sum();
        this.idePrekoJavneNabavke = idePrekoJavneNabavke;
    }

    public List<GrupaKorpeDto> getGrupe() {
        return grupe;
    }

    public BigDecimal getUkupanIznos() {
        return ukupanIznos;
    }

    public int getUkupnoStavki() {
        return ukupnoStavki;
    }

    /** Pravno lice ne zatvara narudžbinu direktno — ide na licitaciju (#18). */
    public boolean isIdePrekoJavneNabavke() {
        return idePrekoJavneNabavke;
    }
}
