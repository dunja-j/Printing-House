package com.example.backend.security;

import com.example.backend.models.Korisnik;
import com.example.backend.models.TipKorisnika;

import jakarta.servlet.http.HttpSession;

/**
 * Jedno mesto za citanje/upis prijavljenog korisnika u HTTP sesiju, da se
 * naziv atributa ne bi ponavljao po kontrolerima.
 */
public final class Sesija {

    public static final String ATRIBUT = "korisnik";
    private static final String ATRIBUT_REGISTRACIJE = "registracija";

    private Sesija() {
    }

    public static void prijavi(HttpSession session, Korisnik korisnik) {
        session.setAttribute(ATRIBUT, korisnik.getKorIme());
        session.setAttribute("tip", korisnik.getTip());
    }

    public static void odjavi(HttpSession session) {
        session.invalidate();
    }

    /** Korisnicko ime prijavljenog korisnika, ili null ako niko nije prijavljen. */
    public static String korIme(HttpSession session) {
        return (String) session.getAttribute(ATRIBUT);
    }

    public static TipKorisnika tip(HttpSession session) {
        return (TipKorisnika) session.getAttribute("tip");
    }

    /** Pamti ko se upravo registrovao, da bi drugi korak (slika) znao kom nalogu pripada. */
    public static void zapocetaRegistracija(HttpSession session, String korIme) {
        session.setAttribute(ATRIBUT_REGISTRACIJE, korIme);
    }

    public static String korImeRegistracije(HttpSession session) {
        return (String) session.getAttribute(ATRIBUT_REGISTRACIJE);
    }

    public static void zavrsiRegistraciju(HttpSession session) {
        session.removeAttribute(ATRIBUT_REGISTRACIJE);
    }
}
