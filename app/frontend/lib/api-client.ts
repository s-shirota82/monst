export const API_BASE_URL =
    process.env.NEXT_PUBLIC_API_BASE_URL?.replace(/\/$/, "") || "http://localhost:8080";

export class ApiError extends Error {
    status: number;
    payload?: unknown;
    constructor(message: string, status: number, payload?: unknown) {
        super(message);
        this.status = status;
        this.payload = payload;
    }
}

type FetchOptions = Omit<RequestInit, "headers"> & {
    headers?: Record<string, string>;
    token?: string | null;
};

export async function apiFetch<T>(path: string, options: FetchOptions = {}): Promise<T> {
    const url = path.startsWith("http") ? path : `${API_BASE_URL}${path.startsWith("/") ? "" : "/"}${path}`;

    const headers: Record<string, string> = {
        ...(options.headers || {}),
    };

    if (options.token) {
        headers["Authorization"] = `Bearer ${options.token}`;
    }

    const isFormData = typeof FormData !== "undefined" && options.body instanceof FormData;
    if (!isFormData && options.body && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    const res = await fetch(url, {
        ...options,
        headers,
        cache: "no-store",
    });

    const contentType = res.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const payload = isJson ? await res.json().catch(() => null) : await res.text().catch(() => null);

    if (!res.ok) {
        const msg =
            payload && typeof payload === "object" && "message" in (payload as any)
                ? String((payload as any).message)
                : `Request failed: ${res.status}`;
        throw new ApiError(msg, res.status, payload);
    }

    return payload as T;
}
