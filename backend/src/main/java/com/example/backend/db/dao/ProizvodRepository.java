package com.example.backend.db.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.dto.PretragaRedDto;
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

    /** Oba parametra su opciona — ako su null, taj uslov se ne primenjuje. */
    @Query("""
            SELECT new com.example.backend.dto.PretragaRedDto(
                       p.id, p.naziv, s.nazivInstitucije, s.grad, k.naziv,
                       p.jedinicnaCena, p.kolicinaNaLageru, COUNT(o.id))
            FROM Proizvod p
            JOIN p.stampar s
            JOIN p.kategorija k
            LEFT JOIN OcenaProizvoda o
                   ON o.proizvod = p AND o.vrednost = com.example.backend.models.VrednostOcene.lajk
            WHERE p.aktivan = true
              AND (:naziv IS NULL OR LOWER(p.naziv) LIKE LOWER(CONCAT('%', :naziv, '%')))
              AND (:kategorijaId IS NULL OR k.id = :kategorijaId)
            GROUP BY p.id, p.naziv, s.nazivInstitucije, s.grad, k.naziv,
                     p.jedinicnaCena, p.kolicinaNaLageru
            ORDER BY p.naziv ASC
            """)
    List<PretragaRedDto> pretrazi(@Param("naziv") String naziv,
            @Param("kategorijaId") Integer kategorijaId);

    @Query("""
            SELECT p FROM Proizvod p
            JOIN FETCH p.stampar
            JOIN FETCH p.kategorija
            LEFT JOIN FETCH p.potkategorija
            WHERE p.id = :id AND p.aktivan = true
            """)
    Optional<Proizvod> nadjiAktivanSaDetaljima(@Param("id") Integer id);

    @Query("""
            SELECT p FROM Proizvod p
            JOIN FETCH p.kategorija
            LEFT JOIN FETCH p.potkategorija
            WHERE p.stampar.korIme = :korIme
            ORDER BY p.naziv ASC
            """)
    List<Proizvod> zaStampara(@Param("korIme") String korIme);

    boolean existsBySifraAndStamparKorIme(String sifra, String stamparKorIme);
}
