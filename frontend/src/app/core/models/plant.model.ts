export interface Plant {
  id: number;
  nickname: string;
  speciesName: string;
  photoUrl: string | null;
  location: string;
}

export interface CreatePlantRequest {
  nickname: string;
  speciesName: string;
  photoUrl: string | null;
  location: string;
}
