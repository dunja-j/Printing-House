import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { JavnaPocetna } from '../models/javno';

@Injectable({ providedIn: 'root' })
export class JavnoService {
  private http = inject(HttpClient);

  pocetna(): Observable<JavnaPocetna> {
    return this.http.get<JavnaPocetna>(`${environment.apiUrl}/javno/pocetna`);
  }

  /** Puna putanja do slike proizvoda, sa rezervnom slikom ako je nema. */
  slikaProizvoda(naziv: string | null): string {
    return `${environment.fileUrl}/proizvodi/${naziv ?? 'default_product_image.jpg'}`;
  }
}
