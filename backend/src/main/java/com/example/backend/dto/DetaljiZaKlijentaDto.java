package com.example.backend.dto;

import java.util.List;

import com.example.backend.models.Proizvod;

/** Detalji koje vidi prijavljeni klijent — dodatno boje i usluge štampe, potrebne za poručivanje. */
public class DetaljiZaKlijentaDto extends DetaljiProizvodaDto {

    private final List<String> dostupneBoje;
    private final List<UslugaStampeDto> uslugeStampe;

    public DetaljiZaKlijentaDto(Proizvod p, long brojLajkova, long brojDislajkova) {
        super(p, brojLajkova, brojDislajkova);
        this.dostupneBoje = List.copyOf(p.getDostupneBoje());
        this.uslugeStampe = p.getUslugeStampe().stream().map(UslugaStampeDto::new).toList();
    }

    public List<String> getDostupneBoje() {
        return dostupneBoje;
    }

    public List<UslugaStampeDto> getUslugeStampe() {
        return uslugeStampe;
    }
}
