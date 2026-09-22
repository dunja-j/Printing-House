package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.models.Narudzbina;

public interface NarudzbinaRepository extends JpaRepository<Narudzbina, Integer> {

    /**
     * DISTINCT je neophodan jer JOIN FETCH kolekcije stavki inace duplira
     * narudzbinu po broju stavki.
     */
    @Query("""
            SELECT DISTINCT n FROM Narudzbina n
            JOIN FETCH n.stampar
            LEFT JOIN FETCH n.stavke s
            LEFT JOIN FETCH s.proizvod
            LEFT JOIN FETCH s.usluga
            WHERE n.klijent.korIme = :korIme
            ORDER BY n.datumNarudzbine DESC
            """)
    List<Narudzbina> zaKlijenta(@Param("korIme") String korIme);

    /**
     * Narudžbine pristigle štampariji. Prikazuju se samo one od fizičkih lica —
     * porudžbine pravnih lica idu preko javne nabavke (videti FEATURES.md #18).
     */
    @Query("""
            SELECT DISTINCT n FROM Narudzbina n
            JOIN FETCH n.klijent k
            LEFT JOIN FETCH n.stavke s
            LEFT JOIN FETCH s.proizvod
            LEFT JOIN FETCH s.usluga
            WHERE n.stampar.korIme = :korIme
              AND k.tip = com.example.backend.models.TipKorisnika.klijent_fizicko
            ORDER BY n.datumNarudzbine DESC
            """)
    List<Narudzbina> zaStampara(@Param("korIme") String korIme);
}
