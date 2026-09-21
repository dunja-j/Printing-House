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
}
