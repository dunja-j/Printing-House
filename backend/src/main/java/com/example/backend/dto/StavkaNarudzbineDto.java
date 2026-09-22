package com.example.backend.dto;

import java.math.BigDecimal;

import com.example.backend.models.StavkaNarudzbine;

public class StavkaNarudzbineDto {

    private final Integer id;
    private final Integer proizvodId;
    private final String nazivProizvoda;
    private final String tipStampe;
    private final int kolicina;
    private final String boja;
    private final String tekstZaStampu;
    private final BigDecimal cenaStavke;

    public StavkaNarudzbineDto(StavkaNarudzbine s) {
        this.id = s.getId();
        this.proizvodId = s.getProizvod().getId();
        this.nazivProizvoda = s.getProizvod().getNaziv();
        this.tipStampe = s.getUsluga() == null ? null : s.getUsluga().getTipStampe();
        this.kolicina = s.getKolicina();
        this.boja = s.getBoja();
        this.tekstZaStampu = s.getTekstZaStampu();
        this.cenaStavke = s.getCenaStavke();
    }

    public Integer getId() {
        return id;
    }

    public Integer getProizvodId() {
        return proizvodId;
    }

    public String getNazivProizvoda() {
        return nazivProizvoda;
    }

    public String getTipStampe() {
        return tipStampe;
    }

    public int getKolicina() {
        return kolicina;
    }

    public String getBoja() {
        return boja;
    }

    public String getTekstZaStampu() {
        return tekstZaStampu;
    }

    public BigDecimal getCenaStavke() {
        return cenaStavke;
    }
}
