import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReclamationComponent } from './list/reclamation.component';
import { ReclamationDetailComponent } from './detail/reclamation-detail.component';
import { ReclamationUpdateComponent } from './update/reclamation-update.component';
import ReclamationResolve from './route/reclamation-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const reclamationRoute: Routes = [
  {
    path: '',
    component: ReclamationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReclamationDetailComponent,
    resolve: {
      reclamation: ReclamationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReclamationUpdateComponent,
    resolve: {
      reclamation: ReclamationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReclamationUpdateComponent,
    resolve: {
      reclamation: ReclamationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reclamationRoute;
