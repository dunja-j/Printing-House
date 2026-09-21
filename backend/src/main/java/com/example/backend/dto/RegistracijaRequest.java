package com.example.backend.dto;

import com.example.backend.models.TipKorisnika;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Polja za institucije (naziv, adresa, grad, matični broj, PIB) su ovde bez
 * anotacija jer su obavezna samo za pravna lica i štamparije — ta provera se
 * radi u RegistracijaService.
 */
public class RegistracijaRequest {

    public static final String REGEX_LOZINKE = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z]\\S{7,11}$";

    @NotBlank(message = "Korisničko ime je obavezno.")
    @Pattern(regexp = "^[A-Za-z0-9._-]{3,45}$",
            message = "Korisničko ime sme da sadrži samo slova, cifre, tačku, donju crtu i crticu (3-45 karaktera).")
    private String korIme;

    @NotBlank(message = "Lozinka je obavezna.")
    @Pattern(regexp = REGEX_LOZINKE,
            message = "Lozinka mora imati 8-12 karaktera, počinjati slovom i sadržati bar jedno veliko slovo, jednu cifru i jedan specijalni karakter.")
    private String lozinka;

    @NotBlank(message = "Ime je obavezno.")
    @Size(max = 45, message = "Ime sme imati najviše 45 karaktera.")
    private String ime;

    @NotBlank(message = "Prezime je obavezno.")
    @Size(max = 45, message = "Prezime sme imati najviše 45 karaktera.")
    private String prezime;

    @NotBlank(message = "Kontakt telefon je obavezan.")
    @Pattern(regexp = "^[0-9+\\s\\-/()]{6,30}$", message = "Kontakt telefon nije u ispravnom formatu.")
    private String telefon;

    @NotBlank(message = "Mejl adresa je obavezna.")
    @Email(message = "Mejl adresa nije u ispravnom formatu.")
    @Size(max = 100, message = "Mejl adresa sme imati najviše 100 karaktera.")
    private String mejl;

    @NotNull(message = "Tip naloga je obavezan.")
    private TipKorisnika tip;

    private String nazivInstitucije;
    private String adresaSedista;
    private String grad;
    private String maticniBroj;
    private String pib;

    public String getKorIme() {
        return korIme;
    }

    public void setKorIme(String korIme) {
        this.korIme = korIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
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
}
