import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICours } from '../cours.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../cours.test-samples';

import { CoursService } from './cours.service';

const requireRestSample: ICours = {
  ...sampleWithRequiredData,
};

describe('Cours Service', () => {
  let service: CoursService;
  let httpMock: HttpTestingController;
  let expectedResult: ICours | ICours[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CoursService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Cours', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const cours = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cours).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cours', () => {
      const cours = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cours).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Cours', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cours', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Cours', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCoursToCollectionIfMissing', () => {
      it('should add a Cours to an empty array', () => {
        const cours: ICours = sampleWithRequiredData;
        expectedResult = service.addCoursToCollectionIfMissing([], cours);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cours);
      });

      it('should not add a Cours to an array that contains it', () => {
        const cours: ICours = sampleWithRequiredData;
        const coursCollection: ICours[] = [
          {
            ...cours,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCoursToCollectionIfMissing(coursCollection, cours);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cours to an array that doesn't contain it", () => {
        const cours: ICours = sampleWithRequiredData;
        const coursCollection: ICours[] = [sampleWithPartialData];
        expectedResult = service.addCoursToCollectionIfMissing(coursCollection, cours);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cours);
      });

      it('should add only unique Cours to an array', () => {
        const coursArray: ICours[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const coursCollection: ICours[] = [sampleWithRequiredData];
        expectedResult = service.addCoursToCollectionIfMissing(coursCollection, ...coursArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cours: ICours = sampleWithRequiredData;
        const cours2: ICours = sampleWithPartialData;
        expectedResult = service.addCoursToCollectionIfMissing([], cours, cours2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cours);
        expect(expectedResult).toContain(cours2);
      });

      it('should accept null and undefined values', () => {
        const cours: ICours = sampleWithRequiredData;
        expectedResult = service.addCoursToCollectionIfMissing([], null, cours, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cours);
      });

      it('should return initial array if no Cours is added', () => {
        const coursCollection: ICours[] = [sampleWithRequiredData];
        expectedResult = service.addCoursToCollectionIfMissing(coursCollection, undefined, null);
        expect(expectedResult).toEqual(coursCollection);
      });
    });

    describe('compareCours', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCours(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCours(entity1, entity2);
        const compareResult2 = service.compareCours(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCours(entity1, entity2);
        const compareResult2 = service.compareCours(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCours(entity1, entity2);
        const compareResult2 = service.compareCours(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
