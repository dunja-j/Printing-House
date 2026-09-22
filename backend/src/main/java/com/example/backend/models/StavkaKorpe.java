package com.example.backend.models;

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

/** Stavka e-korpe — pripremljena, ali jos nepotvrdjena narudzbina. */
@Entity
@Table(name = "stavka_korpe")
public class StavkaKorpe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "klijent_kor_ime")
    private Korisnik klijent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proizvod_id")
    private Proizvod proizvod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usluga_id")
    private UslugaStampe usluga;

    @Column(name = "kolicina", nullable = false)
    private int kolicina;

    @Column(name = "boja", length = 50)
    private String boja;

    @Column(name = "tekst_za_stampu", length = 200)
    private String tekstZaStampu;

    @Column(name = "slicica_za_stampu_url", length = 255)
    private String slicicaZaStampuUrl;

    @Column(name = "datum_dodavanja", nullable = false)
    private LocalDateTime datumDodavanja;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Korisnik getKlijent() {
        return klijent;
    }

    public void setKlijent(Korisnik klijent) {
        this.klijent = klijent;
    }

    public Proizvod getProizvod() {
        return proizvod;
    }

    public void setProizvod(Proizvod proizvod) {
        this.proizvod = proizvod;
    }

    public UslugaStampe getUsluga() {
        return usluga;
    }

    public void setUsluga(UslugaStampe usluga) {
        this.usluga = usluga;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public String getBoja() {
        return boja;
    }

    public void setBoja(String boja) {
        this.boja = boja;
    }

    public String getTekstZaStampu() {
        return tekstZaStampu;
    }

    public void setTekstZaStampu(String tekstZaStampu) {
        this.tekstZaStampu = tekstZaStampu;
    }

    public String getSlicicaZaStampuUrl() {
        return slicicaZaStampuUrl;
    }

    public void setSlicicaZaStampuUrl(String slicicaZaStampuUrl) {
        this.slicicaZaStampuUrl = slicicaZaStampuUrl;
    }

    public LocalDateTime getDatumDodavanja() {
        return datumDodavanja;
    }

    public void setDatumDodavanja(LocalDateTime datumDodavanja) {
        this.datumDodavanja = datumDodavanja;
    }
}
