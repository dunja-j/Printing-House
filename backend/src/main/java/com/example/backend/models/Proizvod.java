package com.example.backend.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "proizvod")
public class Proizvod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sifra", nullable = false, length = 30)
    private String sifra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stampar_kor_ime")
    private Korisnik stampar;

    @Column(name = "naziv", nullable = false, length = 150)
    private String naziv;

    @Column(name = "opis")
    private String opis;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kategorija_id")
    private Kategorija kategorija;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potkategorija_id")
    private Potkategorija potkategorija;

    @Column(name = "jedinicna_cena", nullable = false)
    private BigDecimal jedinicnaCena;

    @Column(name = "kolicina_na_lageru", nullable = false)
    private int kolicinaNaLageru;

    @Column(name = "slika_url", length = 255)
    private String slikaUrl;

    @Column(name = "aktivan", nullable = false)
    private boolean aktivan;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public Korisnik getStampar() {
        return stampar;
    }

    public void setStampar(Korisnik stampar) {
        this.stampar = stampar;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public Potkategorija getPotkategorija() {
        return potkategorija;
    }

    public void setPotkategorija(Potkategorija potkategorija) {
        this.potkategorija = potkategorija;
    }

    public BigDecimal getJedinicnaCena() {
        return jedinicnaCena;
    }

    public void setJedinicnaCena(BigDecimal jedinicnaCena) {
        this.jedinicnaCena = jedinicnaCena;
    }

    public int getKolicinaNaLageru() {
        return kolicinaNaLageru;
    }

    public void setKolicinaNaLageru(int kolicinaNaLageru) {
        this.kolicinaNaLageru = kolicinaNaLageru;
    }

    public String getSlikaUrl() {
        return slikaUrl;
    }

    public void setSlikaUrl(String slikaUrl) {
        this.slikaUrl = slikaUrl;
    }

    public boolean isAktivan() {
        return aktivan;
    }

    public void setAktivan(boolean aktivan) {
        this.aktivan = aktivan;
    }
}
