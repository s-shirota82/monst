import express from "express";
import mysql from "mysql2/promise";

const app = express();
const port = 3000;

const db = {
  host: process.env.DB_HOST || "mysql",
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || "appuser",
  password: process.env.DB_PASSWORD || "apppass",
  database: process.env.DB_NAME || "appdb",
};

app.get("/health", (_, res) => res.json({ ok: true }));

app.get("/users", async (_, res) => {
  const conn = await mysql.createConnection(db);
  const [rows] = await conn.query("SELECT id, name, created_at FROM users ORDER BY id");
  await conn.end();
  res.json(rows);
});

app.listen(port, "0.0.0.0", () => {
  console.log(`node listening on :${port}`);
});
