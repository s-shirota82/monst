"use client"

import { useEffect, useMemo, useState } from "react"
import Link from "next/link"
import { SiteHeader } from "@/components/site-header"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ChevronLeft, ChevronRight, Search } from "lucide-react"

import { fetchMonsterList } from "@/lib/monsters-api"
import type { MonsterFullResponse } from "@/lib/types"
import { ApiError } from "@/lib/api-client"
import { useAuth } from "@/lib/auth-context"

function imageSrc(img: { mimeType: string | null; base64: string | null; path: string } | null | undefined) {
  if (!img) return null
  if (img.base64 && img.mimeType) return `data:${img.mimeType};base64,${img.base64}`
  return null
}

// 所持キャラ（モックでOK）
const OWNED_MOCK = [
  { id: "m1", number: 90001, name: "所持モンスターA", img: "/monster-character.jpg" },
  { id: "m2", number: 90002, name: "所持モンスターB", img: "/monster-.jpg" },
  { id: "m3", number: 90003, name: "所持モンスターC", img: "/game-monster-creature-no.jpg" },
]

const ATTRIBUTE_OPTIONS: { label: string; id: number }[] = [
  { label: "火", id: 1 },
  { label: "水", id: 2 },
  { label: "木", id: 3 },
  { label: "光", id: 4 },
  { label: "闇", id: 5 },
  { label: "無", id: 6 },
]

const RARITY_OPTIONS = ["1", "2", "3", "4", "5", "6"]

export default function MonstersPage() {
  const { user, isLoading: authLoading } = useAuth()
  const isLoggedIn = !!user && !authLoading

  const [tab, setTab] = useState<"all" | "owned">("all")

  // ログアウトしたら all に戻す
  useEffect(() => {
    if (!isLoggedIn && tab === "owned") setTab("all")
  }, [isLoggedIn, tab])

  // ここから下は既存の状態管理をそのまま
  const [items, setItems] = useState<MonsterFullResponse[]>([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(0)
  const [size] = useState(20)

  const [q, setQ] = useState("")
  const [rarity, setRarity] = useState<string>("")
  const [attributeId, setAttributeId] = useState<string>("")

  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const queryParams = useMemo(() => {
    return {
      q: q.trim() || undefined,
      rarity: rarity ? Number(rarity) : undefined,
      attributeId: attributeId ? Number(attributeId) : undefined,
      page,
      size,
      includeImages: true,
    }
  }, [q, rarity, attributeId, page, size])

  useEffect(() => {
    // 所持タブ中はAPIを叩かない（モックのままでOK要件）
    if (tab === "owned") return

    let canceled = false
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const res = await fetchMonsterList(queryParams)
        if (canceled) return
        setItems(res.items)
        setTotal(res.total)
      } catch (e) {
        if (canceled) return
        if (e instanceof ApiError) setError(e.message)
        else setError("Failed to load monsters")
      } finally {
        if (!canceled) setLoading(false)
      }
    })()
    return () => {
      canceled = true
    }
  }, [queryParams, tab])

  const totalPages = Math.max(1, Math.ceil(total / size))

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="container mx-auto px-4 py-8">
        <div className="mb-6 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
          <div>
            <h1 className="text-2xl font-bold">モンスター図鑑</h1>
            <p className="text-sm text-muted-foreground">GET /monster/select/all から取得します。</p>
          </div>

          {/* 追加：タブボタン（表示の仕方は変えず、ここだけ追加） */}
          <div className="flex items-center gap-2">
            <Button variant={tab === "all" ? "default" : "outline"} onClick={() => setTab("all")}>
              全モンスター
            </Button>
            {isLoggedIn && (
              <Button variant={tab === "owned" ? "default" : "outline"} onClick={() => setTab("owned")}>
                所持キャラ
              </Button>
            )}
          </div>
        </div>

        {/* 所持キャラ（ログイン時のみタブ出現、表示内容はモック） */}
        {tab === "owned" && (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {OWNED_MOCK.map((m) => (
              <Card key={m.id} className="p-4 hover:shadow-md transition-shadow">
                <div className="flex items-center gap-3">
                  <div className="h-14 w-14 overflow-hidden rounded-md bg-muted flex items-center justify-center">
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img src={m.img} alt={m.name} className="h-full w-full object-cover" />
                  </div>

                  <div className="min-w-0">
                    <div className="text-sm text-muted-foreground">No.{m.number}</div>
                    <div className="font-semibold truncate">{m.name}</div>
                    <div className="text-xs text-muted-foreground">（モック表示）</div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}

        {/* ここから下は既存の全モンスター表示を一切変更していない（tab==="all" で囲うだけ） */}
        {tab === "all" && (
          <>
            {error && (
              <div className="mb-6 rounded-md border p-4 text-sm">
                <div className="font-semibold">エラー</div>
                <div className="text-muted-foreground">{error}</div>
              </div>
            )}

            <div className="mb-6 flex flex-col gap-2 md:flex-row md:items-center">
              <div className="relative">
                <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  className="pl-8 w-[280px]"
                  placeholder="名前で検索（q）"
                  value={q}
                  onChange={(e) => {
                    setPage(0)
                    setQ(e.target.value)
                  }}
                />
              </div>

              <Select
                value={rarity}
                onValueChange={(v) => {
                  setPage(0)
                  setRarity(v === "all" ? "" : v)
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
                  setPage(0)
                  setAttributeId(v === "all" ? "" : v)
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
            </div>

            <div className="mb-4 flex items-center justify-between">
              <div className="text-sm text-muted-foreground">{loading ? "Loading..." : `${total} 件`}</div>

              <div className="flex items-center gap-2">
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

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {items.map((m) => {
                const iconSrc = imageSrc(m.images?.icon ?? null)
                return (
                  <Link key={m.id} href={`/monsters/${m.id}`} className="block">
                    <Card className="p-4 hover:shadow-md transition-shadow">
                      <div className="flex items-center gap-3">
                        <div className="h-14 w-14 overflow-hidden rounded-md bg-muted flex items-center justify-center">
                          {iconSrc ? (
                            // eslint-disable-next-line @next/next/no-img-element
                            <img src={iconSrc} alt={m.name} className="h-full w-full object-cover" />
                          ) : (
                            <div className="text-xs text-muted-foreground">no image</div>
                          )}
                        </div>

                        <div className="min-w-0">
                          <div className="text-sm text-muted-foreground">No.{m.number}</div>
                          <div className="font-semibold truncate">{m.name}</div>
                          <div className="text-xs text-muted-foreground">
                            {m.attribute?.name} / {m.tribe} / {m.battleType}
                          </div>
                        </div>
                      </div>
                    </Card>
                  </Link>
                )
              })}
            </div>
          </>
        )}
      </main>
    </div>
  )
}
