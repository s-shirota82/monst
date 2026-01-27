export type ImageData = {
  path: string;
  mimeType: string | null;
  base64: string | null;
};

export type NamedImage = {
  name: string;
  image: ImageData | null;
};

export type MonsterAbility = {
  name: string;
  stage: string | null;
};

export type MonsterFullResponse = {
  id: number;
  number: number;
  name: string;

  rarity: { value: number; maxLevel: number };
  evolutionStage: { name: string; levelCapRelease: boolean; superBattleRelease: boolean };

  attribute: NamedImage;
  hitType: string;
  tribe: string;
  battleType: string;

  status: {
    hp: { max: number; plusMax: number };
    attack: { max: number; plusMax: number };
    speed: { max: number; plusMax: number };
  };

  luckSkill: NamedImage | null;

  abilities: { base: MonsterAbility[]; gauge: MonsterAbility[] };
  connectSkill: { condition: string | null; abilities: MonsterAbility[] };

  skills: { shot: string | null; assist: string | null };
  strikeShot: { name: string | null; effect: string | null };

  friendshipCombo: {
    main: {
      name: string;
      attribute: NamedImage;
      category: string;
      description: string;
      power: number | null;
      image: ImageData | null;
    };
    sub: {
      name: string;
      attribute: NamedImage;
      category: string;
      description: string;
      power: number | null;
      image: ImageData | null;
    } | null;
  };

  series: string | null;

  images: { icon: ImageData | null; monster: ImageData | null };
};

export type MonsterFullListResponse = {
  items: MonsterFullResponse[];
  page: number;
  size: number;
  total: number;
};

export type LoginRequest = { email: string; password: string };
export type LoginResponse = { id: number; email: string; token?: string; role?: string | number };

export type RegisterRequest = { email: string; password: string; name: string };
export type RegisterResponse = { id: number; email: string; name: string; role?: string | number };
