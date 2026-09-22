package com.example.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NovaUslugaStampeRequest {

    @NotBlank(message = "Tip štampe je obavezan.")
    @Size(max = 150, message = "Tip štampe sme imati najviše 150 karaktera.")
    private String tipStampe;

    @NotNull(message = "Doplata za uslugu štampe je obavezna.")
    @DecimalMin(value = "0.00", message = "Doplata ne može biti negativna.")
    private BigDecimal dodatnaCenaPoKomadu;

    @Min(value = 1, message = "Maksimalna širina mora biti veća od nule.")
    private Integer maxSirinaMm;

    @Min(value = 1, message = "Maksimalna visina mora biti veća od nule.")
    private Integer maxVisinaMm;

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
