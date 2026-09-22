package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.models.Narudzbina;
import com.example.backend.models.StatusNarudzbine;

/** Narudžbina onako kako je vidi štamparija — sa podacima o naručiocu. */
public class StamparNarudzbinaDto {

    private final Integer id;
    private final LocalDateTime datumNarudzbine;
    private final StatusNarudzbine status;
    private final String klijentKorIme;
    private final String imeKlijenta;
    private final String telefonKlijenta;
    private final String gradKlijenta;
    private final BigDecimal ukupanIznos;
    private final List<StavkaNarudzbineDto> stavke;

    public StamparNarudzbinaDto(Narudzbina n) {
        this.id = n.getId();
        this.datumNarudzbine = n.getDatumNarudzbine();
        this.status = n.getStatus();
        this.klijentKorIme = n.getKlijent().getKorIme();
        this.imeKlijenta = n.getKlijent().getIme() + " " + n.getKlijent().getPrezime();
        this.telefonKlijenta = n.getKlijent().getTelefon();
        this.gradKlijenta = n.getKlijent().getGrad();
        this.ukupanIznos = n.getUkupanIznos();
        this.stavke = n.getStavke().stream().map(StavkaNarudzbineDto::new).toList();
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDatumNarudzbine() {
        return datumNarudzbine;
    }

    public StatusNarudzbine getStatus() {
        return status;
    }

    public String getKlijentKorIme() {
        return klijentKorIme;
    }

    public String getImeKlijenta() {
        return imeKlijenta;
    }

    public String getTelefonKlijenta() {
        return telefonKlijenta;
    }

    public String getGradKlijenta() {
        return gradKlijenta;
    }

    public BigDecimal getUkupanIznos() {
        return ukupanIznos;
    }

    public List<StavkaNarudzbineDto> getStavke() {
        return stavke;
    }

    public int getBrojStavki() {
        return stavke.size();
    }

    /** Sledeći status koji štamparija sme da postavi, ili null ako ga nema. */
    public StatusNarudzbine getSledeciStatus() {
        return switch (status) {
            case naruceno, placeno -> StatusNarudzbine.u_stampi;
            case u_stampi -> StatusNarudzbine.isporuceno;
            default -> null;
        };
    }
}
