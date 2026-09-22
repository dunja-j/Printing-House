package com.example.backend.dto;

import com.example.backend.models.StavkaNabavke;

public class StavkaNabavkeDto {

    private final Integer id;
    private final String nazivProizvoda;
    private final String kategorija;
    private final String potkategorija;
    private final int kolicina;
    private final String boja;
    private final String tipStampe;
    private final String tekstZaStampu;

    public StavkaNabavkeDto(StavkaNabavke s) {
        this.id = s.getId();
        this.nazivProizvoda = s.getNazivProizvoda();
        this.kategorija = s.getKategorija().getNaziv();
        this.potkategorija = s.getPotkategorija() == null ? null : s.getPotkategorija().getNaziv();
        this.kolicina = s.getKolicina();
        this.boja = s.getBoja();
        this.tipStampe = s.getTipStampe();
        this.tekstZaStampu = s.getTekstZaStampu();
    }

    public Integer getId() {
        return id;
    }

    public String getNazivProizvoda() {
        return nazivProizvoda;
    }

    public String getKategorija() {
        return kategorija;
    }

    public String getPotkategorija() {
        return potkategorija;
    }

    public int getKolicina() {
        return kolicina;
    }

    public String getBoja() {
        return boja;
    }

    public String getTipStampe() {
        return tipStampe;
    }

    public String getTekstZaStampu() {
        return tekstZaStampu;
    }
}
