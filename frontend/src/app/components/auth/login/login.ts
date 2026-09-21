import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private auth = inject(AuthService);
  private router = inject(Router);

  korIme = '';
  lozinka = '';
  greska = signal<string | null>(null);
  ucitavanje = signal(false);

  /** Poruka koju postavlja registracija posle uspešnog kreiranja naloga. */
  obavestenje = signal<string | null>(
    inject(ActivatedRoute).snapshot.queryParamMap.get('poruka')
  );

  posalji(): void {
    this.greska.set(null);

    if (!this.korIme.trim() || !this.lozinka) {
      this.greska.set('Unesite korisničko ime i lozinku.');
      return;
    }

    this.ucitavanje.set(true);
    this.auth.prijava(this.korIme.trim(), this.lozinka).subscribe({
      next: (k) => {
        this.ucitavanje.set(false);
        this.router.navigateByUrl(this.auth.pocetnaRuta(k.tip));
      },
      error: (err) => {
        this.ucitavanje.set(false);
        this.greska.set(err?.error?.poruka ?? 'Prijava nije uspela. Pokušajte ponovo.');
      }
    });
  }
}
