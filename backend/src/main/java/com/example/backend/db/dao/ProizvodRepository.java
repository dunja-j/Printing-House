package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.dto.TopProizvodDto;
import com.example.backend.models.Proizvod;

public interface ProizvodRepository extends JpaRepository<Proizvod, Integer> {

    /**
     * Najlajkovaniji aktivni proizvodi. LEFT JOIN je namerno — proizvod bez ijedne
     * ocene treba da se pojavi sa nulom, a ne da ispadne iz rezultata.
     */
    @Query("""
            SELECT new com.example.backend.dto.TopProizvodDto(
                       p.id, p.naziv, p.slikaUrl, s.nazivInstitucije, s.grad, COUNT(o.id))
            FROM Proizvod p
            JOIN p.stampar s
            LEFT JOIN OcenaProizvoda o
                   ON o.proizvod = p AND o.vrednost = com.example.backend.models.VrednostOcene.lajk
            WHERE p.aktivan = true
            GROUP BY p.id, p.naziv, p.slikaUrl, s.nazivInstitucije, s.grad
            ORDER BY COUNT(o.id) DESC, p.naziv ASC
            """)
    List<TopProizvodDto> najboljeOceneni(Pageable stranicenje);
}
