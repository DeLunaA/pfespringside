import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'demande',
        data: { pageTitle: 'sssssssssApp.demande.home.title' },
        loadChildren: () => import('./demande/demande.routes'),
      },
      {
        path: 'reclamation',
        data: { pageTitle: 'sssssssssApp.reclamation.home.title' },
        loadChildren: () => import('./reclamation/reclamation.routes'),
      },
      {
        path: 'cours',
        data: { pageTitle: 'sssssssssApp.cours.home.title' },
        loadChildren: () => import('./cours/cours.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
