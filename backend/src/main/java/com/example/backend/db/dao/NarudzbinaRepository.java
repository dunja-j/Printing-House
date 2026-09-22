package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.models.Narudzbina;
import com.example.backend.models.StavkaNarudzbine;

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
     * Narudžbine pristigle štampariji — i one od fizičkih lica i one nastale od
     * pobedničke ponude na javnoj nabavci pravnog lica (videti FEATURES.md #18).
     */
    @Query("""
            SELECT DISTINCT n FROM Narudzbina n
            JOIN FETCH n.klijent k
            LEFT JOIN FETCH n.stavke s
            LEFT JOIN FETCH s.proizvod
            LEFT JOIN FETCH s.usluga
            WHERE n.stampar.korIme = :korIme
            ORDER BY n.datumNarudzbine DESC
            """)
    List<Narudzbina> zaStampara(@Param("korIme") String korIme);

    /**
     * Arhiva klijenta: isporučene i primljene stavke, podrazumevano sortirane po
     * datumu naručivanja (specifikacija, odeljak "Arhiva proizvoda").
     */
    @Query("""
            SELECT s FROM StavkaNarudzbine s
            JOIN FETCH s.narudzbina n
            JOIN FETCH n.stampar
            JOIN FETCH s.proizvod
            LEFT JOIN FETCH s.usluga
            WHERE n.klijent.korIme = :korIme
              AND n.status IN (com.example.backend.models.StatusNarudzbine.isporuceno,
                               com.example.backend.models.StatusNarudzbine.primljeno)
            ORDER BY n.datumNarudzbine DESC, s.id ASC
            """)
    List<StavkaNarudzbine> arhivaKlijenta(@Param("korIme") String korIme);
}
