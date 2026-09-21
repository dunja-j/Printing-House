import { Component, inject } from '@angular/core';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-stampar-pocetna',
  template: `
    @if (korisnik(); as k) {
      <div class="ph-omot">
        <section class="ph-kartica">
          <h1>{{ k.nazivInstitucije }}</h1>
          <p>Odgovorno lice: {{ k.ime }} {{ k.prezime }} &middot; {{ k.grad }}</p>
          <p class="ph-napomena">
            Proizvodi i usluge, količine, narudžbine i licitacije dolaze u narednim
            funkcionalnostima.
          </p>
        </section>
      </div>
    }
  `
})
export class StamparPocetna {
  korisnik = inject(AuthService).korisnik;
}
