package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.backend.models.Proizvod;

/** Proizvod onako kako ga vidi štampar u svom pregledu. */
public class StamparProizvodDto {

    private final Integer id;
    private final String sifra;
    private final String naziv;
    private final String opis;
    private final String kategorija;
    private final String potkategorija;
    private final BigDecimal jedinicnaCena;
    private final int kolicinaNaLageru;
    private final String slikaUrl;
    private final boolean aktivan;
    private final List<String> dostupneBoje;
    private final List<UslugaStampeDto> uslugeStampe;

    public StamparProizvodDto(Proizvod p) {
        this.id = p.getId();
        this.sifra = p.getSifra();
        this.naziv = p.getNaziv();
        this.opis = p.getOpis();
        this.kategorija = p.getKategorija().getNaziv();
        this.potkategorija = p.getPotkategorija() == null ? null : p.getPotkategorija().getNaziv();
        this.jedinicnaCena = p.getJedinicnaCena();
        this.kolicinaNaLageru = p.getKolicinaNaLageru();
        this.slikaUrl = p.getSlikaUrl();
        this.aktivan = p.isAktivan();
        this.dostupneBoje = List.copyOf(p.getDostupneBoje());
        this.uslugeStampe = p.getUslugeStampe().stream().map(UslugaStampeDto::new).toList();
    }

    public Integer getId() {
        return id;
    }

    public String getSifra() {
        return sifra;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getOpis() {
        return opis;
    }

    public String getKategorija() {
        return kategorija;
    }

    public String getPotkategorija() {
        return potkategorija;
    }

    public BigDecimal getJedinicnaCena() {
        return jedinicnaCena;
    }

    public int getKolicinaNaLageru() {
        return kolicinaNaLageru;
    }

    public String getSlikaUrl() {
        return slikaUrl;
    }

    public boolean isAktivan() {
        return aktivan;
    }

    public List<String> getDostupneBoje() {
        return dostupneBoje;
    }

    public List<UslugaStampeDto> getUslugeStampe() {
        return uslugeStampe;
    }
}
