package com.example.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "korisnik")
public class Korisnik {

    @Id
    @Column(name = "kor_ime", length = 45)
    private String korIme;

    @Column(name = "lozinka_hash", nullable = false, length = 100)
    private String lozinkaHash;

    @Column(name = "ime", nullable = false, length = 45)
    private String ime;

    @Column(name = "prezime", nullable = false, length = 45)
    private String prezime;

    @Column(name = "telefon", length = 30)
    private String telefon;

    @Column(name = "mejl", nullable = false, length = 100)
    private String mejl;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip", nullable = false)
    private TipKorisnika tip;

    @Column(name = "slika_url", nullable = false, length = 255)
    private String slikaUrl;

    @Column(name = "naziv_institucije", length = 150)
    private String nazivInstitucije;

    @Column(name = "adresa_sedista", length = 200)
    private String adresaSedista;

    @Column(name = "grad", length = 100)
    private String grad;

    @Column(name = "maticni_broj", length = 8)
    private String maticniBroj;

    @Column(name = "pib", length = 9)
    private String pib;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_registracije", nullable = false)
    private StatusRegistracije statusRegistracije;

    @Column(name = "datum_registracije", nullable = false)
    private LocalDateTime datumRegistracije;

    /** Samo za stampare: broj narudzbina za koje je klijent prijavio da nisu stigle. */
    @Column(name = "broj_nedostavljenih", nullable = false)
    private int brojNedostavljenih;

    public String getKorIme() {
        return korIme;
    }

    public void setKorIme(String korIme) {
        this.korIme = korIme;
    }

    public String getLozinkaHash() {
        return lozinkaHash;
    }

    public void setLozinkaHash(String lozinkaHash) {
        this.lozinkaHash = lozinkaHash;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getMejl() {
        return mejl;
    }

    public void setMejl(String mejl) {
        this.mejl = mejl;
    }

    public TipKorisnika getTip() {
        return tip;
    }

    public void setTip(TipKorisnika tip) {
        this.tip = tip;
    }

    public String getSlikaUrl() {
        return slikaUrl;
    }

    public void setSlikaUrl(String slikaUrl) {
        this.slikaUrl = slikaUrl;
    }

    public String getNazivInstitucije() {
        return nazivInstitucije;
    }

    public void setNazivInstitucije(String nazivInstitucije) {
        this.nazivInstitucije = nazivInstitucije;
    }

    public String getAdresaSedista() {
        return adresaSedista;
    }

    public void setAdresaSedista(String adresaSedista) {
        this.adresaSedista = adresaSedista;
    }

    public String getGrad() {
        return grad;
    }

    public void setGrad(String grad) {
        this.grad = grad;
    }

    public String getMaticniBroj() {
        return maticniBroj;
    }

    public void setMaticniBroj(String maticniBroj) {
        this.maticniBroj = maticniBroj;
    }

    public String getPib() {
        return pib;
    }

    public void setPib(String pib) {
        this.pib = pib;
    }

    public StatusRegistracije getStatusRegistracije() {
        return statusRegistracije;
    }

    public void setStatusRegistracije(StatusRegistracije statusRegistracije) {
        this.statusRegistracije = statusRegistracije;
    }

    public LocalDateTime getDatumRegistracije() {
        return datumRegistracije;
    }

    public void setDatumRegistracije(LocalDateTime datumRegistracije) {
        this.datumRegistracije = datumRegistracije;
    }

    public int getBrojNedostavljenih() {
        return brojNedostavljenih;
    }

    public void setBrojNedostavljenih(int brojNedostavljenih) {
        this.brojNedostavljenih = brojNedostavljenih;
    }
}
