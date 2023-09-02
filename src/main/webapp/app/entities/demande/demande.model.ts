import { IUser } from 'app/entities/user/user.model';

export interface IDemande {
  id: number;
  sujet?: string | null;
  description?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDemande = Omit<IDemande, 'id'> & { id: null };
