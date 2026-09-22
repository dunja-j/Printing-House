import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-stampar-pocetna',
  imports: [RouterLink],
  template: `
    @if (korisnik(); as k) {
      <div class="ph-omot">
        <section class="ph-kartica">
          <h1>{{ k.nazivInstitucije }}</h1>
          <p>Odgovorno lice: {{ k.ime }} {{ k.prezime }} &middot; {{ k.grad }}</p>

          <nav class="precice">
            <a routerLink="/stampar/narudzbine">Naručeni proizvodi</a>
            <a routerLink="/stampar/nabavke">Javne nabavke</a>
            <a routerLink="/stampar/proizvodi">Proizvodi i usluge</a>
            <a routerLink="/stampar/proizvodi/novi">Dodaj proizvod</a>
            <a routerLink="/profil">Moj profil</a>
          </nav>
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
export class StamparPocetna {
  korisnik = inject(AuthService).korisnik;
}
