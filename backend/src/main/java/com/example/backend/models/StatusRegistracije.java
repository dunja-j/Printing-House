package com.example.backend.models;

/** Status zahteva za registraciju; mapira se na ENUM kolonu `korisnik.status_registracije`. */
public enum StatusRegistracije {
    na_cekanju,
    odobren,
    odbijen
}
