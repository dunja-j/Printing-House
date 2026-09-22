package com.example.backend.dto;

import java.math.BigDecimal;

import com.example.backend.models.StavkaKorpe;

public class StavkaKorpeDto {

    private final Integer id;
    private final Integer proizvodId;
    private final String nazivProizvoda;
    private final String slikaUrl;
    private final String boja;
    private final Integer uslugaId;
    private final String tipStampe;
    private final String tekstZaStampu;
    private final int kolicina;
    private final int kolicinaNaLageru;
    private final BigDecimal jedinicnaCena;
    private final BigDecimal dodatnaCenaPoKomadu;
    private final BigDecimal cenaPoKomadu;
    private final BigDecimal ukupno;

    public StavkaKorpeDto(StavkaKorpe s) {
        this.id = s.getId();
        this.proizvodId = s.getProizvod().getId();
        this.nazivProizvoda = s.getProizvod().getNaziv();
        this.slikaUrl = s.getProizvod().getSlikaUrl();
        this.boja = s.getBoja();
        this.uslugaId = s.getUsluga() == null ? null : s.getUsluga().getId();
        this.tipStampe = s.getUsluga() == null ? null : s.getUsluga().getTipStampe();
        this.tekstZaStampu = s.getTekstZaStampu();
        this.kolicina = s.getKolicina();
        this.kolicinaNaLageru = s.getProizvod().getKolicinaNaLageru();
        this.jedinicnaCena = s.getProizvod().getJedinicnaCena();
        this.dodatnaCenaPoKomadu = s.getUsluga() == null
                ? BigDecimal.ZERO
                : s.getUsluga().getDodatnaCenaPoKomadu();
        this.cenaPoKomadu = this.jedinicnaCena.add(this.dodatnaCenaPoKomadu);
        this.ukupno = this.cenaPoKomadu.multiply(BigDecimal.valueOf(this.kolicina));
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

    public String getSlikaUrl() {
        return slikaUrl;
    }

    public String getBoja() {
        return boja;
    }

    public Integer getUslugaId() {
        return uslugaId;
    }

    public String getTipStampe() {
        return tipStampe;
    }

    public String getTekstZaStampu() {
        return tekstZaStampu;
    }

    public int getKolicina() {
        return kolicina;
    }

    public int getKolicinaNaLageru() {
        return kolicinaNaLageru;
    }

    public BigDecimal getJedinicnaCena() {
        return jedinicnaCena;
    }

    public BigDecimal getDodatnaCenaPoKomadu() {
        return dodatnaCenaPoKomadu;
    }

    public BigDecimal getCenaPoKomadu() {
        return cenaPoKomadu;
    }

    public BigDecimal getUkupno() {
        return ukupno;
    }
}
