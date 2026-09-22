package com.example.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.db.dao.KorisnikRepository;
import com.example.backend.dto.AzuriranjeProfilaRequest;
import com.example.backend.dto.PoslovnaGreska;
import com.example.backend.models.Korisnik;
import com.example.backend.models.TipKorisnika;

@Service
public class ProfilService {

    private final KorisnikRepository korisnikRepository;
    private final SlikaService slikaService;

    public ProfilService(KorisnikRepository korisnikRepository, SlikaService slikaService) {
        this.korisnikRepository = korisnikRepository;
        this.slikaService = slikaService;
    }

    @Transactional(readOnly = true)
    public Korisnik nadji(String korIme) {
        return korisnikRepository.findByKorIme(korIme)
                .orElseThrow(() -> new PoslovnaGreska(HttpStatus.NOT_FOUND, "Nalog nije pronađen."));
    }

    @Transactional
    public Korisnik azuriraj(String korIme, AzuriranjeProfilaRequest zahtev) {
        Korisnik korisnik = nadji(korIme);
        boolean institucija = korisnik.getTip() == TipKorisnika.klijent_pravno
                || korisnik.getTip() == TipKorisnika.stampar;

        if (institucija) {
            proveriPoljaInstitucije(zahtev);
        }
        proveriJedinstvenost(zahtev, korIme, institucija);

        korisnik.setIme(zahtev.getIme().trim());
        korisnik.setPrezime(zahtev.getPrezime().trim());
        korisnik.setTelefon(zahtev.getTelefon().trim());
        korisnik.setMejl(zahtev.getMejl().trim());
        korisnik.setGrad(prazanKaoNull(zahtev.getGrad()));

        if (institucija) {
            korisnik.setNazivInstitucije(zahtev.getNazivInstitucije().trim());
            korisnik.setAdresaSedista(zahtev.getAdresaSedista().trim());
            korisnik.setMaticniBroj(zahtev.getMaticniBroj().trim());
            korisnik.setPib(zahtev.getPib().trim());
        }
        return korisnikRepository.save(korisnik);
    }

    @Transactional
    public Korisnik promeniSliku(String korIme, MultipartFile slika) {
        Korisnik korisnik = nadji(korIme);
        korisnik.setSlikaUrl(slikaService.sacuvajProfilnu(slika, korIme));
        return korisnikRepository.save(korisnik);
    }

    private void proveriPoljaInstitucije(AzuriranjeProfilaRequest z) {
        if (prazno(z.getNazivInstitucije())) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Naziv institucije je obavezan.");
        }
        if (prazno(z.getAdresaSedista())) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Adresa sedišta je obavezna.");
        }
        if (prazno(z.getGrad())) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Grad je obavezan.");
        }
        if (prazno(z.getMaticniBroj()) || !z.getMaticniBroj().trim().matches("^\\d{8}$")) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST, "Matični broj mora imati tačno 8 cifara.");
        }
        if (prazno(z.getPib()) || !z.getPib().trim().matches("^[1-9]\\d{8}$")) {
            throw new PoslovnaGreska(HttpStatus.BAD_REQUEST,
                    "PIB mora imati 9 cifara i ne sme počinjati nulom.");
        }
    }

    private void proveriJedinstvenost(AzuriranjeProfilaRequest z, String korIme, boolean institucija) {
        if (korisnikRepository.existsByMejlIgnoreCaseAndKorImeNot(z.getMejl().trim(), korIme)) {
            throw new PoslovnaGreska(HttpStatus.CONFLICT, "Nalog sa ovom mejl adresom već postoji.");
        }
        if (institucija) {
            if (korisnikRepository.existsByMaticniBrojAndKorImeNot(z.getMaticniBroj().trim(), korIme)) {
                throw new PoslovnaGreska(HttpStatus.CONFLICT, "Matični broj je već registrovan.");
            }
            if (korisnikRepository.existsByPibAndKorImeNot(z.getPib().trim(), korIme)) {
                throw new PoslovnaGreska(HttpStatus.CONFLICT, "PIB je već registrovan.");
            }
        }
    }

    private static boolean prazno(String s) {
        return s == null || s.isBlank();
    }

    private static String prazanKaoNull(String s) {
        return prazno(s) ? null : s.trim();
    }
}
