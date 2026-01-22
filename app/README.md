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

app/
├── backend/ # Spring Boot
├── frontend/ # Next.js
├── mysql/
│ └── init/ # MySQL 初期化 SQL
├── docker-compose.yml
├── .env
└── README.md

---

## 前提条件

- Windows + WSL2（Ubuntu 24.04 など）
- Docker Desktop インストール済み
- Docker Desktop の WSL Integration が有効

---

## 1. リポジトリのクローン

cd ~
git clone <YOUR_GITHUB_REPO_URL> monst
cd ~/monst/app

---

## 2. WSL に必要なツールをインストール（初回のみ）

sudo apt update
sudo apt install -y unzip rsync

---

## 3. 環境変数ファイル（.env）

MYSQL_DATABASE=appdb
MYSQL_USER=appuser
MYSQL_PASSWORD=apppass
MYSQL_ROOT_PASSWORD=rootpass

---

## 4. 開発環境の起動

docker compose up -d --build

---

## 5. アクセス先

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- MySQL: localhost:3306

---

## 6. 開発の進め方

### Frontend

VSCode で編集 → 保存 → 自動ホットリロード

### Backend

VSCode で編集 → 保存 → 自動再起動（DevTools）

---

## 7. よく使うコマンド

docker compose logs -f
docker compose exec backend sh -lc "gradle test"
docker compose exec frontend sh -lc "pnpm lint"

---

## 8. 停止・初期化

docker compose down
docker compose down -v
