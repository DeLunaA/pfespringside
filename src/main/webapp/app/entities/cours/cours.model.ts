export interface ICours {
  id: number;
  sujet?: string | null;
  description?: string | null;
}

export type NewCours = Omit<ICours, 'id'> & { id: null };
