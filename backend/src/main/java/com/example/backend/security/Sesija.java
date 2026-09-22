package com.example.backend.security;

import org.springframework.http.HttpStatus;

import com.example.backend.dto.PoslovnaGreska;
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

    /** Vraca korisnicko ime prijavljenog korisnika ili prekida zahtev sa 401. */
    public static String zahtevajPrijavu(HttpSession session) {
        String korIme = korIme(session);
        if (korIme == null) {
            throw new PoslovnaGreska(HttpStatus.UNAUTHORIZED, "Niste prijavljeni.");
        }
        return korIme;
    }

    /** Kao zahtevajPrijavu, uz dodatnu proveru da je nalog odgovarajuceg tipa. */
    public static String zahtevajTip(HttpSession session, TipKorisnika... dozvoljeni) {
        String korIme = zahtevajPrijavu(session);
        TipKorisnika tip = tip(session);

        for (TipKorisnika t : dozvoljeni) {
            if (t == tip) {
                return korIme;
            }
        }
        throw new PoslovnaGreska(HttpStatus.FORBIDDEN, "Nemate pristup ovom delu sistema.");
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
