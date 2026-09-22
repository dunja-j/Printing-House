package com.example.backend.dto;

import java.util.List;

import com.example.backend.models.Proizvod;
import com.example.backend.models.VrednostOcene;

/** Jedan primljeni proizvod u arhivi klijenta, sa ocenama i poslednjim komentarima. */
public class ArhivaProizvodDto {

    private final Integer proizvodId;
    private final String naziv;
    private final String slikaUrl;
    private final String nazivStamparije;
    private final String grad;
    private final long brojLajkova;
    private final long brojDislajkova;
    private final VrednostOcene mojaOcena;
    private final List<KomentarDto> poslednjiKomentari;

    public ArhivaProizvodDto(Proizvod p, long brojLajkova, long brojDislajkova,
            VrednostOcene mojaOcena, List<KomentarDto> poslednjiKomentari) {
        this.proizvodId = p.getId();
        this.naziv = p.getNaziv();
        this.slikaUrl = p.getSlikaUrl();
        this.nazivStamparije = p.getStampar().getNazivInstitucije();
        this.grad = p.getStampar().getGrad();
        this.brojLajkova = brojLajkova;
        this.brojDislajkova = brojDislajkova;
        this.mojaOcena = mojaOcena;
        this.poslednjiKomentari = poslednjiKomentari;
    }

    public Integer getProizvodId() {
        return proizvodId;
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

    public long getBrojDislajkova() {
        return brojDislajkova;
    }

    public VrednostOcene getMojaOcena() {
        return mojaOcena;
    }

    public List<KomentarDto> getPoslednjiKomentari() {
        return poslednjiKomentari;
    }
}
