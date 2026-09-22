package com.example.backend.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.backend.dto.PoslovnaGreska;

/** Sve greske ka frontu izlaze u istom obliku: {"poruka": "..."}. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PoslovnaGreska.class)
    public ResponseEntity<Map<String, String>> poslovna(PoslovnaGreska e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("poruka", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacija(MethodArgumentNotValidException e) {
        String poruka = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getDefaultMessage())
                .orElse("Neispravni podaci u zahtevu.");
        return ResponseEntity.badRequest().body(Map.of("poruka", poruka));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> neispravanJson(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("poruka", "Neispravan format zahteva."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> prevelikFajl(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().body(Map.of("poruka", "Slika sme biti najviše 2 MB."));
    }

    /** Npr. /proizvodi/abc kad se ocekuje broj. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> pogresanTip(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("poruka", "Neispravna vrednost parametra \"" + e.getName() + "\"."));
    }

    /** Bez ovoga bi nepostojeca adresa upala u catch-all ispod i vracala 500. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> nemaRute(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("poruka", "Tražena adresa ne postoji."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> ostalo(Exception e) {
        log.error("Neocekivana greska", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("poruka", "Došlo je do greške na serveru. Pokušajte ponovo."));
    }
}
