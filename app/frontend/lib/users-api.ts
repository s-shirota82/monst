import { apiFetch } from "./api-client";
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from "./types";

/**
 * ログイン
 * POST /user/login
 */
export async function loginUser(body: LoginRequest): Promise<LoginResponse> {
  return apiFetch<LoginResponse>("/user/login", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

/**
 * ユーザー登録
 * POST /user/register
 */
export async function registerUser(body: RegisterRequest): Promise<RegisterResponse> {
  return apiFetch<RegisterResponse>("/user/register", {
    method: "POST",
    body: JSON.stringify(body),
  });
}
