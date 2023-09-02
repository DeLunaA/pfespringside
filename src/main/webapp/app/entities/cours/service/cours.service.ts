import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICours, NewCours } from '../cours.model';

export type PartialUpdateCours = Partial<ICours> & Pick<ICours, 'id'>;

export type EntityResponseType = HttpResponse<ICours>;
export type EntityArrayResponseType = HttpResponse<ICours[]>;

@Injectable({ providedIn: 'root' })
export class CoursService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cours');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cours: NewCours): Observable<EntityResponseType> {
    return this.http.post<ICours>(this.resourceUrl, cours, { observe: 'response' });
  }

  update(cours: ICours): Observable<EntityResponseType> {
    return this.http.put<ICours>(`${this.resourceUrl}/${this.getCoursIdentifier(cours)}`, cours, { observe: 'response' });
  }

  partialUpdate(cours: PartialUpdateCours): Observable<EntityResponseType> {
    return this.http.patch<ICours>(`${this.resourceUrl}/${this.getCoursIdentifier(cours)}`, cours, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICours>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICours[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCoursIdentifier(cours: Pick<ICours, 'id'>): number {
    return cours.id;
  }

  compareCours(o1: Pick<ICours, 'id'> | null, o2: Pick<ICours, 'id'> | null): boolean {
    return o1 && o2 ? this.getCoursIdentifier(o1) === this.getCoursIdentifier(o2) : o1 === o2;
  }

  addCoursToCollectionIfMissing<Type extends Pick<ICours, 'id'>>(
    coursCollection: Type[],
    ...coursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cours: Type[] = coursToCheck.filter(isPresent);
    if (cours.length > 0) {
      const coursCollectionIdentifiers = coursCollection.map(coursItem => this.getCoursIdentifier(coursItem)!);
      const coursToAdd = cours.filter(coursItem => {
        const coursIdentifier = this.getCoursIdentifier(coursItem);
        if (coursCollectionIdentifiers.includes(coursIdentifier)) {
          return false;
        }
        coursCollectionIdentifiers.push(coursIdentifier);
        return true;
      });
      return [...coursToAdd, ...coursCollection];
    }
    return coursCollection;
  }
}
