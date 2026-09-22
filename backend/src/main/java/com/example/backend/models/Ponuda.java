package com.example.backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "ponuda")
public class Ponuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nabavka_id")
    private JavnaNabavka nabavka;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stampar_kor_ime")
    private Korisnik stampar;

    @Column(name = "ukupna_cena", nullable = false)
    private BigDecimal ukupnaCena;

    @Column(name = "datum", nullable = false)
    private LocalDateTime datum;

    @Column(name = "pobednicka", nullable = false)
    private boolean pobednicka;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JavnaNabavka getNabavka() {
        return nabavka;
    }

    public void setNabavka(JavnaNabavka nabavka) {
        this.nabavka = nabavka;
    }

    public Korisnik getStampar() {
        return stampar;
    }

    public void setStampar(Korisnik stampar) {
        this.stampar = stampar;
    }

    public BigDecimal getUkupnaCena() {
        return ukupnaCena;
    }

    public void setUkupnaCena(BigDecimal ukupnaCena) {
        this.ukupnaCena = ukupnaCena;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }

    public boolean isPobednicka() {
        return pobednicka;
    }

    public void setPobednicka(boolean pobednicka) {
        this.pobednicka = pobednicka;
    }
}
