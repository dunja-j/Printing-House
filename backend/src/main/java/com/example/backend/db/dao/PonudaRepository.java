package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.models.Ponuda;

public interface PonudaRepository extends JpaRepository<Ponuda, Integer> {

    @Query("""
            SELECT p FROM Ponuda p
            JOIN FETCH p.stampar
            WHERE p.nabavka.id = :nabavkaId
            ORDER BY p.ukupnaCena ASC, p.datum ASC
            """)
    List<Ponuda> zaNabavku(@Param("nabavkaId") Integer nabavkaId);

    @Query("""
            SELECT p FROM Ponuda p
            JOIN FETCH p.stampar
            WHERE p.nabavka.id IN :nabavkaIds
            ORDER BY p.ukupnaCena ASC, p.datum ASC
            """)
    List<Ponuda> zaNabavke(@Param("nabavkaIds") List<Integer> nabavkaIds);

    boolean existsByNabavkaIdAndStamparKorIme(Integer nabavkaId, String stamparKorIme);
}
