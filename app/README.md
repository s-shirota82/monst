# monst 開発環境構築・開発手順（Docker / WSL）

この README は、`monst` リポジトリを GitHub からクローンし、
Docker + WSL + VSCode を使って開発を始めるまでの手順をまとめたものです。

---

## 技術構成

- Frontend: Next.js（pnpm / Node.js 20）
- Backend: Spring Boot 3 / Java 21 / Gradle
- Database: MySQL 8.4
- 開発環境: Docker / Docker Compose / WSL2 / VSCode

---

## ディレクトリ構成

```text
app/
├── backend/   # Spring Boot
├── frontend/  # Next.js
├── mysql/
│   └── init/  # MySQL 初期化 SQL
├── docker-compose.yml
├── .env
└── README.md
```

---

## 5. アクセス先

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- MySQL: localhost:3306

---

## 前提条件

- Windows + WSL2（Ubuntu 24.04 など）
- Docker Desktop インストール済み
- Docker Desktop の WSL Integration が有効

---

## 1. 開発環境の構築

### 1-1. リポジトリのクローン

```bash
git clone <YOUR_GITHUB_REPO_URL> monst
cd ~/monst/app
```

### 1-2. docker構築

```bash
docker compose up -d --build
```

## 2. 開発の進め方

### コンテナの起動

```bash
docker compose start

```

### CI/CD

各ディレクトリに移動 → VSCode で編集 → 保存 → 自動リロード

### コンテナの停止

```bash
docker compose stop

```

## 3. その他

### コンテナの削除

```bash
docker compose down

```

### ボリュームの削除

```bash
docker compose down -v
```

### データベースの初期化

```bash
docker compose down -v
docker compose up -d --build
```
