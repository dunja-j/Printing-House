package com.example.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DodajUKorpuRequest {

    @NotNull(message = "Proizvod je obavezan.")
    private Integer proizvodId;

    private Integer uslugaId;

    @NotNull(message = "Količina je obavezna.")
    @Min(value = 1, message = "Količina mora biti najmanje 1.")
    @Max(value = 100000, message = "Količina je prevelika.")
    private Integer kolicina;

    private String boja;

    @Size(max = 200, message = "Tekst za štampu sme imati najviše 200 karaktera.")
    private String tekstZaStampu;

    public Integer getProizvodId() {
        return proizvodId;
    }

    public void setProizvodId(Integer proizvodId) {
        this.proizvodId = proizvodId;
    }

    public Integer getUslugaId() {
        return uslugaId;
    }

    public void setUslugaId(Integer uslugaId) {
        this.uslugaId = uslugaId;
    }

    public Integer getKolicina() {
        return kolicina;
    }

    public void setKolicina(Integer kolicina) {
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
}
