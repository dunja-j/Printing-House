package com.example.backend.models;

/**
 * Vrednosti kolone `narudzbina.status`. Tok: naruceno → [placeno] → u_stampi →
 * isporuceno → primljeno. Umesto potvrde prijema klijent sme da prijavi da
 * narudzbina nije stigla (nije_stiglo).
 */
public enum StatusNarudzbine {
    naruceno,
    placeno,
    u_stampi,
    isporuceno,
    primljeno,
    nije_stiglo,
    otkazano
}
