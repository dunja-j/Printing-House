package com.example.backend.db.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.models.JavnaNabavka;
import com.example.backend.models.StatusNabavke;

public interface JavnaNabavkaRepository extends JpaRepository<JavnaNabavka, Integer> {

    @Query("""
            SELECT DISTINCT n FROM JavnaNabavka n
            JOIN FETCH n.institucija
            LEFT JOIN FETCH n.stavke s
            LEFT JOIN FETCH s.kategorija
            LEFT JOIN FETCH s.potkategorija
            WHERE n.institucija.korIme = :korIme
            ORDER BY n.id DESC
            """)
    List<JavnaNabavka> zaInstituciju(@Param("korIme") String korIme);

    @Query("""
            SELECT DISTINCT n FROM JavnaNabavka n
            JOIN FETCH n.institucija
            LEFT JOIN FETCH n.stavke s
            LEFT JOIN FETCH s.kategorija
            LEFT JOIN FETCH s.potkategorija
            WHERE n.status = :status
            ORDER BY n.rokZaPonude ASC
            """)
    List<JavnaNabavka> saStatusom(@Param("status") StatusNabavke status);

    @Query("""
            SELECT n FROM JavnaNabavka n
            JOIN FETCH n.institucija
            WHERE n.id = :id
            """)
    Optional<JavnaNabavka> nadjiSaInstitucijom(@Param("id") Integer id);
}
