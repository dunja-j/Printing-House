package com.example.backend.dto;

import java.util.List;

/** Kategorija sa svojim potkategorijama — za padajuće liste pri dodavanju proizvoda. */
public class KategorijaSaPotkategorijamaDto {

    private final Integer id;
    private final String naziv;
    private final List<KategorijaDto> potkategorije;

    public KategorijaSaPotkategorijamaDto(Integer id, String naziv, List<KategorijaDto> potkategorije) {
        this.id = id;
        this.naziv = naziv;
        this.potkategorije = potkategorije;
    }

    public Integer getId() {
        return id;
    }

    public String getNaziv() {
        return naziv;
    }

    public List<KategorijaDto> getPotkategorije() {
        return potkategorije;
    }
}
