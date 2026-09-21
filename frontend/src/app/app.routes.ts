import { Routes } from '@angular/router';

import { AdminPocetna } from './components/admin/pocetna/pocetna';
import { Login } from './components/auth/login/login';
import { LoginAdmin } from './components/auth/login-admin/login-admin';
import { Registracija } from './components/auth/registracija/registracija';
import { Pocetna } from './components/javno/pocetna/pocetna';
import { KlijentPocetna } from './components/klijent/pocetna/pocetna';
import { StamparPocetna } from './components/stampar/pocetna/pocetna';
import { dozvoljenTip, samoGost } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: Pocetna },
  { path: 'login', component: Login, canActivate: [samoGost] },
  { path: 'registracija', component: Registracija, canActivate: [samoGost] },
  // skrivena ruta za administratora — namerno se ne linkuje ni sa jedne javne strane
  { path: 'admin-login', component: LoginAdmin, canActivate: [samoGost] },
  {
    path: 'klijent',
    component: KlijentPocetna,
    canActivate: [dozvoljenTip(['klijent_fizicko', 'klijent_pravno'])]
  },
  { path: 'stampar', component: StamparPocetna, canActivate: [dozvoljenTip(['stampar'])] },
  { path: 'admin', component: AdminPocetna, canActivate: [dozvoljenTip(['administrator'])] },
  { path: '**', redirectTo: '' }
];
