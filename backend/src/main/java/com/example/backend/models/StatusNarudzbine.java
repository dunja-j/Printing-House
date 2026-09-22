package com.example.backend.models;

/** Vrednosti kolone `narudzbina.status`. Tok: naruceno → [placeno] → u_stampi → isporuceno → primljeno. */
public enum StatusNarudzbine {
    naruceno,
    placeno,
    u_stampi,
    isporuceno,
    primljeno,
    otkazano
}
