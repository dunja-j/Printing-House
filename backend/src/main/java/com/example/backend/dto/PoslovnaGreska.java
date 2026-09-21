package com.example.backend.dto;

import org.springframework.http.HttpStatus;

/** Ocekivana poslovna greska — pretvara se u JSON {"poruka": "..."} sa datim statusom. */
public class PoslovnaGreska extends RuntimeException {

    private final HttpStatus status;

    public PoslovnaGreska(HttpStatus status, String poruka) {
        super(poruka);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
