import { IDemande, NewDemande } from './demande.model';

export const sampleWithRequiredData: IDemande = {
  id: 98910,
};

export const sampleWithPartialData: IDemande = {
  id: 52255,
};

export const sampleWithFullData: IDemande = {
  id: 24953,
  sujet: 'kilogram',
  description: 'port intermediate Recyclé',
};

export const sampleWithNewData: NewDemande = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
