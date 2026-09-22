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
@Table(name = "usluga_stampe")
public class UslugaStampe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proizvod_id")
    private Proizvod proizvod;

    @Column(name = "id_usluge", length = 30)
    private String idUsluge;

    @Column(name = "tip_stampe", nullable = false, length = 150)
    private String tipStampe;

    @Column(name = "dodatna_cena_po_komadu", nullable = false)
    private BigDecimal dodatnaCenaPoKomadu;

    @Column(name = "max_sirina_mm")
    private Integer maxSirinaMm;

    @Column(name = "max_visina_mm")
    private Integer maxVisinaMm;

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

    public String getIdUsluge() {
        return idUsluge;
    }

    public void setIdUsluge(String idUsluge) {
        this.idUsluge = idUsluge;
    }

    public String getTipStampe() {
        return tipStampe;
    }

    public void setTipStampe(String tipStampe) {
        this.tipStampe = tipStampe;
    }

    public BigDecimal getDodatnaCenaPoKomadu() {
        return dodatnaCenaPoKomadu;
    }

    public void setDodatnaCenaPoKomadu(BigDecimal dodatnaCenaPoKomadu) {
        this.dodatnaCenaPoKomadu = dodatnaCenaPoKomadu;
    }

    public Integer getMaxSirinaMm() {
        return maxSirinaMm;
    }

    public void setMaxSirinaMm(Integer maxSirinaMm) {
        this.maxSirinaMm = maxSirinaMm;
    }

    public Integer getMaxVisinaMm() {
        return maxVisinaMm;
    }

    public void setMaxVisinaMm(Integer maxVisinaMm) {
        this.maxVisinaMm = maxVisinaMm;
    }
}
