import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ReclamationFormService, ReclamationFormGroup } from './reclamation-form.service';
import { IReclamation } from '../reclamation.model';
import { ReclamationService } from '../service/reclamation.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  standalone: true,
  selector: 'jhi-reclamation-update',
  templateUrl: './reclamation-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReclamationUpdateComponent implements OnInit {
  isSaving = false;
  reclamation: IReclamation | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: ReclamationFormGroup = this.reclamationFormService.createReclamationFormGroup();

  constructor(
    protected reclamationService: ReclamationService,
    protected reclamationFormService: ReclamationFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reclamation }) => {
      this.reclamation = reclamation;
      if (reclamation) {
        this.updateForm(reclamation);
      }
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reclamation = this.reclamationFormService.getReclamation(this.editForm);
    if (reclamation.id !== null) {
      this.subscribeToSaveResponse(this.reclamationService.update(reclamation));
    } else {
      this.subscribeToSaveResponse(this.reclamationService.create(reclamation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReclamation>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(reclamation: IReclamation): void {
    this.reclamation = reclamation;
    this.reclamationFormService.resetForm(this.editForm, reclamation);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, reclamation.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.reclamation?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
