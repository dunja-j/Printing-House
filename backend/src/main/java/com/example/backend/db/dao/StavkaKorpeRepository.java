package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.models.StavkaKorpe;

public interface StavkaKorpeRepository extends JpaRepository<StavkaKorpe, Integer> {

    @Query("""
            SELECT s FROM StavkaKorpe s
            JOIN FETCH s.proizvod p
            JOIN FETCH p.stampar
            LEFT JOIN FETCH s.usluga
            WHERE s.klijent.korIme = :korIme
            ORDER BY p.stampar.korIme ASC, s.id ASC
            """)
    List<StavkaKorpe> zaKlijenta(@Param("korIme") String korIme);

    void deleteByKlijentKorIme(String korIme);
}
