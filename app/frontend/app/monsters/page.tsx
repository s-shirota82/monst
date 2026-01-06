"use client"

import { Card } from "@/components/ui/card"
import { SiteHeader } from "@/components/site-header"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Flame, Droplet, Zap, Leaf, ChevronLeft, ChevronRight } from "lucide-react"
import Link from "next/link"
import { useState, useMemo } from "react"

// モンスターのタイプを定義
const elementIcons = {
  fire: { icon: Flame, color: "text-red-500" },
  water: { icon: Droplet, color: "text-blue-500" },
  thunder: { icon: Zap, color: "text-yellow-500" },
  wood: { icon: Leaf, color: "text-green-500" },
}

const generateMonsters = () => {
  const elements = ["fire", "water", "thunder", "wood"] as const
  const types = ["バランス", "パワー", "スピード"]
  const monsters = []

  for (let i = 1; i <= 9000; i++) {
    monsters.push({
      id: i,
      dexNo: String(i).padStart(4, "0"),
      name: `モンスター${i}`,
      element: elements[Math.floor(Math.random() * elements.length)],
      rarity: Math.floor(Math.random() * 2) + 4, // 4 or 5
      type: types[Math.floor(Math.random() * types.length)],
      evolution: Math.floor(Math.random() * 2) + 2, // 2 or 3
    })
  }

  return monsters
}

const allMonsters = generateMonsters()
const ITEMS_PER_PAGE = 300

export default function MonstersPage() {
  const [currentPage, setCurrentPage] = useState(1)
  const [selectedElement, setSelectedElement] = useState<string | null>(null)

  const filteredMonsters = useMemo(() => {
    if (!selectedElement) return allMonsters
    return allMonsters.filter((m) => m.element === selectedElement)
  }, [selectedElement])

  const totalPages = Math.ceil(filteredMonsters.length / ITEMS_PER_PAGE)
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE
  const endIndex = startIndex + ITEMS_PER_PAGE
  const currentMonsters = filteredMonsters.slice(startIndex, endIndex)

  const handleFilterChange = (element: string | null) => {
    setSelectedElement(element)
    setCurrentPage(1)
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

      {/* Filters */}
      <section className="container mx-auto max-w-7xl px-4 py-3">
        <div className="flex flex-wrap gap-2">
          <Badge
            variant={selectedElement === null ? "default" : "outline"}
            className="cursor-pointer hover:bg-primary hover:text-primary-foreground"
            onClick={() => handleFilterChange(null)}
          >
            すべて ({allMonsters.length.toLocaleString()})
          </Badge>
          <Badge
            variant={selectedElement === "fire" ? "default" : "outline"}
            className="cursor-pointer hover:bg-primary hover:text-primary-foreground"
            onClick={() => handleFilterChange("fire")}
          >
            <Flame className="mr-1 h-3 w-3" />
            火属性
          </Badge>
          <Badge
            variant={selectedElement === "water" ? "default" : "outline"}
            className="cursor-pointer hover:bg-primary hover:text-primary-foreground"
            onClick={() => handleFilterChange("water")}
          >
            <Droplet className="mr-1 h-3 w-3" />
            水属性
          </Badge>
          <Badge
            variant={selectedElement === "thunder" ? "default" : "outline"}
            className="cursor-pointer hover:bg-primary hover:text-primary-foreground"
            onClick={() => handleFilterChange("thunder")}
          >
            <Zap className="mr-1 h-3 w-3" />
            雷属性
          </Badge>
          <Badge
            variant={selectedElement === "wood" ? "default" : "outline"}
            className="cursor-pointer hover:bg-primary hover:text-primary-foreground"
            onClick={() => handleFilterChange("wood")}
          >
            <Leaf className="mr-1 h-3 w-3" />
            木属性
          </Badge>
        </div>
        <p className="mt-2 text-sm text-muted-foreground">
          {filteredMonsters.length.toLocaleString()}体中 {startIndex + 1}-{Math.min(endIndex, filteredMonsters.length)}
          体を表示
        </p>
      </section>

      {/* Monster Grid */}
      <section className="container mx-auto max-w-7xl px-4 py-3">
        <div className="grid grid-cols-5 gap-1 sm:grid-cols-8 md:grid-cols-10 lg:grid-cols-12 xl:grid-cols-15 2xl:grid-cols-18">
          {currentMonsters.map((monster) => {
            const elementData = elementIcons[monster.element as keyof typeof elementIcons]
            const ElementIcon = elementData.icon

            return (
              <Link key={monster.id} href={`/monsters/${monster.id}`}>
                <Card className="group cursor-pointer overflow-hidden transition-all hover:shadow-lg hover:border-primary">
                  <div className="relative aspect-square bg-gradient-to-br from-muted to-muted/50">
                    <img
                      src={`/game-monster-creature-no.jpg?key=e93mf&height=80&width=80&query=game+monster+creature+no${monster.dexNo}`}
                      alt={`No.${monster.dexNo}`}
                      className="h-full w-full object-cover transition-transform group-hover:scale-110"
                    />
                    <div className="absolute left-1 top-1 rounded bg-background/90 px-1.5 py-0.5 text-[10px] font-semibold backdrop-blur-sm">
                      {monster.dexNo}
                    </div>
                    <div className="absolute right-1 top-1 rounded-full bg-background/80 p-1 backdrop-blur-sm">
                      <ElementIcon className={`h-3 w-3 ${elementData.color}`} />
                    </div>
                  </div>
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
