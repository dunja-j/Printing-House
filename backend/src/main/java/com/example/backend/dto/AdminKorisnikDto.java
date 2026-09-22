package com.example.backend.dto;

import java.time.LocalDateTime;

import com.example.backend.models.Korisnik;
import com.example.backend.models.StatusRegistracije;
import com.example.backend.models.TipKorisnika;

/** Korisnik onako kako ga vidi administrator — uključuje i status registracije. */
public class AdminKorisnikDto {

    private final String korIme;
    private final String ime;
    private final String prezime;
    private final String mejl;
    private final String telefon;
    private final TipKorisnika tip;
    private final StatusRegistracije statusRegistracije;
    private final String slikaUrl;
    private final String nazivInstitucije;
    private final String adresaSedista;
    private final String grad;
    private final String maticniBroj;
    private final String pib;
    private final LocalDateTime datumRegistracije;

    public AdminKorisnikDto(Korisnik k) {
        this.korIme = k.getKorIme();
        this.ime = k.getIme();
        this.prezime = k.getPrezime();
        this.mejl = k.getMejl();
        this.telefon = k.getTelefon();
        this.tip = k.getTip();
        this.statusRegistracije = k.getStatusRegistracije();
        this.slikaUrl = k.getSlikaUrl();
        this.nazivInstitucije = k.getNazivInstitucije();
        this.adresaSedista = k.getAdresaSedista();
        this.grad = k.getGrad();
        this.maticniBroj = k.getMaticniBroj();
        this.pib = k.getPib();
        this.datumRegistracije = k.getDatumRegistracije();
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

    public StatusRegistracije getStatusRegistracije() {
        return statusRegistracije;
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

    public String getMaticniBroj() {
        return maticniBroj;
    }

    public String getPib() {
        return pib;
    }

    public LocalDateTime getDatumRegistracije() {
        return datumRegistracije;
    }

    public boolean isInstitucija() {
        return tip == TipKorisnika.klijent_pravno || tip == TipKorisnika.stampar;
    }
}
