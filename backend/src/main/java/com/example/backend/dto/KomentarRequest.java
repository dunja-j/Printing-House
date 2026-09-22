package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class KomentarRequest {

    @NotBlank(message = "Komentar ne sme biti prazan.")
    @Size(max = 500, message = "Komentar sme imati najviše 500 karaktera.")
    private String tekst;

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
}
