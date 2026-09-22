package com.example.backend.dto;

import java.math.BigDecimal;

import com.example.backend.models.Proizvod;

/** Detalji proizvoda onako kako ih vidi i neprijavljeni korisnik. */
public class DetaljiProizvodaDto {

    private final Integer id;
    private final String sifra;
    private final String naziv;
    private final String opis;
    private final String kategorija;
    private final String potkategorija;
    private final BigDecimal jedinicnaCena;
    private final int kolicinaNaLageru;
    private final String slikaUrl;
    private final String nazivStamparije;
    private final String grad;
    private final String adresaSedista;
    private final long brojLajkova;
    private final long brojDislajkova;

    public DetaljiProizvodaDto(Proizvod p, long brojLajkova, long brojDislajkova) {
        this.id = p.getId();
        this.sifra = p.getSifra();
        this.naziv = p.getNaziv();
        this.opis = p.getOpis();
        this.kategorija = p.getKategorija().getNaziv();
        this.potkategorija = p.getPotkategorija() == null ? null : p.getPotkategorija().getNaziv();
        this.jedinicnaCena = p.getJedinicnaCena();
        this.kolicinaNaLageru = p.getKolicinaNaLageru();
        this.slikaUrl = p.getSlikaUrl();
        this.nazivStamparije = p.getStampar().getNazivInstitucije();
        this.grad = p.getStampar().getGrad();
        this.adresaSedista = p.getStampar().getAdresaSedista();
        this.brojLajkova = brojLajkova;
        this.brojDislajkova = brojDislajkova;
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

    public String getNazivStamparije() {
        return nazivStamparije;
    }

    public String getGrad() {
        return grad;
    }

    public String getAdresaSedista() {
        return adresaSedista;
    }

    public long getBrojLajkova() {
        return brojLajkova;
    }

    public long getBrojDislajkova() {
        return brojDislajkova;
    }
}
