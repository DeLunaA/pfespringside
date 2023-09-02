import { ICours, NewCours } from './cours.model';

export const sampleWithRequiredData: ICours = {
  id: 88815,
};

export const sampleWithPartialData: ICours = {
  id: 49015,
};

export const sampleWithFullData: ICours = {
  id: 87181,
  sujet: 'partenaire Folk Triplette',
  description: 'miam b',
};

export const sampleWithNewData: NewCours = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
