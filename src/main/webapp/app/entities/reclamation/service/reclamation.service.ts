import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReclamation, NewReclamation } from '../reclamation.model';

export type PartialUpdateReclamation = Partial<IReclamation> & Pick<IReclamation, 'id'>;

type RestOf<T extends IReclamation | NewReclamation> = Omit<T, 'createdIn'> & {
  createdIn?: string | null;
};

export type RestReclamation = RestOf<IReclamation>;

export type NewRestReclamation = RestOf<NewReclamation>;

export type PartialUpdateRestReclamation = RestOf<PartialUpdateReclamation>;

export type EntityResponseType = HttpResponse<IReclamation>;
export type EntityArrayResponseType = HttpResponse<IReclamation[]>;

@Injectable({ providedIn: 'root' })
export class ReclamationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reclamations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reclamation: NewReclamation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reclamation);
    return this.http
      .post<RestReclamation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(reclamation: IReclamation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reclamation);
    return this.http
      .put<RestReclamation>(`${this.resourceUrl}/${this.getReclamationIdentifier(reclamation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(reclamation: PartialUpdateReclamation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reclamation);
    return this.http
      .patch<RestReclamation>(`${this.resourceUrl}/${this.getReclamationIdentifier(reclamation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReclamation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReclamation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReclamationIdentifier(reclamation: Pick<IReclamation, 'id'>): number {
    return reclamation.id;
  }

  compareReclamation(o1: Pick<IReclamation, 'id'> | null, o2: Pick<IReclamation, 'id'> | null): boolean {
    return o1 && o2 ? this.getReclamationIdentifier(o1) === this.getReclamationIdentifier(o2) : o1 === o2;
  }

  addReclamationToCollectionIfMissing<Type extends Pick<IReclamation, 'id'>>(
    reclamationCollection: Type[],
    ...reclamationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const reclamations: Type[] = reclamationsToCheck.filter(isPresent);
    if (reclamations.length > 0) {
      const reclamationCollectionIdentifiers = reclamationCollection.map(
        reclamationItem => this.getReclamationIdentifier(reclamationItem)!
      );
      const reclamationsToAdd = reclamations.filter(reclamationItem => {
        const reclamationIdentifier = this.getReclamationIdentifier(reclamationItem);
        if (reclamationCollectionIdentifiers.includes(reclamationIdentifier)) {
          return false;
        }
        reclamationCollectionIdentifiers.push(reclamationIdentifier);
        return true;
      });
      return [...reclamationsToAdd, ...reclamationCollection];
    }
    return reclamationCollection;
  }

  protected convertDateFromClient<T extends IReclamation | NewReclamation | PartialUpdateReclamation>(reclamation: T): RestOf<T> {
    return {
      ...reclamation,
      createdIn: reclamation.createdIn?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReclamation: RestReclamation): IReclamation {
    return {
      ...restReclamation,
      createdIn: restReclamation.createdIn ? dayjs(restReclamation.createdIn) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReclamation>): HttpResponse<IReclamation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReclamation[]>): HttpResponse<IReclamation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
