import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { ChevronRight, Star, Calendar, Swords, BookOpen } from "lucide-react"
import { SiteHeader } from "@/components/site-header"
import Link from "next/link"

export default function HomePage() {
  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-br from-hero-from via-hero-via to-hero-to py-12 text-hero-foreground md:py-16 lg:py-20">
        <div className="container mx-auto max-w-7xl px-4">
          <div className="flex flex-col items-center text-center">
            <h1 className="text-balance text-4xl font-bold leading-tight tracking-tight md:text-5xl lg:text-6xl">
              最速攻略情報をお届け
            </h1>
            <p className="mt-3 max-w-2xl text-pretty text-lg text-hero-foreground/80 md:text-xl">
              最新イベント、キャラクター評価、クエスト攻略まで
            </p>
            <div className="mt-6 flex flex-wrap justify-center gap-3">
              <Button size="lg" className="bg-accent text-accent-foreground hover:bg-accent/90">
                最新情報を見る
                <ChevronRight className="ml-2 h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Quick Navigation */}
      <section className="container mx-auto max-w-7xl px-4 py-8">
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-4 lg:gap-4">
          <Card className="group cursor-pointer p-4 transition-all hover:border-primary hover:shadow-md lg:p-6">
            <div className="flex flex-col items-center gap-3 text-center">
              <div className="rounded-lg bg-primary/10 p-3 text-primary transition-colors group-hover:bg-primary group-hover:text-primary-foreground lg:p-4">
                <Calendar className="h-6 w-6 lg:h-8 lg:w-8" />
              </div>
              <div className="font-semibold lg:text-lg">イベント</div>
            </div>
          </Card>

          <Link href="/monsters">
            <Card className="group cursor-pointer p-4 transition-all hover:border-primary hover:shadow-md lg:p-6">
              <div className="flex flex-col items-center gap-3 text-center">
                <div className="rounded-lg bg-primary/10 p-3 text-primary transition-colors group-hover:bg-primary group-hover:text-primary-foreground lg:p-4">
                  <Star className="h-6 w-6 lg:h-8 lg:w-8" />
                </div>
                <div className="font-semibold lg:text-lg">モンスター図鑑</div>
              </div>
            </Card>
          </Link>

          <Card className="group cursor-pointer p-4 transition-all hover:border-primary hover:shadow-md lg:p-6">
            <div className="flex flex-col items-center gap-3 text-center">
              <div className="rounded-lg bg-primary/10 p-3 text-primary transition-colors group-hover:bg-primary group-hover:text-primary-foreground lg:p-4">
                <Swords className="h-6 w-6 lg:h-8 lg:w-8" />
              </div>
              <div className="font-semibold lg:text-lg">クエスト</div>
            </div>
          </Card>

          <Card className="group cursor-pointer p-4 transition-all hover:border-primary hover:shadow-md lg:p-6">
            <div className="flex flex-col items-center gap-3 text-center">
              <div className="rounded-lg bg-primary/10 p-3 text-primary transition-colors group-hover:bg-primary group-hover:text-primary-foreground lg:p-4">
                <BookOpen className="h-6 w-6 lg:h-8 lg:w-8" />
              </div>
              <div className="font-semibold lg:text-lg">初心者ガイド</div>
            </div>
          </Card>
        </div>
      </section>

      {/* Latest News */}
      <section className="container mx-auto max-w-7xl px-4 py-8">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-2xl font-bold lg:text-3xl">最新ニュース</h2>
          <Button variant="ghost" size="sm">
            すべて見る
            <ChevronRight className="ml-1 h-4 w-4" />
          </Button>
        </div>

        <div className="grid gap-3 md:grid-cols-2 lg:gap-4">
          <Card className="overflow-hidden transition-all hover:shadow-md">
            <div className="flex gap-4 p-4">
              <div className="h-20 w-20 flex-shrink-0 overflow-hidden rounded-lg bg-muted lg:h-24 lg:w-24">
                <img src="/monster-character.jpg" alt="News thumbnail" className="h-full w-full object-cover" />
              </div>
              <div className="flex-1">
                <div className="mb-1 flex items-center gap-2">
                  <span className="rounded bg-primary px-2 py-0.5 text-xs font-semibold text-primary-foreground">
                    NEW
                  </span>
                  <span className="text-xs text-muted-foreground">2時間前</span>
                </div>
                <h3 className="text-pretty font-semibold leading-snug lg:text-lg">
                  新イベント「神獣の試練」開催！限定キャラ登場
                </h3>
              </div>
            </div>
          </Card>

          <Card className="overflow-hidden transition-all hover:shadow-md">
            <div className="flex gap-4 p-4">
              <div className="h-20 w-20 flex-shrink-0 overflow-hidden rounded-lg bg-muted lg:h-24 lg:w-24">
                <img src="/game-update.jpg" alt="News thumbnail" className="h-full w-full object-cover" />
              </div>
              <div className="flex-1">
                <div className="mb-1 flex items-center gap-2">
                  <span className="rounded bg-accent px-2 py-0.5 text-xs font-semibold text-accent-foreground">
                    更新
                  </span>
                  <span className="text-xs text-muted-foreground">5時間前</span>
                </div>
                <h3 className="text-pretty font-semibold leading-snug lg:text-lg">
                  バージョン12.0アップデート情報まとめ
                </h3>
              </div>
            </div>
          </Card>

          <Card className="overflow-hidden transition-all hover:shadow-md">
            <div className="flex gap-4 p-4">
              <div className="h-20 w-20 flex-shrink-0 overflow-hidden rounded-lg bg-muted lg:h-24 lg:w-24">
                <img src="/strategy-guide.jpg" alt="News thumbnail" className="h-full w-full object-cover" />
              </div>
              <div className="flex-1">
                <div className="mb-1 flex items-center gap-2">
                  <span className="rounded bg-secondary px-2 py-0.5 text-xs font-semibold text-secondary-foreground">
                    攻略
                  </span>
                  <span className="text-xs text-muted-foreground">1日前</span>
                </div>
                <h3 className="text-sm font-semibold leading-snug lg:text-base">
                  覇者の塔40階の攻略パーティとポイント解説
                </h3>
              </div>
            </div>
          </Card>
        </div>
      </section>

      {/* Popular Characters */}
      <section className="container mx-auto max-w-7xl px-4 py-8">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-2xl font-bold lg:text-3xl">人気キャラランキング</h2>
          <Button variant="ghost" size="sm">
            もっと見る
            <ChevronRight className="ml-1 h-4 w-4" />
          </Button>
        </div>

        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 lg:gap-4">
          {[1, 2, 3, 4, 5].map((rank) => (
            <Card key={rank} className="group cursor-pointer overflow-hidden transition-all hover:shadow-lg">
              <div className="aspect-square bg-muted">
                <img
                  src={`/anime-character-rank-.jpg?key=edri3&height=200&width=200&query=anime+character+rank+${rank}`}
                  alt={`Character ${rank}`}
                  className="h-full w-full object-cover"
                />
              </div>
              <div className="p-3">
                <div className="mb-1 flex items-center gap-1">
                  <span className="rounded bg-primary px-1.5 py-0.5 text-xs font-bold text-primary-foreground">
                    {rank}位
                  </span>
                  <div className="flex gap-0.5">
                    {[...Array(5)].map((_, i) => (
                      <Star key={i} className="h-3 w-3 fill-yellow-400 text-yellow-400" />
                    ))}
                  </div>
                </div>
                <h3 className="text-sm font-semibold leading-snug">キャラクター名</h3>
              </div>
            </Card>
          ))}
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
