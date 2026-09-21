package com.example.backend.models;

import java.time.LocalDateTime;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "ocena_proizvoda")
public class OcenaProizvoda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proizvod_id")
    private Proizvod proizvod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "klijent_kor_ime")
    private Korisnik klijent;

    @Enumerated(EnumType.STRING)
    @Column(name = "vrednost", nullable = false)
    private VrednostOcene vrednost;

    @Column(name = "datum", nullable = false)
    private LocalDateTime datum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Proizvod getProizvod() {
        return proizvod;
    }

    public void setProizvod(Proizvod proizvod) {
        this.proizvod = proizvod;
    }

    public Korisnik getKlijent() {
        return klijent;
    }

    public void setKlijent(Korisnik klijent) {
        this.klijent = klijent;
    }

    public VrednostOcene getVrednost() {
        return vrednost;
    }

    public void setVrednost(VrednostOcene vrednost) {
        this.vrednost = vrednost;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }
}
