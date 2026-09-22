package com.example.backend.dto;

import java.math.BigDecimal;

import com.example.backend.models.UslugaStampe;

public class UslugaStampeDto {

    private final Integer id;
    private final String tipStampe;
    private final BigDecimal dodatnaCenaPoKomadu;
    private final Integer maxSirinaMm;
    private final Integer maxVisinaMm;

    public UslugaStampeDto(UslugaStampe u) {
        this.id = u.getId();
        this.tipStampe = u.getTipStampe();
        this.dodatnaCenaPoKomadu = u.getDodatnaCenaPoKomadu();
        this.maxSirinaMm = u.getMaxSirinaMm();
        this.maxVisinaMm = u.getMaxVisinaMm();
    }

    public Integer getId() {
        return id;
    }

    public String getTipStampe() {
        return tipStampe;
    }

    public BigDecimal getDodatnaCenaPoKomadu() {
        return dodatnaCenaPoKomadu;
    }

    public Integer getMaxSirinaMm() {
        return maxSirinaMm;
    }

    public Integer getMaxVisinaMm() {
        return maxVisinaMm;
    }
}
