"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

import { SiteHeader } from "@/components/site-header";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

import { registerUser } from "@/lib/users-api";
import { ApiError } from "@/lib/api-client";

export default function RegisterPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);

    try {
      await registerUser({
        email: email.trim(),
        name: name.trim(),
        password,
      });

      // 登録成功 → ログインへ
      router.push("/login");
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError("登録に失敗しました。");
      }
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
            <h1 className="text-xl font-bold">ユーザー登録</h1>
            <p className="mt-2 text-sm text-muted-foreground">
              メールアドレス・名前・パスワードで登録します。
            </p>

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
                <label className="text-sm font-medium">表示名</label>
                <Input
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="test"
                  autoComplete="name"
                  required
                />
              </div>

              <div className="space-y-1">
                <label className="text-sm font-medium">パスワード</label>
                <Input
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  type="password"
                  placeholder="Abcd1234"
                  autoComplete="new-password"
                  required
                />
                <div className="text-xs text-muted-foreground">
                  8〜50文字、英字+数字の混合、大文字を含む（英数字のみ）
                </div>
              </div>

              <Button type="submit" className="w-full" disabled={submitting}>
                {submitting ? "送信中..." : "登録する"}
              </Button>
            </form>

            <div className="mt-4 text-sm text-muted-foreground">
              すでにアカウントを持っている場合は{" "}
              <a href="/login" className="underline">
                ログイン
              </a>
              へ。
            </div>
          </Card>
        </div>
      </main>
    </div>
  );
}
