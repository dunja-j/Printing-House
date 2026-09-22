package com.example.backend.dto;

import java.util.List;

import com.example.backend.models.Proizvod;

/** Detalji koje vidi prijavljeni klijent — dodatno boje i usluge štampe, potrebne za poručivanje. */
public class DetaljiZaKlijentaDto extends DetaljiProizvodaDto {

    private final List<String> dostupneBoje;
    private final List<UslugaStampeDto> uslugeStampe;
    private final List<KomentarDto> poslednjiKomentari;

    public DetaljiZaKlijentaDto(Proizvod p, long brojLajkova, long brojDislajkova,
            List<KomentarDto> poslednjiKomentari) {
        super(p, brojLajkova, brojDislajkova);
        this.dostupneBoje = List.copyOf(p.getDostupneBoje());
        this.uslugeStampe = p.getUslugeStampe().stream().map(UslugaStampeDto::new).toList();
        this.poslednjiKomentari = poslednjiKomentari;
    }

    public List<String> getDostupneBoje() {
        return dostupneBoje;
    }

    public List<UslugaStampeDto> getUslugeStampe() {
        return uslugeStampe;
    }

    public List<KomentarDto> getPoslednjiKomentari() {
        return poslednjiKomentari;
    }
}
