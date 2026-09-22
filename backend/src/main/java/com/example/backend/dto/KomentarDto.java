package com.example.backend.dto;

import java.time.LocalDateTime;

import com.example.backend.models.KomentarProizvoda;

public class KomentarDto {

    private final Integer id;
    private final String korIme;
    private final String autor;
    private final String tekst;
    private final LocalDateTime datum;
    private final boolean moj;

    public KomentarDto(KomentarProizvoda k, String prijavljeniKorIme) {
        this.id = k.getId();
        this.korIme = k.getKlijent().getKorIme();
        this.autor = k.getKlijent().getIme() + " " + k.getKlijent().getPrezime();
        this.tekst = k.getTekst();
        this.datum = k.getDatum();
        this.moj = k.getKlijent().getKorIme().equals(prijavljeniKorIme);
    }

    public Integer getId() {
        return id;
    }

    public String getKorIme() {
        return korIme;
    }

    public String getAutor() {
        return autor;
    }

    public String getTekst() {
        return tekst;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    /** Sopstveni komentar front uokviruje narandžastom linijom (zahtev specifikacije). */
    public boolean isMoj() {
        return moj;
    }
}
