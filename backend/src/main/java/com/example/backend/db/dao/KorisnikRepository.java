package com.example.backend.db.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.models.Korisnik;
import com.example.backend.models.StatusRegistracije;
import com.example.backend.models.TipKorisnika;

public interface KorisnikRepository extends JpaRepository<Korisnik, String> {

    Optional<Korisnik> findByKorIme(String korIme);

    boolean existsByKorIme(String korIme);

    boolean existsByMejlIgnoreCase(String mejl);

    boolean existsByMaticniBroj(String maticniBroj);

    boolean existsByPib(String pib);

    // varijante koje ignorisu samog korisnika — za proveru jedinstvenosti pri azuriranju profila
    boolean existsByMejlIgnoreCaseAndKorImeNot(String mejl, String korIme);

    boolean existsByMaticniBrojAndKorImeNot(String maticniBroj, String korIme);

    boolean existsByPibAndKorImeNot(String pib, String korIme);

    long countByTipAndStatusRegistracije(TipKorisnika tip, StatusRegistracije status);
}
