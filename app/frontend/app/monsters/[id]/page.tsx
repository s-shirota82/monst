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
    race: "神族",
    evolution: 3,
    hp: 18500,
    atk: 22000,
    spd: 320,
    ability: "火属性ダメージ軽減",
    abilityDesc: "火属性からのダメージを30%軽減",
    gaugeAbility: "水属性ダメージ軽減",
    gaugeAbilityDesc: "水属性からのダメージを20%軽減",
    connectSkill: "炎の連携",
    connectSkillDesc: "味方の火属性攻撃力を50%アップ",
    connectCondition: "自分を含む2体以上でタッチ",
    shotSkill: "紅蓮の炎",
    shotSkillDesc: "火属性の超強力な攻撃を放つ",
    assistSkill: "火属性強化サポート",
    assistSkillDesc: "装備したモンスターの火属性攻撃力を30%アップ",
    friendCombo: "クロスレーザーEL",
    friendComboDesc: "十字方向にレーザー攻撃（威力：15000）",
    subFriendCombo: "超爆発",
    subFriendComboDesc: "広範囲に火属性ダメージ（威力：8000）",
  },
  "2": {
    id: 2,
    dexNo: "002",
    name: "水龍",
    element: "water",
    rarity: 5,
    type: "パワー",
    race: "ドラゴン族",
    evolution: 3,
    hp: 17800,
    atk: 25000,
    spd: 280,
    ability: "水属性強化",
    abilityDesc: "水属性の攻撃力が50%アップ",
    gaugeAbility: "雷属性ダメージ軽減",
    gaugeAbilityDesc: "雷属性からのダメージを25%軽減",
    connectSkill: "水龍の加護",
    connectSkillDesc: "味方全体のHPを5000回復",
    connectCondition: "自分を含む3体でタッチ",
    shotSkill: "水龍の咆哮",
    shotSkillDesc: "水属性の全体攻撃を放つ",
    assistSkill: "水属性防御サポート",
    assistSkillDesc: "装備したモンスターの水属性耐性を30%アップ",
    friendCombo: "超強貫通ホーミング",
    friendComboDesc: "敵を貫通する追尾弾（威力：12000）",
    subFriendCombo: "回復",
    subFriendComboDesc: "味方全体のHPを3000回復",
  },
  "3": {
    id: 3,
    dexNo: "003",
    name: "雷獣",
    element: "thunder",
    rarity: 4,
    type: "スピード",
    race: "獣族",
    evolution: 2,
    hp: 15000,
    atk: 18000,
    spd: 380,
    ability: "スピードアップ",
    abilityDesc: "移動速度が30%アップ",
    gaugeAbility: "木属性ダメージ軽減",
    gaugeAbilityDesc: "木属性からのダメージを15%軽減",
    connectSkill: "電光石火",
    connectSkillDesc: "自身のスピードを100アップ",
    connectCondition: "自分のみでタッチ",
    shotSkill: "稲妻斬り",
    shotSkillDesc: "雷属性の高速攻撃",
    assistSkill: "スピードサポート",
    assistSkillDesc: "装備したモンスターのスピードを50アップ",
    friendCombo: "電撃",
    friendComboDesc: "周囲の敵に雷属性ダメージ（威力：8000）",
    subFriendCombo: null,
    subFriendComboDesc: null,
  },
  "4": {
    id: 4,
    dexNo: "004",
    name: "森の精霊",
    element: "wood",
    rarity: 4,
    type: "バランス",
    race: "妖精族",
    evolution: 2,
    hp: 16000,
    atk: 17000,
    spd: 310,
    ability: "回復ブースト",
    abilityDesc: "回復量が20%アップ",
    gaugeAbility: "火属性ダメージ軽減",
    gaugeAbilityDesc: "火属性からのダメージを20%軽減",
    connectSkill: "森の祝福",
    connectSkillDesc: "味方全体のHPを3000回復",
    connectCondition: "自分を含む2体以上でタッチ",
    shotSkill: "森の癒し",
    shotSkillDesc: "味方全体のHPを回復",
    assistSkill: "回復サポート",
    assistSkillDesc: "装備したモンスターの回復量を25%アップ",
    friendCombo: "回復",
    friendComboDesc: "味方全体のHPを2500回復",
    subFriendCombo: "毒",
    subFriendComboDesc: "敵に毒の状態異常を付与",
  },
  "5": {
    id: 5,
    dexNo: "005",
    name: "紅蓮の戦士",
    element: "fire",
    rarity: 5,
    type: "パワー",
    race: "魔族",
    evolution: 3,
    hp: 16500,
    atk: 26000,
    spd: 290,
    ability: "火属性キラー",
    abilityDesc: "火属性に対して特効",
    gaugeAbility: "水属性ダメージ軽減",
    gaugeAbilityDesc: "水属性からのダメージを30%軽減",
    connectSkill: "炎の激励",
    connectSkillDesc: "味方の攻撃力を1000アップ",
    connectCondition: "自分を含む2体でタッチ",
    shotSkill: "炎帝の剣",
    shotSkillDesc: "火属性の極大攻撃",
    assistSkill: "攻撃力サポート",
    assistSkillDesc: "装備したモンスターの攻撃力を2000アップ",
    friendCombo: "超爆発",
    friendComboDesc: "広範囲に火属性ダメージ（威力：18000）",
    subFriendCombo: "貫通レーザーL",
    subFriendComboDesc: "前方に貫通レーザー（威力：10000）",
  },
  "6": {
    id: 6,
    dexNo: "006",
    name: "氷雪の女王",
    element: "water",
    rarity: 5,
    type: "バランス",
    race: "神族",
    evolution: 3,
    hp: 18000,
    atk: 21000,
    spd: 315,
    ability: "状態異常無効",
    abilityDesc: "毒・麻痺を無効化",
    gaugeAbility: "雷属性ダメージ軽減",
    gaugeAbilityDesc: "雷属性からのダメージを20%軽減",
    connectSkill: "氷結の護り",
    connectSkillDesc: "味方全体の防御力を50%アップ",
    connectCondition: "自分を含む3体以上でタッチ",
    shotSkill: "絶対零度",
    shotSkillDesc: "水属性の氷結攻撃",
    assistSkill: "状態異常耐性サポート",
    assistSkillDesc: "装備したモンスターの状態異常耐性アップ",
    friendCombo: "氷結",
    friendComboDesc: "周囲の敵を氷結（威力：10000）",
    subFriendCombo: null,
    subFriendComboDesc: null,
  },
  "7": {
    id: 7,
    dexNo: "007",
    name: "電光の使者",
    element: "thunder",
    rarity: 4,
    type: "スピード",
    race: "魔族",
    evolution: 2,
    hp: 14500,
    atk: 19000,
    spd: 390,
    ability: "クリティカル",
    abilityDesc: "クリティカル率20%アップ",
    gaugeAbility: "木属性ダメージ軽減",
    gaugeAbilityDesc: "木属性からのダメージを18%軽減",
    connectSkill: "雷鳴の呼び声",
    connectSkillDesc: "雷属性攻撃力を40%アップ",
    connectCondition: "自分を含む2体以上でタッチ",
    shotSkill: "雷光一閃",
    shotSkillDesc: "雷属性の連続攻撃",
    assistSkill: "クリティカルサポート",
    assistSkillDesc: "装備したモンスターのクリティカル率10%アップ",
    friendCombo: "連撃",
    friendComboDesc: "前方に連続攻撃（威力：7000×3）",
    subFriendCombo: "電撃",
    subFriendComboDesc: "周囲の敵に雷属性ダメージ（威力：5000）",
  },
  "8": {
    id: 8,
    dexNo: "008",
    name: "大地の巨人",
    element: "wood",
    rarity: 5,
    type: "パワー",
    race: "魔人族",
    evolution: 3,
    hp: 21000,
    atk: 24000,
    spd: 260,
    ability: "ガードアップ",
    abilityDesc: "防御力が40%アップ",
    gaugeAbility: "火属性ダメージ軽減",
    gaugeAbilityDesc: "火属性からのダメージを25%軽減",
    connectSkill: "大地の力",
    connectSkillDesc: "味方全体の攻撃力と防御力を30%アップ",
    connectCondition: "自分を含む4体でタッチ",
    shotSkill: "大地の怒り",
    shotSkillDesc: "木属性の超攻撃",
    assistSkill: "防御力サポート",
    assistSkillDesc: "装備したモンスターの防御力を50%アップ",
    friendCombo: "地雷",
    friendComboDesc: "敵の足元に地雷を設置（威力：20000）",
    subFriendCombo: "超爆発",
    subFriendComboDesc: "広範囲に木属性ダメージ（威力：12000）",
  },
  "9": {
    id: 9,
    dexNo: "009",
    name: "火焔鳥",
    element: "fire",
    rarity: 4,
    type: "スピード",
    race: "獣族",
    evolution: 2,
    hp: 14000,
    atk: 20000,
    spd: 370,
    ability: "飛行",
    abilityDesc: "地雷を無視して移動",
    gaugeAbility: "水属性ダメージ軽減",
    gaugeAbilityDesc: "水属性からのダメージを15%軽減",
    connectSkill: "炎の翼",
    connectSkillDesc: "自身のスピードと攻撃力を30%アップ",
    connectCondition: "自分のみでタッチ",
    shotSkill: "炎の翼",
    shotSkillDesc: "火属性の範囲攻撃",
    assistSkill: "飛行サポート",
    assistSkillDesc: "装備したモンスターに飛行能力を付与",
    friendCombo: "貫通ホーミング",
    friendComboDesc: "敵を貫通する追尾弾（威力：9000）",
    subFriendCombo: null,
    subFriendComboDesc: null,
  },
  "10": {
    id: 10,
    dexNo: "010",
    name: "深海の王",
    element: "water",
    rarity: 4,
    type: "バランス",
    race: "海獣族",
    evolution: 2,
    hp: 17000,
    atk: 18500,
    spd: 300,
    ability: "アンチ重力バリア",
    abilityDesc: "重力バリアを無効化",
    gaugeAbility: "雷属性ダメージ軽減",
    gaugeAbilityDesc: "雷属性からのダメージを22%軽減",
    connectSkill: "深海の加護",
    connectSkillDesc: "味方全体のHPを4000回復",
    connectCondition: "自分を含む3体でタッチ",
    shotSkill: "深海の渦",
    shotSkillDesc: "水属性の引き寄せ攻撃",
    assistSkill: "ギミック対策サポート",
    assistSkillDesc: "装備したモンスターに重力バリア無効を付与",
    friendCombo: "ワープ",
    friendComboDesc: "ランダムな敵にワープ攻撃（威力：11000）",
    subFriendCombo: "超強貫通ホーミング",
    subFriendComboDesc: "敵を貫通する強力な追尾弾（威力：8000）",
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
                  <Badge variant="outline">{monster.race}</Badge>
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
          {/* Ability */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">アビリティ</h2>
            <div className="space-y-2">
              <div className="text-sm text-muted-foreground">{monster.ability}</div>
              <p className="text-base">{monster.abilityDesc}</p>
            </div>
            <div className="mt-4 space-y-2 border-t pt-4">
              <div className="text-sm text-muted-foreground">ゲージアビリティ: {monster.gaugeAbility}</div>
              <p className="text-base">{monster.gaugeAbilityDesc}</p>
            </div>
          </Card>

          {/* Connect Skill */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">コネクトスキル</h2>
            <div className="space-y-2">
              <div className="text-sm text-muted-foreground">{monster.connectSkill}</div>
              <p className="text-base">{monster.connectSkillDesc}</p>
            </div>
            <div className="mt-3 rounded-lg bg-muted p-3">
              <p className="text-sm">
                <span className="font-medium">発動条件: </span>
                <span className="text-muted-foreground">{monster.connectCondition}</span>
              </p>
            </div>
          </Card>

          {/* Shot Skill */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">ショットスキル</h2>
            <div className="space-y-2">
              <div className="text-sm text-muted-foreground">{monster.shotSkill}</div>
              <p className="text-base">{monster.shotSkillDesc}</p>
            </div>
          </Card>

          {/* Assist Skill */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">アシストスキル</h2>
            <div className="space-y-2">
              <div className="text-sm text-muted-foreground">{monster.assistSkill}</div>
              <p className="text-base">{monster.assistSkillDesc}</p>
            </div>
          </Card>

          {/* Friend Combo */}
          <Card className="p-6">
            <h2 className="mb-4 text-xl font-bold">友情コンボ</h2>
            <div className="space-y-2">
              <div className="text-sm text-muted-foreground">{monster.friendCombo}</div>
              <p className="text-base">{monster.friendComboDesc}</p>
            </div>
            {monster.subFriendCombo && (
              <div className="mt-4 space-y-2 border-t pt-4">
                <div className="text-sm text-muted-foreground">副友情コンボ: {monster.subFriendCombo}</div>
                <p className="text-base">{monster.subFriendComboDesc}</p>
              </div>
            )}
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
                クエストで活躍できる性能を持つ。アビリティ「{monster.ability}」は非常に有用で、コネクトスキル「
                {monster.connectSkill}」も強力です。ショットスキル「{monster.shotSkill}
                」は攻撃力に優れ、アシストスキル「{monster.assistSkill}」は味方をサポートします。友情コンボ「
                {monster.friendCombo}」は戦略に活用できる強力な攻撃です。副友情コンボ「
                {monster.subFriendCombo}」もまた戦略に活用できる攻撃です。
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
