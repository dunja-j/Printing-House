package com.example.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class PonudaRequest {

    @NotNull(message = "Unesite ukupnu cenu ponude.")
    @DecimalMin(value = "0.01", message = "Ukupna cena mora biti veća od nule.")
    @DecimalMax(value = "99999999.99", message = "Ukupna cena je prevelika.")
    private BigDecimal ukupnaCena;

    public BigDecimal getUkupnaCena() {
        return ukupnaCena;
    }

    public void setUkupnaCena(BigDecimal ukupnaCena) {
        this.ukupnaCena = ukupnaCena;
    }
}
