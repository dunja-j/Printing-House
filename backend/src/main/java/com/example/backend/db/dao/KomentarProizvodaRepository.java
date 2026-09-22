package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.models.KomentarProizvoda;

public interface KomentarProizvodaRepository extends JpaRepository<KomentarProizvoda, Integer> {

    @Query("""
            SELECT k FROM KomentarProizvoda k
            JOIN FETCH k.klijent
            WHERE k.proizvod.id = :proizvodId
            ORDER BY k.datum DESC
            """)
    List<KomentarProizvoda> poslednjiZaProizvod(@Param("proizvodId") Integer proizvodId,
            Pageable stranicenje);
}
