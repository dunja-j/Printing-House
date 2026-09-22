package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Šta institucija traži — bez veze sa konkretnim proizvodom neke štamparije. */
@Entity
@Table(name = "stavka_nabavke")
public class StavkaNabavke {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nabavka_id")
    private JavnaNabavka nabavka;

    @Column(name = "naziv_proizvoda", nullable = false, length = 150)
    private String nazivProizvoda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kategorija_id")
    private Kategorija kategorija;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potkategorija_id")
    private Potkategorija potkategorija;

    @Column(name = "kolicina", nullable = false)
    private int kolicina;

    @Column(name = "boja", length = 50)
    private String boja;

    @Column(name = "tip_stampe", length = 150)
    private String tipStampe;

    @Column(name = "tekst_za_stampu", length = 200)
    private String tekstZaStampu;

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

    public String getNazivProizvoda() {
        return nazivProizvoda;
    }

    public void setNazivProizvoda(String nazivProizvoda) {
        this.nazivProizvoda = nazivProizvoda;
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

    public String getTipStampe() {
        return tipStampe;
    }

    public void setTipStampe(String tipStampe) {
        this.tipStampe = tipStampe;
    }

    public String getTekstZaStampu() {
        return tekstZaStampu;
    }

    public void setTekstZaStampu(String tekstZaStampu) {
        this.tekstZaStampu = tekstZaStampu;
    }
}
