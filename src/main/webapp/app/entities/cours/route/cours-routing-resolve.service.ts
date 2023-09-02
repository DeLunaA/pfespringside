import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICours } from '../cours.model';
import { CoursService } from '../service/cours.service';

export const coursResolve = (route: ActivatedRouteSnapshot): Observable<null | ICours> => {
  const id = route.params['id'];
  if (id) {
    return inject(CoursService)
      .find(id)
      .pipe(
        mergeMap((cours: HttpResponse<ICours>) => {
          if (cours.body) {
            return of(cours.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default coursResolve;
