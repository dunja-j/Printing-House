package com.example.backend.models;

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
@Table(name = "javna_nabavka")
public class JavnaNabavka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucija_kor_ime")
    private Korisnik institucija;

    @Column(name = "datum_objave", nullable = false)
    private LocalDateTime datumObjave;

    @Column(name = "rok_za_ponude", nullable = false)
    private LocalDateTime rokZaPonude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusNabavke status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "narudzbina_id")
    private Narudzbina narudzbina;

    @OneToMany(mappedBy = "nabavka", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<StavkaNabavke> stavke = new ArrayList<>();

    @OneToMany(mappedBy = "nabavka", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ukupnaCena ASC")
    private List<Ponuda> ponude = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Korisnik getInstitucija() {
        return institucija;
    }

    public void setInstitucija(Korisnik institucija) {
        this.institucija = institucija;
    }

    public LocalDateTime getDatumObjave() {
        return datumObjave;
    }

    public void setDatumObjave(LocalDateTime datumObjave) {
        this.datumObjave = datumObjave;
    }

    public LocalDateTime getRokZaPonude() {
        return rokZaPonude;
    }

    public void setRokZaPonude(LocalDateTime rokZaPonude) {
        this.rokZaPonude = rokZaPonude;
    }

    public StatusNabavke getStatus() {
        return status;
    }

    public void setStatus(StatusNabavke status) {
        this.status = status;
    }

    public Narudzbina getNarudzbina() {
        return narudzbina;
    }

    public void setNarudzbina(Narudzbina narudzbina) {
        this.narudzbina = narudzbina;
    }

    public List<StavkaNabavke> getStavke() {
        return stavke;
    }

    public void setStavke(List<StavkaNabavke> stavke) {
        this.stavke = stavke;
    }

    public List<Ponuda> getPonude() {
        return ponude;
    }

    public void setPonude(List<Ponuda> ponude) {
        this.ponude = ponude;
    }
}
