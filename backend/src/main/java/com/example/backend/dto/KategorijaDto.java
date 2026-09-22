package com.example.backend.dto;

public class KategorijaDto {

    private final Integer id;
    private final String naziv;

    public KategorijaDto(Integer id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }

    public Integer getId() {
        return id;
    }

    public String getNaziv() {
        return naziv;
    }
}
