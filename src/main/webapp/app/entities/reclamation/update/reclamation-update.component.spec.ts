import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReclamationFormService } from './reclamation-form.service';
import { ReclamationService } from '../service/reclamation.service';
import { IReclamation } from '../reclamation.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ReclamationUpdateComponent } from './reclamation-update.component';

describe('Reclamation Management Update Component', () => {
  let comp: ReclamationUpdateComponent;
  let fixture: ComponentFixture<ReclamationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reclamationFormService: ReclamationFormService;
  let reclamationService: ReclamationService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ReclamationUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ReclamationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReclamationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reclamationFormService = TestBed.inject(ReclamationFormService);
    reclamationService = TestBed.inject(ReclamationService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const reclamation: IReclamation = { id: 456 };
      const user: IUser = { id: 82327 };
      reclamation.user = user;

      const userCollection: IUser[] = [{ id: 39311 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reclamation });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reclamation: IReclamation = { id: 456 };
      const user: IUser = { id: 479 };
      reclamation.user = user;

      activatedRoute.data = of({ reclamation });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.reclamation).toEqual(reclamation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReclamation>>();
      const reclamation = { id: 123 };
      jest.spyOn(reclamationFormService, 'getReclamation').mockReturnValue(reclamation);
      jest.spyOn(reclamationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reclamation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reclamation }));
      saveSubject.complete();

      // THEN
      expect(reclamationFormService.getReclamation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reclamationService.update).toHaveBeenCalledWith(expect.objectContaining(reclamation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReclamation>>();
      const reclamation = { id: 123 };
      jest.spyOn(reclamationFormService, 'getReclamation').mockReturnValue({ id: null });
      jest.spyOn(reclamationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reclamation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reclamation }));
      saveSubject.complete();

      // THEN
      expect(reclamationFormService.getReclamation).toHaveBeenCalled();
      expect(reclamationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReclamation>>();
      const reclamation = { id: 123 };
      jest.spyOn(reclamationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reclamation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reclamationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
