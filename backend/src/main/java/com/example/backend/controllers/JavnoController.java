package com.example.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.JavnaPocetnaDto;
import com.example.backend.service.JavnoService;

@RestController
@RequestMapping("/api/javno")
public class JavnoController {

    private final JavnoService javnoService;

    public JavnoController(JavnoService javnoService) {
        this.javnoService = javnoService;
    }

    @GetMapping("/pocetna")
    public JavnaPocetnaDto pocetna() {
        return javnoService.pocetna();
    }
}
