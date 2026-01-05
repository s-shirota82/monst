# monst 環境構築手順（Docker / WSL）

このREADMEは、`monst/` を GitHub からクローンして、コンテナ構築〜疎通確認〜（必要に応じて）frontend の更新パッケージ(zip)を差分反映するまでの手順をまとめたものです。

## 前提

- Windows + WSL2（Ubuntu等）
- Docker Desktop をインストール済み
- Docker Desktop の **WSL Integration** が有効  
  Docker Desktop → Settings → Resources → WSL Integration → 対象ディストリビューションを ON

## 1. リポジトリのクローン

```bash
cd ~
git clone <YOUR_GITHUB_REPO_URL> monst
cd ~/monst/app
```

> `docker-compose.yml` が `~/monst/app` にある前提。

## 2. 必要コマンドのインストール（WSL）

frontend 更新(zip差分反映)で使います。

```bash
sudo apt update
sudo apt install -y unzip rsync
```

### Docker コマンドが見つからない場合

以下が出る場合は Docker Desktop の WSL Integration が未設定です。

```
The command 'docker' could not be found in this WSL 2 distro.
We recommend to activate the WSL integration in Docker Desktop settings.
```

Docker Desktop 側で WSL Integration を有効化 → WSL を再起動してください。

## 3. 環境変数ファイルの準備（.env）

`~/monst/app/.env` が無ければ作成します（中身はプロジェクトの指示に従う）。

```bash
cd ~/monst/app
ls -a
# .env が存在することを確認
```

## 4. Docker Compose 起動（ビルド込み）

```bash
cd ~/monst/app
docker compose up -d --build
```

起動確認：

```bash
docker compose ps
```

想定ポート（例）：

- backend(Spring Boot): `8080`
- node(Express): `3000`
- mysql: `3306`

## 5. 疎通確認（curl）

```bash
curl -v http://localhost:8080/health
curl -v http://localhost:3000/health
curl -v http://localhost:3000/users
```

期待値（例）：

- `/health` は `{"ok":true}`
- `/users` はユーザー配列が返る

## 6. DB（MySQL）へ接続・DDL投入

### コンテナに入る

```bash
docker exec -it monst-mysql mysql -u root -p
# パスワードは .env / docker-compose.yml の設定に従う
```

### DB選択

```sql
SHOW DATABASES;
USE <YOUR_DB_NAME>;
```

### DDL投入

作成した SQL（monster_main や master テーブル等）を実行してください。

## 7. frontend（Next.js）をローカルで起動（任意）

`frontend/` がリポジトリに含まれる/展開済みの場合。

```bash
cd ~/monst/app/frontend
corepack enable
pnpm install
```

### 起動（node:3000 と衝突しやすいので 3001 推奨）

```bash
pnpm dev -p 3001
```

ブラウザ：

- http://localhost:3001

> `Cannot GET /` が出る場合、`http://localhost:3000`（Express）を開いている可能性があります。Next.js を起動したポート（例：3001）にアクセスしてください。

---

# frontend 更新パッケージ(zip)を差分反映（パターンB）

## 想定するzip構造（パターンB）

zipを展開すると直下に以下がある構造：

```
app/
components/
package.json
pnpm-lock.yaml
...
```

## 手順

### 1) zipを一時ディレクトリへ展開

`/path/to/frontend_update.zip` は実ファイルに置き換えてください。

```bash
cd ~/monst/app
rm -rf /tmp/frontend_update
mkdir -p /tmp/frontend_update

unzip /path/to/frontend_update.zip -d /tmp/frontend_update
ls /tmp/frontend_update
```

`app/` や `components/` が見えればOKです。

### 2) 差分の事前確認（推奨）

```bash
rsync -av --delete --dry-run /tmp/frontend_update/ ~/monst/app/frontend/
```

### 3) 差分を反映（本番）

```bash
rsync -av --delete /tmp/frontend_update/ ~/monst/app/frontend/
```

`--delete` により、zip側に無いファイルは `frontend/` から削除されます（差分更新の肝）。

### 4) 依存関係が変わった場合のみ install

`package.json` / `pnpm-lock.yaml` が更新された場合：

```bash
cd ~/monst/app/frontend
pnpm install
```

### 5) 再起動

- ローカル dev の場合（起動し直す）

```bash
cd ~/monst/app/frontend
pnpm dev -p 3001
```

- Docker で frontend を動かしている場合

```bash
cd ~/monst/app
docker compose build frontend
docker compose up -d frontend
```

---

## 付録：停止・削除

停止：

```bash
cd ~/monst/app
docker compose stop
```

削除：

```bash
docker compose down
```

DBを初期化したい場合（ボリューム削除）：

```bash
docker compose down -v
```
