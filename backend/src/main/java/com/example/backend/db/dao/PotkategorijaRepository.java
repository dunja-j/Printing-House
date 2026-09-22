package com.example.backend.db.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.models.Potkategorija;

public interface PotkategorijaRepository extends JpaRepository<Potkategorija, Integer> {

    List<Potkategorija> findByKategorijaIdOrderByNazivAsc(Integer kategorijaId);
}
