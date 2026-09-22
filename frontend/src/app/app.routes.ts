import { Routes } from '@angular/router';

import { AdminPocetna } from './components/admin/pocetna/pocetna';
import { Login } from './components/auth/login/login';
import { LoginAdmin } from './components/auth/login-admin/login-admin';
import { Registracija } from './components/auth/registracija/registracija';
import { DetaljiProizvodaKomponenta } from './components/javno/detalji-proizvoda/detalji-proizvoda';
import { Pocetna } from './components/javno/pocetna/pocetna';
import { Pretraga } from './components/javno/pretraga/pretraga';
import { KlijentPocetna } from './components/klijent/pocetna/pocetna';
import { Profil } from './components/profil/profil';
import { StamparPocetna } from './components/stampar/pocetna/pocetna';
import { dozvoljenTip, samoGost, samoPrijavljeni } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: Pocetna },
  { path: 'pretraga', component: Pretraga },
  { path: 'proizvod/:id', component: DetaljiProizvodaKomponenta },
  { path: 'login', component: Login, canActivate: [samoGost] },
  { path: 'registracija', component: Registracija, canActivate: [samoGost] },
  { path: 'profil', component: Profil, canActivate: [samoPrijavljeni] },
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
