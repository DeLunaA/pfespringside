import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CoursComponent } from './list/cours.component';
import { CoursDetailComponent } from './detail/cours-detail.component';
import { CoursUpdateComponent } from './update/cours-update.component';
import CoursResolve from './route/cours-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const coursRoute: Routes = [
  {
    path: '',
    component: CoursComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CoursDetailComponent,
    resolve: {
      cours: CoursResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CoursUpdateComponent,
    resolve: {
      cours: CoursResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CoursUpdateComponent,
    resolve: {
      cours: CoursResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default coursRoute;
