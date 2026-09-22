package com.example.backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "narudzbina")
public class Narudzbina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "klijent_kor_ime")
    private Korisnik klijent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stampar_kor_ime")
    private Korisnik stampar;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusNarudzbine status;

    @Column(name = "datum_narudzbine", nullable = false)
    private LocalDateTime datumNarudzbine;

    @Column(name = "ukupan_iznos", nullable = false)
    private BigDecimal ukupanIznos;

    @OneToMany(mappedBy = "narudzbina", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<StavkaNarudzbine> stavke = new ArrayList<>();

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

    public Korisnik getStampar() {
        return stampar;
    }

    public void setStampar(Korisnik stampar) {
        this.stampar = stampar;
    }

    public StatusNarudzbine getStatus() {
        return status;
    }

    public void setStatus(StatusNarudzbine status) {
        this.status = status;
    }

    public LocalDateTime getDatumNarudzbine() {
        return datumNarudzbine;
    }

    public void setDatumNarudzbine(LocalDateTime datumNarudzbine) {
        this.datumNarudzbine = datumNarudzbine;
    }

    public BigDecimal getUkupanIznos() {
        return ukupanIznos;
    }

    public void setUkupanIznos(BigDecimal ukupanIznos) {
        this.ukupanIznos = ukupanIznos;
    }

    public List<StavkaNarudzbine> getStavke() {
        return stavke;
    }

    public void setStavke(List<StavkaNarudzbine> stavke) {
        this.stavke = stavke;
    }
}
