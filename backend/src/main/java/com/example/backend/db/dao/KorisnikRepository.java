package com.example.backend.db.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.models.Korisnik;

public interface KorisnikRepository extends JpaRepository<Korisnik, String> {

    Optional<Korisnik> findByKorIme(String korIme);

    boolean existsByKorIme(String korIme);

    boolean existsByMejlIgnoreCase(String mejl);

    boolean existsByMaticniBroj(String maticniBroj);

    boolean existsByPib(String pib);
}
