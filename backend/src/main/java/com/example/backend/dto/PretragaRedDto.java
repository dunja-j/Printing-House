package com.example.backend.dto;

import java.math.BigDecimal;

/** Jedan red u tabeli rezultata pretrage. */
public class PretragaRedDto {

    private final Integer id;
    private final String naziv;
    private final String nazivStamparije;
    private final String grad;
    private final String kategorija;
    private final BigDecimal jedinicnaCena;
    private final int kolicinaNaLageru;
    private final long brojLajkova;

    public PretragaRedDto(Integer id, String naziv, String nazivStamparije, String grad,
            String kategorija, BigDecimal jedinicnaCena, int kolicinaNaLageru, long brojLajkova) {
        this.id = id;
        this.naziv = naziv;
        this.nazivStamparije = nazivStamparije;
        this.grad = grad;
        this.kategorija = kategorija;
        this.jedinicnaCena = jedinicnaCena;
        this.kolicinaNaLageru = kolicinaNaLageru;
        this.brojLajkova = brojLajkova;
    }

    public Integer getId() {
        return id;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getNazivStamparije() {
        return nazivStamparije;
    }

    public String getGrad() {
        return grad;
    }

    public String getKategorija() {
        return kategorija;
    }

    public BigDecimal getJedinicnaCena() {
        return jedinicnaCena;
    }

    public int getKolicinaNaLageru() {
        return kolicinaNaLageru;
    }

    public long getBrojLajkova() {
        return brojLajkova;
    }
}
