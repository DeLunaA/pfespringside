import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReclamation } from '../reclamation.model';
import { ReclamationService } from '../service/reclamation.service';

export const reclamationResolve = (route: ActivatedRouteSnapshot): Observable<null | IReclamation> => {
  const id = route.params['id'];
  if (id) {
    return inject(ReclamationService)
      .find(id)
      .pipe(
        mergeMap((reclamation: HttpResponse<IReclamation>) => {
          if (reclamation.body) {
            return of(reclamation.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default reclamationResolve;
