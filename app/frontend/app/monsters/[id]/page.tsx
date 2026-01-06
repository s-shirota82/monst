import { SiteHeader } from "@/components/site-header"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Star, Flame, Droplet, Zap, Leaf, ArrowLeft, Heart, Sword, Sparkles } from "lucide-react"
import Link from "next/link"

const elementIcons = {
  fire: { icon: Flame, color: "text-red-500", bgColor: "bg-red-500/10" },
  water: { icon: Droplet, color: "text-blue-500", bgColor: "bg-blue-500/10" },
  thunder: { icon: Zap, color: "text-yellow-500", bgColor: "bg-yellow-500/10" },
  wood: { icon: Leaf, color: "text-green-500", bgColor: "bg-green-500/10" },
}

// モンスターデータ（実際にはAPIから取得）
const monstersData: Record<string, any> = {
  "1": {
    id: 1,
    dexNo: "001",
    name: "炎の守護神",
    element: "fire",
    rarity: 5,
    type: "バランス",
    evolution: 3,
    hp: 18500,
    atk: 22000,
    spd: 320,
    skill: "紅蓮の炎",
    skillDesc: "火属性の超強力な攻撃を放つ",
    ability: "火属性ダメージ軽減",
    abilityDesc: "火属性からのダメージを30%軽減",
  },
  "2": {
    id: 2,
    dexNo: "002",
    name: "水龍",
    element: "water",
    rarity: 5,
    type: "パワー",
    evolution: 3,
    hp: 17800,
    atk: 25000,
    spd: 280,
    skill: "水龍の咆哮",
    skillDesc: "水属性の全体攻撃を放つ",
    ability: "水属性強化",
    abilityDesc: "水属性の攻撃力が50%アップ",
  },
  "3": {
    id: 3,
    dexNo: "003",
    name: "雷獣",
    element: "thunder",
    rarity: 4,
    type: "スピード",
    evolution: 2,
    hp: 15000,
    atk: 18000,
    spd: 380,
    skill: "稲妻斬り",
    skillDesc: "雷属性の高速攻撃",
    ability: "スピードアップ",
    abilityDesc: "移動速度が30%アップ",
  },
  "4": {
    id: 4,
    dexNo: "004",
    name: "森の精霊",
    element: "wood",
    rarity: 4,
    type: "バランス",
    evolution: 2,
    hp: 16000,
    atk: 17000,
    spd: 310,
    skill: "森の癒し",
    skillDesc: "味方全体のHPを回復",
    ability: "回復ブースト",
    abilityDesc: "回復量が20%アップ",
  },
  "5": {
    id: 5,
    dexNo: "005",
    name: "紅蓮の戦士",
    element: "fire",
    rarity: 5,
    type: "パワー",
    evolution: 3,
    hp: 16500,
    atk: 26000,
    spd: 290,
    skill: "炎帝の剣",
    skillDesc: "火属性の極大攻撃",
    ability: "火属性キラー",
    abilityDesc: "火属性に対して特効",
  },
  "6": {
    id: 6,
    dexNo: "006",
    name: "氷雪の女王",
    element: "water",
    rarity: 5,
    type: "バランス",
    evolution: 3,
    hp: 18000,
    atk: 21000,
    spd: 315,
    skill: "絶対零度",
    skillDesc: "水属性の氷結攻撃",
    ability: "状態異常無効",
    abilityDesc: "毒・麻痺を無効化",
  },
  "7": {
    id: 7,
    dexNo: "007",
    name: "電光の使者",
    element: "thunder",
    rarity: 4,
    type: "スピード",
    evolution: 2,
    hp: 14500,
    atk: 19000,
    spd: 390,
    skill: "雷光一閃",
    skillDesc: "雷属性の連続攻撃",
    ability: "クリティカル",
    abilityDesc: "クリティカル率20%アップ",
  },
  "8": {
    id: 8,
    dexNo: "008",
    name: "大地の巨人",
    element: "wood",
    rarity: 5,
    type: "パワー",
    evolution: 3,
    hp: 21000,
    atk: 24000,
    spd: 260,
    skill: "大地の怒り",
    skillDesc: "木属性の超攻撃",
    ability: "ガードアップ",
    abilityDesc: "防御力が40%アップ",
  },
  "9": {
    id: 9,
    dexNo: "009",
    name: "火焔鳥",
    element: "fire",
    rarity: 4,
    type: "スピード",
    evolution: 2,
    hp: 14000,
    atk: 20000,
    spd: 370,
    skill: "炎の翼",
    skillDesc: "火属性の範囲攻撃",
    ability: "飛行",
    abilityDesc: "地雷を無視して移動",
  },
  "10": {
    id: 10,
    dexNo: "010",
    name: "深海の王",
    element: "water",
    rarity: 4,
    type: "バランス",
    evolution: 2,
    hp: 17000,
    atk: 18500,
    spd: 300,
    skill: "深海の渦",
    skillDesc: "水属性の引き寄せ攻撃",
    ability: "アンチ重力バリア",
    abilityDesc: "重力バリアを無効化",
  },
}

export default async function MonsterDetailPage({
  params,
}: {
  params: Promise<{ id: string }>
}) {
  const { id } = await params
  const monster = monstersData[id]

  if (!monster) {
    return (
      <div className="min-h-screen bg-background">
        <SiteHeader />
        <div className="container mx-auto max-w-4xl px-4 py-12 text-center">
          <h1 className="text-2xl font-bold">モンスターが見つかりません</h1>
          <Link href="/monsters">
            <Button className="mt-4">図鑑に戻る</Button>
          </Link>
        </div>
      </div>
    )
  }

  const elementData = elementIcons[monster.element as keyof typeof elementIcons]
  const ElementIcon = elementData.icon

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      {/* Back Button */}
      <div className="border-b border-border bg-muted/30">
        <div className="container mx-auto max-w-4xl px-4 py-4">
          <Link href="/monsters">
            <Button variant="ghost" size="sm">
              <ArrowLeft className="mr-2 h-4 w-4" />
              図鑑に戻る
            </Button>
          </Link>
        </div>
      </div>

      {/* Monster Header */}
      <section className="border-b border-border bg-gradient-to-b from-muted/50 to-background py-8 md:py-12">
        <div className="container mx-auto max-w-4xl px-4">
          <div className="flex flex-col gap-6 md:flex-row md:items-start md:gap-8">
            {/* Monster Image */}
            <div className="relative mx-auto w-full max-w-sm md:mx-0 md:w-80">
              <div className={`aspect-square overflow-hidden rounded-lg border-2 ${elementData.bgColor}`}>
                <img
                  src={`/monster-.jpg?height=400&width=400&query=monster+${monster.element}+${monster.id}+legendary`}
                  alt={monster.name}
                  className="h-full w-full object-cover"
                />
              </div>
              <div className="absolute left-3 top-3 rounded-lg bg-background/95 px-3 py-1.5 text-sm font-bold backdrop-blur-sm">
                No.{monster.dexNo}
              </div>
            </div>

            {/* Monster Info */}
            <div className="flex-1 space-y-4">
              <div>
                <div className="mb-2 flex items-center gap-2">
                  <div className={`rounded-full ${elementData.bgColor} p-2`}>
                    <ElementIcon className={`h-6 w-6 ${elementData.color}`} />
                  </div>
                  <Badge variant="secondary">{monster.type}</Badge>
                  <div className="flex gap-0.5">
                    {[...Array(monster.rarity)].map((_, i) => (
                      <Star key={i} className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                    ))}
                  </div>
                </div>
                <h1 className="text-balance text-3xl font-bold md:text-4xl">{monster.name}</h1>
                <p className="mt-2 text-muted-foreground">進化段階: {monster.evolution}段階</p>
              </div>

              {/* Stats */}
              <Card className="p-4">
                <h2 className="mb-3 font-semibold">ステータス</h2>
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <Heart className="h-4 w-4 text-red-500" />
                      <span className="text-sm text-muted-foreground">HP</span>
                    </div>
                    <span className="font-semibold">{monster.hp.toLocaleString()}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <Sword className="h-4 w-4 text-orange-500" />
                      <span className="text-sm text-muted-foreground">攻撃力</span>
                    </div>
                    <span className="font-semibold">{monster.atk.toLocaleString()}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <Sparkles className="h-4 w-4 text-blue-500" />
                      <span className="text-sm text-muted-foreground">スピード</span>
                    </div>
                    <span className="font-semibold">{monster.spd}</span>
                  </div>
                </div>
              </Card>
            </div>
          </div>
        </div>
      </section>

      {/* Skills & Abilities */}
      <section className="py-8">
        <div className="container mx-auto max-w-4xl space-y-6 px-4">
          {/* Skill */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">ストライクショット</h2>
            <div className="space-y-2">
              <h3 className="font-semibold text-primary">{monster.skill}</h3>
              <p className="text-muted-foreground">{monster.skillDesc}</p>
            </div>
          </Card>

          {/* Ability */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">アビリティ</h2>
            <div className="space-y-2">
              <h3 className="font-semibold text-primary">{monster.ability}</h3>
              <p className="text-muted-foreground">{monster.abilityDesc}</p>
            </div>
          </Card>

          {/* Evaluation */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">総合評価</h2>
            <div className="space-y-4">
              <div className="flex items-center gap-4">
                <span className="text-sm font-medium">総合スコア</span>
                <div className="flex gap-0.5">
                  {[...Array(monster.rarity)].map((_, i) => (
                    <Star key={i} className="h-5 w-5 fill-yellow-400 text-yellow-400" />
                  ))}
                </div>
              </div>
              <p className="text-pretty text-sm leading-relaxed text-muted-foreground">
                {monster.type}タイプの優秀なモンスター。
                {monster.element === "fire"
                  ? "火属性"
                  : monster.element === "water"
                    ? "水属性"
                    : monster.element === "thunder"
                      ? "雷属性"
                      : "木属性"}
                クエストで活躍できる性能を持つ。ストライクショット「{monster.skill}」は強力で、アビリティ「
                {monster.ability}」も非常に有用。
              </p>
            </div>
          </Card>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border bg-muted/50 py-8">
        <div className="container mx-auto max-w-4xl px-4 text-center">
          <div className="text-sm text-muted-foreground">
            <p>© 2025 モンスト攻略サイト</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
