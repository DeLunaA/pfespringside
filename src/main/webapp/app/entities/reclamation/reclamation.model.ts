import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IReclamation {
  id: number;
  createdIn?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewReclamation = Omit<IReclamation, 'id'> & { id: null };
