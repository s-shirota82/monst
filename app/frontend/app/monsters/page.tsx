"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";

import { SiteHeader } from "@/components/site-header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ChevronLeft, ChevronRight, Search } from "lucide-react";

import { fetchMonsterList } from "@/lib/monsters-api";
import type { MonsterFullResponse } from "@/lib/types";
import { ApiError } from "@/lib/api-client";
import { useAuth } from "@/lib/auth-context";

function imageSrc(img: { mimeType: string | null; base64: string | null } | null | undefined) {
  if (!img) return null;
  if (img.base64 && img.mimeType) return `data:${img.mimeType};base64,${img.base64}`;
  return null;
}

// 所持キャラ（MockでOK）
const OWNED_MOCK = [
  { id: 1, number: 1, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 2, number: 2, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 5, number: 5, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 7, number: 7, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 20, number: 20, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 22, number: 22, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 25, number: 25, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 26, number: 26, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 27, number: 27, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 28, number: 28, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 29, number: 29, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 35, number: 35, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 37, number: 37, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 42, number: 42, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 54, number: 54, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 55, number: 55, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 56, number: 56, rarity: 5, ownedCountText: "1 : ★5" },
  { id: 58, number: 58, rarity: 5, ownedCountText: "1 : ★5" },
];

const ATTRIBUTE_OPTIONS: { label: string; id: number }[] = [
  { label: "火", id: 1 },
  { label: "水", id: 2 },
  { label: "木", id: 3 },
  { label: "光", id: 4 },
  { label: "闇", id: 5 },
  { label: "無", id: 6 },
];

const RARITY_OPTIONS = ["1", "2", "3", "4", "5", "6"];

export default function MonstersPage() {
  const { user, isLoading: authLoading } = useAuth();
  const isLoggedIn = !!user && !authLoading;

  const [tab, setTab] = useState<"all" | "owned">("all");

  useEffect(() => {
    if (!isLoggedIn && tab === "owned") setTab("all");
  }, [isLoggedIn, tab]);

  const [items, setItems] = useState<MonsterFullResponse[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [size] = useState(300); // 画像の表示に寄せる（1ページ大量表示）

  const [q, setQ] = useState("");
  const [rarity, setRarity] = useState<string>("");
  const [attributeId, setAttributeId] = useState<string>("");

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const queryParams = useMemo(() => {
    return {
      q: q.trim() || undefined,
      rarity: rarity ? Number(rarity) : undefined,
      attributeId: attributeId ? Number(attributeId) : undefined,
      page,
      size,
      includeImages: true,
    };
  }, [q, rarity, attributeId, page, size]);

  useEffect(() => {
    // 所持タブは Mock のままでOK（APIは叩かない）
    if (tab === "owned") return;

    let canceled = false;
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await fetchMonsterList(queryParams);
        if (canceled) return;
        setItems(res.items);
        setTotal(res.total);
      } catch (e) {
        if (canceled) return;
        if (e instanceof ApiError) setError(e.message);
        else setError("Failed to load monsters");
      } finally {
        if (!canceled) setLoading(false);
      }
    })();

    return () => {
      canceled = true;
    };
  }, [queryParams, tab]);

  const totalPages = Math.max(1, Math.ceil(total / size));
  const from = total === 0 ? 0 : page * size + 1;
  const to = Math.min((page + 1) * size, total);

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="container mx-auto px-4 py-6">
        <div className="mb-3 flex items-center justify-between">
          <div className="text-sm text-muted-foreground">
            {loading ? "読み込み中..." : `${total.toLocaleString()}体中 ${from}-${to}体を表示`}
          </div>

          <div className="flex items-center gap-2">
            <Button variant={tab === "all" ? "default" : "outline"} size="sm" onClick={() => setTab("all")}>
              全モンスター
            </Button>
            {isLoggedIn && (
              <Button variant={tab === "owned" ? "default" : "outline"} size="sm" onClick={() => setTab("owned")}>
                所持キャラ
              </Button>
            )}
          </div>
        </div>

        <div className="mb-4 flex flex-col gap-2 md:flex-row md:items-center">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              className="pl-8 w-[280px]"
              placeholder="名前で検索（q）"
              value={q}
              onChange={(e) => {
                setPage(0);
                setQ(e.target.value);
              }}
            />
          </div>

          <Select
            value={rarity}
            onValueChange={(v) => {
              setPage(0);
              setRarity(v === "all" ? "" : v);
            }}
          >
            <SelectTrigger className="w-[140px]">
              <SelectValue placeholder="レア度" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">すべて</SelectItem>
              {RARITY_OPTIONS.map((r) => (
                <SelectItem key={r} value={r}>
                  ★{r}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select
            value={attributeId}
            onValueChange={(v) => {
              setPage(0);
              setAttributeId(v === "all" ? "" : v);
            }}
          >
            <SelectTrigger className="w-[140px]">
              <SelectValue placeholder="属性" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">すべて</SelectItem>
              {ATTRIBUTE_OPTIONS.map((a) => (
                <SelectItem key={a.id} value={String(a.id)}>
                  {a.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <div className="ml-auto flex items-center gap-2">
            <Button
              variant="outline"
              size="icon"
              disabled={page <= 0 || loading}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
            >
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <div className="text-sm">
              {page + 1} / {totalPages}
            </div>
            <Button
              variant="outline"
              size="icon"
              disabled={page + 1 >= totalPages || loading}
              onClick={() => setPage((p) => p + 1)}
            >
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>

        {error && (
          <div className="mb-4 rounded-md border p-4 text-sm">
            <div className="font-semibold">エラー</div>
            <div className="text-muted-foreground">{error}</div>
          </div>
        )}

        {/* タイル表示（元の見た目に寄せる） */}
        {tab === "all" && (
          <div className="grid gap-3 [grid-template-columns:repeat(auto-fill,minmax(84px,1fr))]">
            {items.map((m) => {
              const src = imageSrc(m.images?.icon ?? null);
              const numberText = String(m.number).padStart(4, "0");

              // “未所持っぽい”薄い表示（UIだけ再現。所持判定はこの段階では不要）
              const ownedLike = m.number % 7 === 0 || m.number % 11 === 0; // 適当な疑似所持
              const opacityClass = ownedLike ? "opacity-100" : "opacity-30";

              return (
                <Link key={m.id} href={`/monsters/${m.id}`} className="block">
                  <div className={`relative overflow-hidden rounded-xl border bg-card shadow-sm ${opacityClass}`}>
                    <div className="aspect-square bg-muted">
                      {src ? (
                        // eslint-disable-next-line @next/next/no-img-element
                        <img src={src} alt={m.name} className="h-full w-full object-cover" />
                      ) : (
                        <div className="flex h-full w-full items-center justify-center text-xs text-muted-foreground">
                          no image
                        </div>
                      )}
                    </div>

                    {/* 番号バッジ */}
                    <div className="absolute left-1 top-1 rounded bg-white/90 px-1.5 py-0.5 text-xs font-semibold text-black">
                      {numberText}
                    </div>

                    {/* 下部（例: 1 : ★5 のような行） */}
                    <div className="px-2 py-1 text-center text-xs text-muted-foreground">
                      1 : ★{m.rarity?.value ?? "-"}
                    </div>
                  </div>
                </Link>
              );
            })}
          </div>
        )}

        {/* 所持キャラ（ログイン時のみタブが出る。中身は Mock のまま） */}
        {tab === "owned" && (
          <div className="grid gap-3 [grid-template-columns:repeat(auto-fill,minmax(84px,1fr))]">
            {OWNED_MOCK.map((m) => {
              const numberText = String(m.number).padStart(4, "0");

              // 所持タブは “濃い表示” にする
              return (
                <div key={m.id} className="relative overflow-hidden rounded-xl border bg-card shadow-sm">
                  <div className="aspect-square bg-muted">
                    {/* ここは Mock なので画像は public のダミーを使う（表示形式は維持） */}
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img src="/monster-character.jpg" alt={numberText} className="h-full w-full object-cover" />
                  </div>

                  <div className="absolute left-1 top-1 rounded bg-white/90 px-1.5 py-0.5 text-xs font-semibold text-black">
                    {numberText}
                  </div>

                  <div className="px-2 py-1 text-center text-xs text-muted-foreground">{m.ownedCountText}</div>
                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
}
