import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DemandeComponent } from './list/demande.component';
import { DemandeDetailComponent } from './detail/demande-detail.component';
import { DemandeUpdateComponent } from './update/demande-update.component';
import DemandeResolve from './route/demande-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const demandeRoute: Routes = [
  {
    path: '',
    component: DemandeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DemandeDetailComponent,
    resolve: {
      demande: DemandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DemandeUpdateComponent,
    resolve: {
      demande: DemandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DemandeUpdateComponent,
    resolve: {
      demande: DemandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default demandeRoute;
