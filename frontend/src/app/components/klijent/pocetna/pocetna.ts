import { Component, inject } from '@angular/core';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-klijent-pocetna',
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
          <p class="ph-napomena">
            Profil, pretraga proizvoda, e-korpa i narudžbine dolaze u narednim funkcionalnostima.
          </p>
        </section>
      </div>
    }
  `
})
export class KlijentPocetna {
  korisnik = inject(AuthService).korisnik;
}
