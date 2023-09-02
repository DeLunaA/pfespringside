import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DemandeDetailComponent } from './demande-detail.component';

describe('Demande Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DemandeDetailComponent,
              resolve: { demande: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(DemandeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load demande on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DemandeDetailComponent);

      // THEN
      expect(instance.demande).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
