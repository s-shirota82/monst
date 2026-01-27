"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

import { SiteHeader } from "@/components/site-header";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

import { useAuth } from "@/lib/auth-context";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);

    try {
      const ok = await login(email.trim(), password);
      if (!ok) {
        setError("メールアドレスまたはパスワードが正しくありません。");
        return;
      }
      router.push("/monsters");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <SiteHeader />

      <main className="container mx-auto px-4 py-10">
        <div className="mx-auto max-w-md">
          <Card className="p-6">
            <h1 className="text-xl font-bold">ログイン</h1>

            {error && (
              <div className="mt-4 rounded-md border p-3 text-sm">
                <div className="font-semibold">エラー</div>
                <div className="text-muted-foreground">{error}</div>
              </div>
            )}

            <form className="mt-6 space-y-4" onSubmit={onSubmit}>
              <div className="space-y-1">
                <label className="text-sm font-medium">メールアドレス</label>
                <Input
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="test@example.com"
                  autoComplete="email"
                  required
                />
              </div>

              <div className="space-y-1">
                <label className="text-sm font-medium">パスワード</label>
                <Input
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  type="password"
                  autoComplete="current-password"
                  required
                />
              </div>

              <Button type="submit" className="w-full" disabled={submitting}>
                {submitting ? "ログイン中..." : "ログイン"}
              </Button>
            </form>

            <div className="mt-4 text-sm text-muted-foreground text-center">
              アカウントをお持ちでない方は{" "}
              <a href="/register" className="underline font-medium">
                新規登録はこちら
              </a>
            </div>
          </Card>
        </div>
      </main>
    </div>
  );
}
