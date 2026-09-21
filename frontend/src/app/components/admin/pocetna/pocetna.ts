import { Component, inject } from '@angular/core';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-admin-pocetna',
  template: `
    @if (korisnik(); as k) {
      <div class="ph-omot">
        <section class="ph-kartica">
          <h1>Administratorski panel</h1>
          <p>Prijavljeni ste kao {{ k.korIme }}.</p>
          <p class="ph-napomena">
            Upravljanje nalozima, zahtevi za registraciju, kategorije i statistika dolaze u
            narednim funkcionalnostima.
          </p>
        </section>
      </div>
    }
  `
})
export class AdminPocetna {
  korisnik = inject(AuthService).korisnik;
}
