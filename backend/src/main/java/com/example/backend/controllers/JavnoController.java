package com.example.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.DetaljiProizvodaDto;
import com.example.backend.dto.JavnaPocetnaDto;
import com.example.backend.dto.KategorijaDto;
import com.example.backend.dto.PretragaRedDto;
import com.example.backend.service.JavnoService;
import com.example.backend.service.ProizvodService;

@RestController
@RequestMapping("/api/javno")
public class JavnoController {

    private final JavnoService javnoService;
    private final ProizvodService proizvodService;

    public JavnoController(JavnoService javnoService, ProizvodService proizvodService) {
        this.javnoService = javnoService;
        this.proizvodService = proizvodService;
    }

    @GetMapping("/pocetna")
    public JavnaPocetnaDto pocetna() {
        return javnoService.pocetna();
    }

    @GetMapping("/kategorije")
    public List<KategorijaDto> kategorije() {
        return javnoService.kategorije();
    }

    @GetMapping("/proizvodi")
    public List<PretragaRedDto> pretraga(
            @RequestParam(required = false) String naziv,
            @RequestParam(required = false) Integer kategorijaId) {
        return javnoService.pretraga(naziv, kategorijaId);
    }

    @GetMapping("/proizvodi/{id}")
    public DetaljiProizvodaDto detalji(@PathVariable Integer id) {
        return proizvodService.detalji(id);
    }
}
