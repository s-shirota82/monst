"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useAuth } from "@/lib/auth-context"
import Link from "next/link"
import { LogIn } from "lucide-react"

export default function LoginPage() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const { login } = useAuth()
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setIsLoading(true)

    try {
      const success = await login(email, password)
      if (success) {
        router.push("/")
      } else {
        setError("メールアドレスまたはパスワードが正しくありません")
      }
    } catch (err) {
      setError("ログインに失敗しました")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen flex-col bg-background">
      {/* Header */}
      <header className="w-full border-b border-border bg-background/95 backdrop-blur">
        <div className="container flex h-14 items-center justify-between px-4">
          <Link href="/" className="flex items-center space-x-2">
            <div className="text-xl font-bold text-primary">モンスト攻略</div>
          </Link>
        </div>
      </header>

      {/* Login Form */}
      <div className="flex flex-1 items-center justify-center px-4 py-12">
        <div className="w-full max-w-md">
          <div className="mb-8 text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
              <LogIn className="h-6 w-6 text-primary" />
            </div>
            <h1 className="text-2xl font-bold">ログイン</h1>
            <p className="mt-2 text-sm text-muted-foreground">アカウントにログインして攻略情報をチェック</p>
          </div>

          <Card className="p-6">
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">メールアドレス</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="user1@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  autoComplete="email"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">パスワード</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="password123"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  autoComplete="current-password"
                />
              </div>

              {error && <div className="rounded-md bg-destructive/10 p-3 text-sm text-destructive">{error}</div>}

              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "ログイン中..." : "ログイン"}
              </Button>
            </form>

            <div className="mt-6 rounded-lg bg-muted p-4 text-sm">
              <p className="mb-2 font-semibold">テストアカウント:</p>
              <p className="text-muted-foreground">
                Email: user1@example.com
                <br />
                Password: password123
              </p>
            </div>
          </Card>

          <div className="mt-4 text-center text-sm text-muted-foreground">
            <Link href="/" className="hover:text-primary">
              ホームに戻る
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
