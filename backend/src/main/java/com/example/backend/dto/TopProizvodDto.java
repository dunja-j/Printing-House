package com.example.backend.dto;

/** Jedan red u TOP 5 listi na javnoj početnoj strani. */
public class TopProizvodDto {

    private final Integer id;
    private final String naziv;
    private final String slikaUrl;
    private final String nazivStamparije;
    private final String grad;
    private final long brojLajkova;

    public TopProizvodDto(Integer id, String naziv, String slikaUrl, String nazivStamparije,
            String grad, long brojLajkova) {
        this.id = id;
        this.naziv = naziv;
        this.slikaUrl = slikaUrl;
        this.nazivStamparije = nazivStamparije;
        this.grad = grad;
        this.brojLajkova = brojLajkova;
    }

    public Integer getId() {
        return id;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getSlikaUrl() {
        return slikaUrl;
    }

    public String getNazivStamparije() {
        return nazivStamparije;
    }

    public String getGrad() {
        return grad;
    }

    public long getBrojLajkova() {
        return brojLajkova;
    }
}
