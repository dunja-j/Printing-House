import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-klijent-pocetna',
  imports: [RouterLink],
  template: `
    @if (korisnik(); as k) {
      <div class="ph-omot">
        <section class="ph-kartica">
          <h1>Zdravo, {{ k.ime }} {{ k.prezime }}</h1>
          <p>
            Prijavljeni ste kao
            <strong>{{ k.tip === 'klijent_pravno' ? 'pravno lice' : 'fizičko lice' }}</strong
            >@if (k.nazivInstitucije) { ({{ k.nazivInstitucije }}) }.
          </p>

          <nav class="precice">
            <a routerLink="/klijent/narudzbine">Moje narudžbine</a>
            <a routerLink="/pretraga">Pretraga proizvoda</a>
            <a routerLink="/profil">Moj profil</a>
          </nav>

          <p class="ph-napomena">E-korpa i poručivanje dolaze u narednim funkcionalnostima.</p>
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
export class KlijentPocetna {
  korisnik = inject(AuthService).korisnik;
}
