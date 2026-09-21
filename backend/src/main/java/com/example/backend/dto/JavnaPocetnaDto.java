package com.example.backend.dto;

import java.util.List;

public class JavnaPocetnaDto {

    private final long brojStamparija;
    private final List<TopProizvodDto> topProizvodi;

    public JavnaPocetnaDto(long brojStamparija, List<TopProizvodDto> topProizvodi) {
        this.brojStamparija = brojStamparija;
        this.topProizvodi = topProizvodi;
    }

    public long getBrojStamparija() {
        return brojStamparija;
    }

    public List<TopProizvodDto> getTopProizvodi() {
        return topProizvodi;
    }
}
