import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-pocetna',
  imports: [RouterLink],
  template: `
    <div class="ph-omot">
      <section class="ph-kartica">
        <h1>Dobrodošli u Printing House</h1>
        <p>
          Platforma koja povezuje štamparije i klijente koji naručuju štampane proizvode.
        </p>
        <p class="ph-napomena">
          Broj registrovanih štamparija i TOP 5 proizvoda biće prikazani ovde (funkcionalnost #4).
        </p>

        @if (korisnik()) {
          <a class="ph-dugme ph-dugme-link" [routerLink]="mojaRuta()">Idi na moj nalog</a>
        } @else {
          <a class="ph-dugme ph-dugme-link" routerLink="/login">Prijavi se</a>
        }
      </section>
    </div>
  `
})
export class Pocetna {
  private auth = inject(AuthService);

  korisnik = this.auth.korisnik;
  mojaRuta = computed(() => {
    const k = this.korisnik();
    return k ? this.auth.pocetnaRuta(k.tip) : '/login';
  });
}
