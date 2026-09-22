package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.models.Narudzbina;
import com.example.backend.models.StatusNarudzbine;

public class NarudzbinaDto {

    private final Integer id;
    private final LocalDateTime datumNarudzbine;
    private final StatusNarudzbine status;
    private final String nazivStamparije;
    private final String gradStamparije;
    private final BigDecimal ukupanIznos;
    private final List<StavkaNarudzbineDto> stavke;

    public NarudzbinaDto(Narudzbina n) {
        this.id = n.getId();
        this.datumNarudzbine = n.getDatumNarudzbine();
        this.status = n.getStatus();
        this.nazivStamparije = n.getStampar().getNazivInstitucije();
        this.gradStamparije = n.getStampar().getGrad();
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

    public String getNazivStamparije() {
        return nazivStamparije;
    }

    public String getGradStamparije() {
        return gradStamparije;
    }

    public BigDecimal getUkupanIznos() {
        return ukupanIznos;
    }

    public List<StavkaNarudzbineDto> getStavke() {
        return stavke;
    }

    /** Otkazivanje je dozvoljeno samo dok stamparija nije preuzela posao. */
    public boolean isMozeOtkazati() {
        return status == StatusNarudzbine.naruceno;
    }

    /** Prijem potvrđuje klijent, i tek tada sme da oceni proizvode iz narudžbine. */
    public boolean isMozePotvrditiPrijem() {
        return status == StatusNarudzbine.isporuceno;
    }

    public int getBrojStavki() {
        return stavke.size();
    }
}
