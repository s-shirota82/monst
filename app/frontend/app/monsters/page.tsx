import { Card } from "@/components/ui/card"
import { SiteHeader } from "@/components/site-header"
import { Badge } from "@/components/ui/badge"
import { Star, Flame, Droplet, Zap, Leaf } from "lucide-react"

// モンスターのタイプを定義
const elementIcons = {
  fire: { icon: Flame, color: "text-red-500" },
  water: { icon: Droplet, color: "text-blue-500" },
  thunder: { icon: Zap, color: "text-yellow-500" },
  wood: { icon: Leaf, color: "text-green-500" },
}

// サンプルモンスターデータ
const monsters = [
  { id: 1, name: "炎の守護神", element: "fire", rarity: 5, type: "バランス" },
  { id: 2, name: "水龍", element: "water", rarity: 5, type: "パワー" },
  { id: 3, name: "雷獣", element: "thunder", rarity: 4, type: "スピード" },
  { id: 4, name: "森の精霊", element: "wood", rarity: 4, type: "バランス" },
  { id: 5, name: "紅蓮の戦士", element: "fire", rarity: 5, type: "パワー" },
  { id: 6, name: "氷雪の女王", element: "water", rarity: 5, type: "バランス" },
  { id: 7, name: "電光の使者", element: "thunder", rarity: 4, type: "スピード" },
  { id: 8, name: "大地の巨人", element: "wood", rarity: 5, type: "パワー" },
  { id: 9, name: "火焔鳥", element: "fire", rarity: 4, type: "スピード" },
  { id: 10, name: "深海の王", element: "water", rarity: 4, type: "バランス" },
]

export default function MonstersPage() {
  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      {/* Page Header */}
      <section className="border-b border-border bg-muted/30 py-8 md:py-12">
        <div className="container mx-auto max-w-7xl px-4">
          <h1 className="text-balance text-3xl font-bold md:text-4xl lg:text-5xl">モンスター図鑑</h1>
          <p className="mt-2 text-pretty text-muted-foreground md:text-lg">
            全モンスターの詳細情報・ステータス・評価をチェック
          </p>
        </div>
      </section>

      {/* Filters */}
      <section className="container mx-auto max-w-7xl px-4 py-6">
        <div className="flex flex-wrap gap-2">
          <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
            すべて
          </Badge>
          <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
            <Flame className="mr-1 h-3 w-3" />
            火属性
          </Badge>
          <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
            <Droplet className="mr-1 h-3 w-3" />
            水属性
          </Badge>
          <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
            <Zap className="mr-1 h-3 w-3" />
            雷属性
          </Badge>
          <Badge variant="outline" className="cursor-pointer hover:bg-primary hover:text-primary-foreground">
            <Leaf className="mr-1 h-3 w-3" />
            木属性
          </Badge>
        </div>
      </section>

      {/* Monster Grid */}
      <section className="container mx-auto max-w-7xl px-4 py-6">
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 lg:gap-5">
          {monsters.map((monster) => {
            const elementData = elementIcons[monster.element as keyof typeof elementIcons]
            const ElementIcon = elementData.icon

            return (
              <Card
                key={monster.id}
                className="group cursor-pointer overflow-hidden transition-all hover:shadow-lg hover:border-primary"
              >
                <div className="relative aspect-square bg-gradient-to-br from-muted to-muted/50">
                  <img
                    src={`/monster-.jpg?height=200&width=200&query=monster+${monster.element}+${monster.id}`}
                    alt={monster.name}
                    className="h-full w-full object-cover transition-transform group-hover:scale-105"
                  />
                  <div className="absolute right-2 top-2 rounded-full bg-background/80 p-1.5 backdrop-blur-sm">
                    <ElementIcon className={`h-4 w-4 ${elementData.color}`} />
                  </div>
                </div>
                <div className="p-3">
                  <div className="mb-2 flex items-center justify-between">
                    <div className="flex gap-0.5">
                      {[...Array(monster.rarity)].map((_, i) => (
                        <Star key={i} className="h-3 w-3 fill-yellow-400 text-yellow-400" />
                      ))}
                    </div>
                    <Badge variant="secondary" className="text-xs">
                      {monster.type}
                    </Badge>
                  </div>
                  <h3 className="text-balance text-sm font-semibold leading-snug">{monster.name}</h3>
                </div>
              </Card>
            )
          })}
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border bg-muted/50 py-8">
        <div className="container mx-auto max-w-7xl px-4 text-center">
          <div className="text-sm text-muted-foreground">
            <p>© 2025 モンスト攻略サイト</p>
            <p className="mt-2">当サイトはモンスターストライクの攻略情報を提供する非公式サイトです</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
