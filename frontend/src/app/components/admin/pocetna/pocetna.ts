import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-admin-pocetna',
  imports: [RouterLink],
  template: `
    @if (korisnik(); as k) {
      <div class="ph-omot">
        <section class="ph-kartica">
          <h1>Administratorski panel</h1>
          <p>Prijavljeni ste kao {{ k.korIme }}.</p>

          <nav class="precice">
            <a routerLink="/admin/zahtevi">Zahtevi za registraciju</a>
            <a routerLink="/admin/korisnici">Korisnički nalozi</a>
            <a routerLink="/admin/kategorije">Kategorije proizvoda</a>
            <a routerLink="/profil">Moj profil</a>
          </nav>

          <p class="ph-napomena">Statistika dolazi u narednim funkcionalnostima.</p>
        </section>
      </div>
    }
  `,
  styles: `
    .precice {
      display: flex;
      flex-wrap: wrap;
      gap: 0.75rem;
      margin: 1.25rem 0;
    }

    .precice a {
      padding: 0.6rem 1rem;
      background: var(--ph-pozadina);
      border: 1px solid var(--ph-ivica);
      border-radius: var(--ph-radius);
      color: var(--ph-tamna);
      text-decoration: none;
      font-weight: 600;
      font-size: 0.9rem;
    }

    .precice a:hover {
      border-color: var(--ph-primarna);
      color: var(--ph-primarna);
    }
  `
})
export class AdminPocetna {
  korisnik = inject(AuthService).korisnik;
}
