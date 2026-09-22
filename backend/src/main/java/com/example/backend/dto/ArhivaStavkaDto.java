package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.models.StatusNarudzbine;
import com.example.backend.models.StavkaNarudzbine;
import com.example.backend.models.VrednostOcene;

/**
 * Jedna stavka u arhivi klijenta — isporučen ili primljen proizvod, sa podacima
 * o narudžbini iz koje potiče. Ocenjivanje je dozvoljeno tek kad je primljen.
 */
public class ArhivaStavkaDto {

    private final Integer stavkaId;
    private final Integer narudzbinaId;
    private final LocalDateTime datumNarudzbine;
    private final StatusNarudzbine status;
    private final Integer proizvodId;
    private final String naziv;
    private final String slikaUrl;
    private final int kolicina;
    private final String boja;
    private final String tipStampe;
    private final String nazivStamparije;
    private final String grad;
    private final long brojLajkova;
    private final long brojDislajkova;
    private final VrednostOcene mojaOcena;
    private final List<KomentarDto> poslednjiKomentari;

    public ArhivaStavkaDto(StavkaNarudzbine s, long brojLajkova, long brojDislajkova,
            VrednostOcene mojaOcena, List<KomentarDto> poslednjiKomentari) {
        this.stavkaId = s.getId();
        this.narudzbinaId = s.getNarudzbina().getId();
        this.datumNarudzbine = s.getNarudzbina().getDatumNarudzbine();
        this.status = s.getNarudzbina().getStatus();
        this.proizvodId = s.getProizvod().getId();
        this.naziv = s.getProizvod().getNaziv();
        this.slikaUrl = s.getProizvod().getSlikaUrl();
        this.kolicina = s.getKolicina();
        this.boja = s.getBoja();
        this.tipStampe = s.getUsluga() == null ? null : s.getUsluga().getTipStampe();
        this.nazivStamparije = s.getNarudzbina().getStampar().getNazivInstitucije();
        this.grad = s.getNarudzbina().getStampar().getGrad();
        this.brojLajkova = brojLajkova;
        this.brojDislajkova = brojDislajkova;
        this.mojaOcena = mojaOcena;
        this.poslednjiKomentari = poslednjiKomentari;
    }

    public Integer getStavkaId() {
        return stavkaId;
    }

    public Integer getNarudzbinaId() {
        return narudzbinaId;
    }

    public LocalDateTime getDatumNarudzbine() {
        return datumNarudzbine;
    }

    public StatusNarudzbine getStatus() {
        return status;
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

    public int getKolicina() {
        return kolicina;
    }

    public String getBoja() {
        return boja;
    }

    public String getTipStampe() {
        return tipStampe;
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

    /** Isporučenu stavku klijent prvo mora da preuzme, pa tek onda sme da je oceni. */
    public boolean isMozePotvrditiPrijem() {
        return status == StatusNarudzbine.isporuceno;
    }

    public boolean isMozeOceniti() {
        return status == StatusNarudzbine.primljeno;
    }
}
