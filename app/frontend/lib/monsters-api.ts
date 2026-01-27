import { apiFetch } from "./api-client";
import type { MonsterFullListResponse, MonsterFullResponse } from "./types";

export type MonsterListParams = {
  q?: string;
  rarity?: number;
  attributeId?: number;
  tribeId?: number;
  battleTypeId?: number;
  page?: number;
  size?: number;
  includeImages?: boolean;
};

export async function fetchMonsterList(params: MonsterListParams = {}): Promise<MonsterFullListResponse> {
  const sp = new URLSearchParams();
  if (params.q) sp.set("q", params.q);
  if (params.rarity != null) sp.set("rarity", String(params.rarity));
  if (params.attributeId != null) sp.set("attributeId", String(params.attributeId));
  if (params.tribeId != null) sp.set("tribeId", String(params.tribeId));
  if (params.battleTypeId != null) sp.set("battleTypeId", String(params.battleTypeId));
  sp.set("page", String(params.page ?? 0));
  sp.set("size", String(params.size ?? 20));
  sp.set("includeImages", String(params.includeImages ?? true));

  return apiFetch<MonsterFullListResponse>(`/monster/select/all?${sp.toString()}`, { method: "GET" });
}

export async function fetchMonsterDetail(id: number): Promise<MonsterFullResponse> {
  return apiFetch<MonsterFullResponse>(`/monster/select/${id}`, { method: "GET" });
}
