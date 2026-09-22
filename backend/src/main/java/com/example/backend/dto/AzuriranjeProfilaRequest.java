package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Korisničko ime i tip naloga se ne mogu menjati. Polja institucije su bez
 * anotacija jer su obavezna samo za pravna lica i štamparije — proverava ih
 * ProfilService.
 */
public class AzuriranjeProfilaRequest {

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

    private String grad;
    private String nazivInstitucije;
    private String adresaSedista;
    private String maticniBroj;
    private String pib;

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

    public String getGrad() {
        return grad;
    }

    public void setGrad(String grad) {
        this.grad = grad;
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
