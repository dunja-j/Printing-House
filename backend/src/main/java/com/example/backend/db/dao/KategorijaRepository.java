package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.dto.KategorijaDto;
import com.example.backend.models.Kategorija;

public interface KategorijaRepository extends JpaRepository<Kategorija, Integer> {

    /**
     * Samo kategorije u kojima trenutno postoji aktivan proizvod na stanju —
     * eksplicitan zahtev specifikacije za padajuće liste na javnim stranama.
     */
    @Query("""
            SELECT new com.example.backend.dto.KategorijaDto(k.id, k.naziv)
            FROM Kategorija k
            WHERE EXISTS (SELECT 1 FROM Proizvod p
                          WHERE p.kategorija = k AND p.aktivan = true AND p.kolicinaNaLageru > 0
                            AND p.stampar.brojNedostavljenih < 3)
            ORDER BY k.naziv ASC
            """)
    List<KategorijaDto> saDostupnimProizvodima();
}
