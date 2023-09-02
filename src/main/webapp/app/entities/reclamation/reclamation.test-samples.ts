import dayjs from 'dayjs/esm';

import { IReclamation, NewReclamation } from './reclamation.model';

export const sampleWithRequiredData: IReclamation = {
  id: 94696,
};

export const sampleWithPartialData: IReclamation = {
  id: 79287,
};

export const sampleWithFullData: IReclamation = {
  id: 85005,
  createdIn: dayjs('2023-07-29T13:58'),
};

export const sampleWithNewData: NewReclamation = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
