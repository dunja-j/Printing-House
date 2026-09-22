package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NoviProizvodRequest {

    @NotBlank(message = "Šifra proizvoda je obavezna.")
    @Pattern(regexp = "^[A-Za-z0-9._-]{2,30}$",
            message = "Šifra sme da sadrži samo slova, cifre, tačku, donju crtu i crticu (2-30 karaktera).")
    private String sifra;

    @NotBlank(message = "Naziv proizvoda je obavezan.")
    @Size(max = 150, message = "Naziv sme imati najviše 150 karaktera.")
    private String naziv;

    @Size(max = 2000, message = "Opis sme imati najviše 2000 karaktera.")
    private String opis;

    @NotNull(message = "Kategorija je obavezna.")
    private Integer kategorijaId;

    private Integer potkategorijaId;

    @NotNull(message = "Jedinična cena je obavezna.")
    @DecimalMin(value = "0.01", message = "Jedinična cena mora biti veća od nule.")
    private BigDecimal jedinicnaCena;

    @NotNull(message = "Količina na lageru je obavezna.")
    @Min(value = 0, message = "Količina na lageru ne može biti negativna.")
    private Integer kolicinaNaLageru;

    private List<String> dostupneBoje;

    @Valid
    private List<NovaUslugaStampeRequest> uslugeStampe;

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Integer getKategorijaId() {
        return kategorijaId;
    }

    public void setKategorijaId(Integer kategorijaId) {
        this.kategorijaId = kategorijaId;
    }

    public Integer getPotkategorijaId() {
        return potkategorijaId;
    }

    public void setPotkategorijaId(Integer potkategorijaId) {
        this.potkategorijaId = potkategorijaId;
    }

    public BigDecimal getJedinicnaCena() {
        return jedinicnaCena;
    }

    public void setJedinicnaCena(BigDecimal jedinicnaCena) {
        this.jedinicnaCena = jedinicnaCena;
    }

    public Integer getKolicinaNaLageru() {
        return kolicinaNaLageru;
    }

    public void setKolicinaNaLageru(Integer kolicinaNaLageru) {
        this.kolicinaNaLageru = kolicinaNaLageru;
    }

    public List<String> getDostupneBoje() {
        return dostupneBoje;
    }

    public void setDostupneBoje(List<String> dostupneBoje) {
        this.dostupneBoje = dostupneBoje;
    }

    public List<NovaUslugaStampeRequest> getUslugeStampe() {
        return uslugeStampe;
    }

    public void setUslugeStampe(List<NovaUslugaStampeRequest> uslugeStampe) {
        this.uslugeStampe = uslugeStampe;
    }
}
