import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReclamationDetailComponent } from './reclamation-detail.component';

describe('Reclamation Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReclamationDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ReclamationDetailComponent,
              resolve: { reclamation: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(ReclamationDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load reclamation on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReclamationDetailComponent);

      // THEN
      expect(instance.reclamation).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
