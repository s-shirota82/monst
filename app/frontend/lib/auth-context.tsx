"use client"

import type React from "react"
import { createContext, useContext, useEffect, useMemo, useState } from "react"
import { apiFetch } from "./api-client"
import type { LoginResponse } from "./types"

type Role = "ADMIN" | "USER" | string | number

interface User {
  id: string
  email: string
  role?: Role
  token?: string | null
}

interface AuthContextType {
  user: User | null
  token: string | null
  isAdmin: boolean
  login: (email: string, password: string) => Promise<boolean>
  logout: () => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

const STORAGE_KEY = "monst_auth"

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (raw) {
        const parsed = JSON.parse(raw) as User
        setUser(parsed)
      }
    } catch {
      // ignore
    } finally {
      setIsLoading(false)
    }
  }, [])

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const res = await apiFetch<LoginResponse>("/user/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      })

      const next: User = {
        id: String(res.id),
        email: res.email,
        role: res.role,
        token: (res as any).token ?? null,
      }

      setUser(next)
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
      return true
    } catch {
      return false
    }
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem(STORAGE_KEY)
  }

  const token = user?.token ?? null

  const isAdmin = useMemo(() => {
    if (!user?.role) return false
    if (typeof user.role === "string") return user.role.toUpperCase() === "ADMIN"
    return false
  }, [user?.role])

  return (
    <AuthContext.Provider value={{ user, token, isAdmin, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
