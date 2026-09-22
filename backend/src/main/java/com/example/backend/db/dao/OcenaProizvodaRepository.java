package com.example.backend.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.models.OcenaProizvoda;
import com.example.backend.models.VrednostOcene;

public interface OcenaProizvodaRepository extends JpaRepository<OcenaProizvoda, Integer> {

    long countByProizvodIdAndVrednost(Integer proizvodId, VrednostOcene vrednost);
}
