package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NazivRequest {

    @NotBlank(message = "Naziv je obavezan.")
    @Size(max = 100, message = "Naziv sme imati najviše 100 karaktera.")
    private String naziv;

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
}
