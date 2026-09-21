package com.example.backend.dto;

import com.example.backend.models.Korisnik;
import com.example.backend.models.TipKorisnika;

/** Korisnik onako kako se salje frontu — bez hash-a lozinke. */
public class KorisnikDto {

    private String korIme;
    private String ime;
    private String prezime;
    private String mejl;
    private String telefon;
    private TipKorisnika tip;
    private String slikaUrl;
    private String nazivInstitucije;
    private String adresaSedista;
    private String grad;

    public static KorisnikDto od(Korisnik k) {
        KorisnikDto dto = new KorisnikDto();
        dto.korIme = k.getKorIme();
        dto.ime = k.getIme();
        dto.prezime = k.getPrezime();
        dto.mejl = k.getMejl();
        dto.telefon = k.getTelefon();
        dto.tip = k.getTip();
        dto.slikaUrl = k.getSlikaUrl();
        dto.nazivInstitucije = k.getNazivInstitucije();
        dto.adresaSedista = k.getAdresaSedista();
        dto.grad = k.getGrad();
        return dto;
    }

    public String getKorIme() {
        return korIme;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getMejl() {
        return mejl;
    }

    public String getTelefon() {
        return telefon;
    }

    public TipKorisnika getTip() {
        return tip;
    }

    public String getSlikaUrl() {
        return slikaUrl;
    }

    public String getNazivInstitucije() {
        return nazivInstitucije;
    }

    public String getAdresaSedista() {
        return adresaSedista;
    }

    public String getGrad() {
        return grad;
    }
}
