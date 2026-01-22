"use client"

import { Card } from "@/components/ui/card"
import { SiteHeader } from "@/components/site-header"
import { Button } from "@/components/ui/button"
import { Flame, Droplet, Zap, Leaf, ChevronLeft, ChevronRight, Search } from "lucide-react"
import Link from "next/link"
import { useState, useMemo } from "react"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { Checkbox } from "@/components/ui/checkbox"
import { Label } from "@/components/ui/label"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"

// モンスターのタイプを定義
const elementIcons = {
  fire: { icon: Flame, color: "text-red-500" },
  water: { icon: Droplet, color: "text-blue-500" },
  thunder: { icon: Zap, color: "text-yellow-500" },
  wood: { icon: Leaf, color: "text-green-500" },
}

const races = ["神族", "ドラゴン族", "獣族", "魔族", "妖精族"]
const battleTypes = ["バランス", "スピード", "パワー"]
const abilities = [
  "火属性ダメージ軽減",
  "水属性ダメージ軽減",
  "雷属性ダメージ軽減",
  "木属性ダメージ軽減",
  "アンチワープ",
  "アンチ減速壁",
]
const friendshipCombos = ["貫通", "ホーミング", "レーザー", "十字レーザー", "爆発", "ワンウェイレーザー"]
const evolutionStages = ["2段階", "3段階"]
const seriesList = ["神獣シリーズ", "英雄シリーズ", "三国志シリーズ", "ドラゴンシリーズ", "コラボシリーズ"]

const generateMonsters = () => {
  const elements = ["fire", "water", "thunder", "wood"] as const

  const monsters = []

  for (let i = 1; i <= 9000; i++) {
    monsters.push({
      id: i,
      dexNo: String(i).padStart(4, "0"),
      name: `モンスター${i}`,
      element: elements[Math.floor(Math.random() * elements.length)],
      rarity: Math.floor(Math.random() * 2) + 4, // 4 or 5
      type: battleTypes[Math.floor(Math.random() * battleTypes.length)],
      race: races[Math.floor(Math.random() * races.length)],
      evolution: evolutionStages[Math.floor(Math.random() * evolutionStages.length)],
      ability: abilities[Math.floor(Math.random() * abilities.length)],
      gaugeAbility: Math.random() > 0.5 ? abilities[Math.floor(Math.random() * abilities.length)] : null,
      connectSkill: Math.random() > 0.5 ? abilities[Math.floor(Math.random() * abilities.length)] : null,
      friendshipCombo: friendshipCombos[Math.floor(Math.random() * friendshipCombos.length)],
      series: seriesList[Math.floor(Math.random() * seriesList.length)],
      strikeShot: `ストライクショット${i}`,
      shotSkill: `ショットスキル${i}`,
      assistSkill: `アシストスキル${i}`,
      owned: Math.random() > 0.7,
    })
  }

  return monsters
}

const allMonsters = generateMonsters()
const ALL_ITEMS_PER_PAGE = 300
const OWNED_ITEMS_PER_PAGE = 300

export default function MonstersPage() {
  const [activeTab, setActiveTab] = useState("all")
  const [currentPage, setCurrentPage] = useState(1)
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedElement, setSelectedElement] = useState<string | null>(null)
  const [selectedRace, setSelectedRace] = useState<string | null>(null)
  const [selectedType, setSelectedType] = useState<string | null>(null)
  const [selectedRarity, setSelectedRarity] = useState<string | null>(null)
  const [selectedAbilities, setSelectedAbilities] = useState<string[]>([])
  const [selectedFriendshipCombo, setSelectedFriendshipCombo] = useState<string | null>(null)
  const [selectedEvolution, setSelectedEvolution] = useState<string | null>(null)
  const [selectedSeries, setSelectedSeries] = useState<string | null>(null)

  const filteredMonsters = useMemo(() => {
    let result = [...allMonsters]

    if (searchQuery.trim()) {
      const query = searchQuery.toLowerCase()
      result = result.filter(
        (m) =>
          m.name.toLowerCase().includes(query) ||
          m.strikeShot.toLowerCase().includes(query) ||
          m.shotSkill.toLowerCase().includes(query) ||
          m.assistSkill.toLowerCase().includes(query),
      )
    }

    if (selectedElement) {
      result = result.filter((m) => m.element === selectedElement)
    }
    if (selectedRace) {
      result = result.filter((m) => m.race === selectedRace)
    }
    if (selectedType) {
      result = result.filter((m) => m.type === selectedType)
    }
    if (selectedRarity) {
      result = result.filter((m) => m.rarity === Number.parseInt(selectedRarity))
    }
    if (selectedAbilities.length > 0) {
      result = result.filter((m) => {
        const monsterAbilities = [m.ability, m.gaugeAbility, m.connectSkill].filter(Boolean)
        return selectedAbilities.every((selectedAbility) => monsterAbilities.includes(selectedAbility))
      })
    }
    if (selectedFriendshipCombo) {
      result = result.filter((m) => m.friendshipCombo === selectedFriendshipCombo)
    }
    if (selectedEvolution) {
      result = result.filter((m) => m.evolution === selectedEvolution)
    }
    if (selectedSeries) {
      result = result.filter((m) => m.series === selectedSeries)
    }

    return result
  }, [
    searchQuery,
    selectedElement,
    selectedRace,
    selectedType,
    selectedRarity,
    selectedAbilities,
    selectedFriendshipCombo,
    selectedEvolution,
    selectedSeries,
  ])

  const itemsPerPage = activeTab === "all" ? ALL_ITEMS_PER_PAGE : OWNED_ITEMS_PER_PAGE

  const totalPages = Math.ceil(filteredMonsters.length / itemsPerPage)
  const startIndex = (currentPage - 1) * itemsPerPage
  const endIndex = startIndex + itemsPerPage
  const currentMonsters = filteredMonsters.slice(startIndex, endIndex)

  const handleFilterChange = () => {
    setCurrentPage(1)
  }

  const handleTabChange = (value: string) => {
    setActiveTab(value)
    setCurrentPage(1)
  }

  const toggleAbility = (ability: string) => {
    setSelectedAbilities((prev) => {
      if (prev.includes(ability)) {
        return prev.filter((a) => a !== ability)
      }
      return [...prev, ability]
    })
    handleFilterChange()
  }

  const getPageNumbers = () => {
    const pages = []
    const maxVisible = 5

    if (totalPages <= maxVisible) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i)
      }
    } else {
      if (currentPage <= 3) {
        pages.push(1, 2, 3, 4, "...", totalPages)
      } else if (currentPage >= totalPages - 2) {
        pages.push(1, "...", totalPages - 3, totalPages - 2, totalPages - 1, totalPages)
      } else {
        pages.push(1, "...", currentPage - 1, currentPage, currentPage + 1, "...", totalPages)
      }
    }

    return pages
  }

  const gridClasses =
    "grid grid-cols-5 gap-2 sm:grid-cols-8 md:grid-cols-10 lg:grid-cols-12 xl:grid-cols-15 2xl:grid-cols-18"

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      {/* Page Header */}
      <section className="border-b border-border bg-muted/30 py-4 md:py-6">
        <div className="container mx-auto max-w-7xl px-4">
          <h1 className="text-balance text-3xl font-bold md:text-4xl lg:text-5xl">モンスター図鑑</h1>
          <p className="mt-2 text-pretty text-muted-foreground md:text-lg">
            全{allMonsters.length.toLocaleString()}体のモンスター情報をチェック
          </p>
        </div>
      </section>

      <section className="container mx-auto max-w-7xl px-4 pt-4">
        <Tabs value={activeTab} onValueChange={handleTabChange}>
          <TabsList>
            <TabsTrigger value="all">全モンスター</TabsTrigger>
            <TabsTrigger value="owned">所持キャラ</TabsTrigger>
          </TabsList>

          <TabsContent value="all" className="space-y-3 pt-3">
            {/* Filters and Search */}
            <div className="space-y-3">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  type="text"
                  placeholder="名前、ストライクショット、ショットスキル、アシストスキルで検索..."
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value)
                    handleFilterChange()
                  }}
                  className="pl-10"
                />
              </div>

              {/* Element Filter */}
              <div className="flex flex-wrap items-center gap-4">
                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">属性:</span>
                  <Select
                    value={selectedElement || "all"}
                    onValueChange={(value) => {
                      setSelectedElement(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[130px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      <SelectItem value="fire">
                        <div className="flex items-center gap-2">
                          <Flame className="h-3 w-3" />
                          火属性
                        </div>
                      </SelectItem>
                      <SelectItem value="water">
                        <div className="flex items-center gap-2">
                          <Droplet className="h-3 w-3" />
                          水属性
                        </div>
                      </SelectItem>
                      <SelectItem value="thunder">
                        <div className="flex items-center gap-2">
                          <Zap className="h-3 w-3" />
                          雷属性
                        </div>
                      </SelectItem>
                      <SelectItem value="wood">
                        <div className="flex items-center gap-2">
                          <Leaf className="h-3 w-3" />
                          木属性
                        </div>
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">種族:</span>
                  <Select
                    value={selectedRace || "all"}
                    onValueChange={(value) => {
                      setSelectedRace(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[150px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {races.map((race) => (
                        <SelectItem key={race} value={race}>
                          {race}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">戦型:</span>
                  <Select
                    value={selectedType || "all"}
                    onValueChange={(value) => {
                      setSelectedType(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[130px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {battleTypes.map((type) => (
                        <SelectItem key={type} value={type}>
                          {type}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">レア度:</span>
                  <Select
                    value={selectedRarity || "all"}
                    onValueChange={(value) => {
                      setSelectedRarity(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[110px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      <SelectItem value="4">★4</SelectItem>
                      <SelectItem value="5">★5</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">進化形態:</span>
                  <Select
                    value={selectedEvolution || "all"}
                    onValueChange={(value) => {
                      setSelectedEvolution(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[130px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {evolutionStages.map((stage) => (
                        <SelectItem key={stage} value={stage}>
                          {stage}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">友情コンボ:</span>
                  <Select
                    value={selectedFriendshipCombo || "all"}
                    onValueChange={(value) => {
                      setSelectedFriendshipCombo(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[180px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {friendshipCombos.map((combo) => (
                        <SelectItem key={combo} value={combo}>
                          {combo}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">シリーズ:</span>
                  <Select
                    value={selectedSeries || "all"}
                    onValueChange={(value) => {
                      setSelectedSeries(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[180px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {seriesList.map((series) => (
                        <SelectItem key={series} value={series}>
                          {series}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-2">
                <span className="text-sm font-medium">アビリティ (複数選択可):</span>
                <div className="flex flex-wrap gap-4">
                  {abilities.map((ability) => (
                    <div key={ability} className="flex items-center space-x-2">
                      <Checkbox
                        id={ability}
                        checked={selectedAbilities.includes(ability)}
                        onCheckedChange={() => toggleAbility(ability)}
                      />
                      <Label htmlFor={ability} className="cursor-pointer text-sm">
                        {ability}
                      </Label>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            <p className="text-sm text-muted-foreground">
              {filteredMonsters.length.toLocaleString()}体中 {startIndex + 1}-
              {Math.min(endIndex, filteredMonsters.length)}体を表示
            </p>
          </TabsContent>

          <TabsContent value="owned" className="space-y-3 pt-3">
            {/* Same filters for owned tab */}
            <div className="space-y-3">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  type="text"
                  placeholder="名前、ストライクショット、ショットスキル、アシストスキルで検索..."
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value)
                    handleFilterChange()
                  }}
                  className="pl-10"
                />
              </div>

              <div className="flex flex-wrap items-center gap-4">
                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">属性:</span>
                  <Select
                    value={selectedElement || "all"}
                    onValueChange={(value) => {
                      setSelectedElement(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[130px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      <SelectItem value="fire">
                        <div className="flex items-center gap-2">
                          <Flame className="h-3 w-3" />
                          火属性
                        </div>
                      </SelectItem>
                      <SelectItem value="water">
                        <div className="flex items-center gap-2">
                          <Droplet className="h-3 w-3" />
                          水属性
                        </div>
                      </SelectItem>
                      <SelectItem value="thunder">
                        <div className="flex items-center gap-2">
                          <Zap className="h-3 w-3" />
                          雷属性
                        </div>
                      </SelectItem>
                      <SelectItem value="wood">
                        <div className="flex items-center gap-2">
                          <Leaf className="h-3 w-3" />
                          木属性
                        </div>
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">種族:</span>
                  <Select
                    value={selectedRace || "all"}
                    onValueChange={(value) => {
                      setSelectedRace(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[150px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {races.map((race) => (
                        <SelectItem key={race} value={race}>
                          {race}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">戦型:</span>
                  <Select
                    value={selectedType || "all"}
                    onValueChange={(value) => {
                      setSelectedType(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[130px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {battleTypes.map((type) => (
                        <SelectItem key={type} value={type}>
                          {type}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">レア度:</span>
                  <Select
                    value={selectedRarity || "all"}
                    onValueChange={(value) => {
                      setSelectedRarity(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[110px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      <SelectItem value="4">★4</SelectItem>
                      <SelectItem value="5">★5</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">進化形態:</span>
                  <Select
                    value={selectedEvolution || "all"}
                    onValueChange={(value) => {
                      setSelectedEvolution(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[130px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {evolutionStages.map((stage) => (
                        <SelectItem key={stage} value={stage}>
                          {stage}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">友情コンボ:</span>
                  <Select
                    value={selectedFriendshipCombo || "all"}
                    onValueChange={(value) => {
                      setSelectedFriendshipCombo(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[180px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {friendshipCombos.map((combo) => (
                        <SelectItem key={combo} value={combo}>
                          {combo}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">シリーズ:</span>
                  <Select
                    value={selectedSeries || "all"}
                    onValueChange={(value) => {
                      setSelectedSeries(value === "all" ? null : value)
                      handleFilterChange()
                    }}
                  >
                    <SelectTrigger className="w-[180px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">すべて</SelectItem>
                      {seriesList.map((series) => (
                        <SelectItem key={series} value={series}>
                          {series}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-2">
                <span className="text-sm font-medium">アビリティ (複数選択可):</span>
                <div className="flex flex-wrap gap-4">
                  {abilities.map((ability) => (
                    <div key={ability} className="flex items-center space-x-2">
                      <Checkbox
                        id={`owned-${ability}`}
                        checked={selectedAbilities.includes(ability)}
                        onCheckedChange={() => toggleAbility(ability)}
                      />
                      <Label htmlFor={`owned-${ability}`} className="cursor-pointer text-sm">
                        {ability}
                      </Label>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            <p className="text-sm text-muted-foreground">
              {filteredMonsters.length.toLocaleString()}体中 {startIndex + 1}-
              {Math.min(endIndex, filteredMonsters.length)}体を表示
            </p>
          </TabsContent>
        </Tabs>
      </section>

      {/* Monster Grid */}
      <section className="container mx-auto max-w-7xl px-4 py-3">
        <div className={gridClasses}>
          {currentMonsters.map((monster) => {
            const elementData = elementIcons[monster.element as keyof typeof elementIcons]
            const ElementIcon = elementData.icon
            const isGrayedOut = activeTab === "owned" && !monster.owned

            return (
              <Link key={monster.id} href={`/monsters/${monster.id}`}>
                <Card
                  className={`group cursor-pointer overflow-hidden transition-all hover:shadow-lg hover:border-primary flex flex-col gap-0 p-0 ${
                    isGrayedOut ? "opacity-30" : ""
                  }`}
                >
                  <div className="relative aspect-square bg-gradient-to-br from-muted to-muted/50">
                    <img
                      src={`/game-monster-creature-no.jpg?key=e93mf&height=${activeTab === "owned" ? "120" : "80"}&width=${activeTab === "owned" ? "120" : "80"}&query=game+monster+creature+no${monster.dexNo}`}
                      alt={`No.${monster.dexNo}`}
                      className="h-full w-full object-cover transition-transform group-hover:scale-110"
                    />
                    <div className="absolute left-1 top-1 rounded bg-background/90 px-1.5 py-0.5 text-[10px] font-semibold backdrop-blur-sm">
                      {monster.dexNo}
                    </div>
                    {/* <div className="absolute right-1 top-1 rounded-full bg-background/80 p-1 backdrop-blur-sm">
                      <ElementIcon className={`h-3 w-3 ${elementData.color}`} />
                    </div> */}
                  </div>
                  {/* ここが「各モンスターの下」 */}
                  {activeTab === "owned" && (
                    <div className="border-t border-border/60 px-1 py-0.5 text-center text-[10px] leading-none text-muted-foreground">
                      1：☘5
                    </div>
                  )}
                </Card>
              </Link>
            )
          })}
        </div>

        <div className="mt-4 flex flex-col items-center gap-4">
          <div className="flex flex-wrap items-center justify-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
              disabled={currentPage === 1}
            >
              <ChevronLeft className="h-4 w-4" />
              前へ
            </Button>

            {getPageNumbers().map((page, idx) =>
              page === "..." ? (
                <span key={`ellipsis-${idx}`} className="px-2 text-muted-foreground">
                  ...
                </span>
              ) : (
                <Button
                  key={page}
                  variant={currentPage === page ? "default" : "outline"}
                  size="sm"
                  onClick={() => setCurrentPage(page as number)}
                  className="min-w-[40px]"
                >
                  {page}
                </Button>
              ),
            )}

            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
              disabled={currentPage === totalPages}
            >
              次へ
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>

          <p className="text-sm text-muted-foreground">
            ページ {currentPage} / {totalPages.toLocaleString()}
          </p>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border bg-muted/50 py-4">
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
