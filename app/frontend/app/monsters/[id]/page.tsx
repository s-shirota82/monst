"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { useParams } from "next/navigation"

import { SiteHeader } from "@/components/site-header"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { ArrowLeft } from "lucide-react"

import { fetchMonsterDetail } from "@/lib/monsters-api"
import type { MonsterFullResponse } from "@/lib/types"
import { ApiError } from "@/lib/api-client"

function imageSrc(img: { mimeType: string | null; base64: string | null; path: string } | null | undefined) {
  if (!img) return null
  if (img.base64 && img.mimeType) return `data:${img.mimeType};base64,${img.base64}`
  return null
}

export default function MonsterDetailPage() {
  const params = useParams<{ id: string }>()
  const id = Number(params.id)

  const [data, setData] = useState<MonsterFullResponse | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    let canceled = false
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const res = await fetchMonsterDetail(id)
        if (canceled) return
        setData(res)
      } catch (e) {
        if (canceled) return
        if (e instanceof ApiError) setError(e.message)
        else setError("Failed to load monster")
      } finally {
        if (!canceled) setLoading(false)
      }
    })()
    return () => {
      canceled = true
    }
  }, [id])

  const iconSrc = imageSrc(data?.images?.icon ?? null)
  const monsterSrc = imageSrc(data?.images?.monster ?? null)

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />
      <main className="container mx-auto px-4 py-8">
        <div className="mb-6 flex items-center justify-between">
          <Link href="/monsters">
            <Button variant="outline" size="sm">
              <ArrowLeft className="mr-2 h-4 w-4" />
              一覧へ
            </Button>
          </Link>
          <div className="text-sm text-muted-foreground">GET /monster/select/{`{id}`}</div>
        </div>

        {loading && <div className="text-sm text-muted-foreground">Loading...</div>}

        {error && (
          <div className="rounded-md border p-4">
            <div className="font-semibold">エラー</div>
            <div className="text-muted-foreground text-sm">{error}</div>
          </div>
        )}

        {data && (
          <div className="grid gap-6 lg:grid-cols-3">
            <Card className="p-6 lg:col-span-1">
              <div className="flex items-center gap-3">
                <div className="h-16 w-16 overflow-hidden rounded-md bg-muted flex items-center justify-center">
                  {iconSrc ? (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img src={iconSrc} alt={data.name} className="h-full w-full object-cover" />
                  ) : (
                    <div className="text-xs text-muted-foreground">no icon</div>
                  )}
                </div>
                <div className="min-w-0">
                  <div className="text-sm text-muted-foreground">No.{data.number}</div>
                  <div className="text-xl font-bold truncate">{data.name}</div>
                  <div className="mt-1 flex flex-wrap gap-2">
                    <Badge variant="secondary">★{data.rarity.value}</Badge>
                    <Badge variant="secondary">{data.attribute?.name}</Badge>
                    <Badge variant="secondary">{data.tribe}</Badge>
                    <Badge variant="secondary">{data.battleType}</Badge>
                  </div>
                </div>
              </div>

              <div className="mt-6 space-y-3 text-sm">
                <div className="flex justify-between">
                  <div className="text-muted-foreground">進化</div>
                  <div>{data.evolutionStage.name}</div>
                </div>
                <div className="flex justify-between">
                  <div className="text-muted-foreground">最大Lv</div>
                  <div>{data.rarity.maxLevel}</div>
                </div>
                <div className="flex justify-between">
                  <div className="text-muted-foreground">ラックスキル</div>
                  <div>{data.luckSkill?.name ?? "-"}</div>
                </div>
              </div>
            </Card>

            <div className="lg:col-span-2 space-y-6">
              <Card className="p-6">
                <div className="text-lg font-semibold">画像</div>
                <div className="mt-4">
                  <div className="aspect-[16/9] w-full overflow-hidden rounded-md bg-muted flex items-center justify-center">
                    {monsterSrc ? (
                      // eslint-disable-next-line @next/next/no-img-element
                      <img src={monsterSrc} alt={data.name} className="h-full w-full object-contain" />
                    ) : (
                      <div className="text-sm text-muted-foreground">no image</div>
                    )}
                  </div>
                </div>
              </Card>

              <Card className="p-6">
                <div className="text-lg font-semibold">ステータス</div>
                <div className="mt-4 grid grid-cols-3 gap-4 text-sm">
                  <div className="rounded-md bg-muted/40 p-3">
                    <div className="text-muted-foreground">HP</div>
                    <div className="font-semibold">{data.status.hp.max}</div>
                    <div className="text-xs text-muted-foreground">+{data.status.hp.plusMax}</div>
                  </div>
                  <div className="rounded-md bg-muted/40 p-3">
                    <div className="text-muted-foreground">攻撃</div>
                    <div className="font-semibold">{data.status.attack.max}</div>
                    <div className="text-xs text-muted-foreground">+{data.status.attack.plusMax}</div>
                  </div>
                  <div className="rounded-md bg-muted/40 p-3">
                    <div className="text-muted-foreground">スピード</div>
                    <div className="font-semibold">{data.status.speed.max}</div>
                    <div className="text-xs text-muted-foreground">+{data.status.speed.plusMax}</div>
                  </div>
                </div>
              </Card>

              <Card className="p-6">
                <div className="text-lg font-semibold">アビリティ</div>
                <div className="mt-3 grid gap-3 md:grid-cols-2">
                  <div className="rounded-md border p-3">
                    <div className="text-sm font-semibold">ベース</div>
                    <ul className="mt-2 space-y-1 text-sm">
                      {data.abilities.base.length === 0 && <li className="text-muted-foreground">-</li>}
                      {data.abilities.base.map((a, idx) => (
                        <li key={idx}>
                          {a.name}
                          {a.stage ? <span className="text-muted-foreground">（{a.stage}）</span> : null}
                        </li>
                      ))}
                    </ul>
                  </div>
                  <div className="rounded-md border p-3">
                    <div className="text-sm font-semibold">ゲージ</div>
                    <ul className="mt-2 space-y-1 text-sm">
                      {data.abilities.gauge.length === 0 && <li className="text-muted-foreground">-</li>}
                      {data.abilities.gauge.map((a, idx) => (
                        <li key={idx}>
                          {a.name}
                          {a.stage ? <span className="text-muted-foreground">（{a.stage}）</span> : null}
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>

                <div className="mt-4 rounded-md border p-3">
                  <div className="text-sm font-semibold">コネクトスキル</div>
                  <div className="mt-2 text-sm text-muted-foreground">発動条件: {data.connectSkill.condition ?? "-"}</div>
                  <ul className="mt-2 space-y-1 text-sm">
                    {data.connectSkill.abilities.length === 0 && <li className="text-muted-foreground">-</li>}
                    {data.connectSkill.abilities.map((a, idx) => (
                      <li key={idx}>
                        {a.name}
                        {a.stage ? <span className="text-muted-foreground">（{a.stage}）</span> : null}
                      </li>
                    ))}
                  </ul>
                </div>
              </Card>

              <Card className="p-6">
                <div className="text-lg font-semibold">友情コンボ</div>
                <div className="mt-3 grid gap-3 md:grid-cols-2">
                  <div className="rounded-md border p-3">
                    <div className="text-sm font-semibold">メイン</div>
                    <div className="mt-2 text-sm">{data.friendshipCombo.main.name}</div>
                    <div className="mt-1 text-xs text-muted-foreground">{data.friendshipCombo.main.description}</div>
                    <div className="mt-2 text-sm">威力: {data.friendshipCombo.main.power ?? "-"}</div>
                  </div>
                  <div className="rounded-md border p-3">
                    <div className="text-sm font-semibold">サブ</div>
                    {data.friendshipCombo.sub ? (
                      <>
                        <div className="mt-2 text-sm">{data.friendshipCombo.sub.name}</div>
                        <div className="mt-1 text-xs text-muted-foreground">{data.friendshipCombo.sub.description}</div>
                        <div className="mt-2 text-sm">威力: {data.friendshipCombo.sub.power ?? "-"}</div>
                      </>
                    ) : (
                      <div className="mt-2 text-sm text-muted-foreground">-</div>
                    )}
                  </div>
                </div>
              </Card>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
