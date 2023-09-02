import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CoursDetailComponent } from './cours-detail.component';

describe('Cours Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CoursDetailComponent,
              resolve: { cours: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(CoursDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load cours on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CoursDetailComponent);

      // THEN
      expect(instance.cours).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
