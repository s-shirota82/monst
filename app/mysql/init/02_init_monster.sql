/* =========================================================
   init.sql（完成形：カラム名整理 + 外部キー追加 + 冪等INSERT）
   - 参照カラムは *_id に統一
   - master の id は BIGINT UNSIGNED
   - 外部キーで整合性担保（InnoDB）
   - seed は ON DUPLICATE KEY UPDATE で冪等
   ========================================================= */

-- 文字コードなどは環境に合わせて（必要なら）設定
-- SET NAMES utf8mb4;
-- SET time_zone = '+09:00';

-- =========================
-- rarity_master
-- =========================
CREATE TABLE IF NOT EXISTS rarity_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rarity INT NOT NULL,               -- レア度（1〜6など）
  max_level INT NOT NULL,            -- 最大レベル
  label VARCHAR(50) NULL,            -- 表示名（例：レベル上限解放時）
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_rarity_master_rarity (rarity)
) ENGINE=InnoDB;

INSERT INTO rarity_master (rarity, max_level, label) VALUES
(1, 5,  NULL),
(2, 15, NULL),
(3, 20, NULL),
(4, 40, NULL),
(5, 70, NULL),
(6, 99, NULL),
(0, 120, 'レベル上限解放時')
ON DUPLICATE KEY UPDATE
  max_level = VALUES(max_level),
  label = VALUES(label);

-- =========================
-- evolution_stage_master
-- =========================
CREATE TABLE IF NOT EXISTS evolution_stage_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,          -- 形態名
  level_cap_release TINYINT NOT NULL, -- レベル上限解放（0/1/2）
  super_battle_release TINYINT NOT NULL, -- 超戦型開放（0/1/2）
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO evolution_stage_master (id, name, level_cap_release, super_battle_release) VALUES
(1, '進化前',        0, 0),
(2, '進化',          0, 0),
(3, '神化',          0, 0),
(4, '獣神化前',      0, 0),
(5, '獣神化',        1, 1),
(6, '獣神化・改',    2, 2),
(7, '真・獣神化前',  0, 0),
(8, '真・獣神化',    2, 2)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  level_cap_release = VALUES(level_cap_release),
  super_battle_release = VALUES(super_battle_release);

-- =========================
-- attribute_master
-- =========================
CREATE TABLE IF NOT EXISTS attribute_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(10) NOT NULL,   -- 属性名（火/水/木/光/闇/無）
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO attribute_master (id, name) VALUES
(1, '火'),
(2, '水'),
(3, '木'),
(4, '光'),
(5, '闇'),
(6, '無')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

-- =========================
-- luck_skill_master
-- =========================
CREATE TABLE IF NOT EXISTS luck_skill_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,   -- ラックスキル名
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO luck_skill_master (id, name) VALUES
(1, 'クリティカル'),
(2, 'シールド'),
(3, '友情コンボクリティカル'),
(4, 'ガイド')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

-- =========================
-- hit_type_master
-- =========================
CREATE TABLE IF NOT EXISTS hit_type_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL,   -- 撃種名（反射 / 貫通）
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO hit_type_master (id, name) VALUES
(1, '反射'),
(2, '貫通')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

-- =========================
-- tribe_master
-- =========================
CREATE TABLE IF NOT EXISTS tribe_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,   -- 種族名
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO tribe_master (id, name) VALUES
(1,  'アクシス'),
(2,  '亜人'),
(3,  '神'),
(4,  '獣'),
(5,  '幻獣'),
(6,  '幻妖'),
(7,  '鉱物'),
(8,  'コスモ'),
(9,  'サムライ'),
(10, '聖騎士'),
(11, '闘神'),
(12, '鳥'),
(13, 'ドラゴン'),
(14, '魔王'),
(15, '魔人'),
(16, '魔族'),
(17, 'ユニオン'),
(18, 'ユニバース'),
(19, '妖精'),
(20, 'ロボット'),
(21, '怪異'),
(22, '怪獣'),
(23, 'キメラアント'),
(24, 'ゴジエヴァ'),
(25, '黄金聖闘士'),
(26, '使徒'),
(27, '死神'),
(28, 'バーニッシュ'),
(29, 'ファイター'),
(30, 'ホムンクルス'),
(31, '魔神'),
(32, 'マベツム'),
(33, '妖怪')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

-- =========================
-- battle_type_master
-- =========================
CREATE TABLE IF NOT EXISTS battle_type_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,   -- 戦型名（パワー / バランス / 砲撃 / スピード）
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO battle_type_master (id, name) VALUES
(1, 'パワー'),
(2, 'バランス'),
(3, '砲撃'),
(4, 'スピード')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

-- =========================
-- ability_master / ability_stage_master（FKなし：JSON運用のため）
-- =========================
CREATE TABLE IF NOT EXISTS ability_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  is_super TINYINT NOT NULL,
  has_stage TINYINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ability_stage_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  stage VARCHAR(5) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================
-- shot_skill_master / assist_skill_master
-- =========================
CREATE TABLE IF NOT EXISTS shot_skill_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS assist_skill_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================
-- strike_shot_name_master
-- =========================
CREATE TABLE IF NOT EXISTS strike_shot_name_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================
-- strike_shot_effect_master（name -> strike_shot_name_id）
-- =========================
CREATE TABLE IF NOT EXISTS strike_shot_effect_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  strike_shot_name_id BIGINT UNSIGNED NOT NULL,
  effect VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_sse_name_id (strike_shot_name_id),
  CONSTRAINT fk_sse_name
    FOREIGN KEY (strike_shot_name_id) REFERENCES strike_shot_name_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================
-- sub_friendship_combo_category_master
-- =========================
CREATE TABLE IF NOT EXISTS sub_friendship_combo_category_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================
-- sub_friendship_combo_name_master（attribute/category -> *_id）
-- =========================
CREATE TABLE IF NOT EXISTS sub_friendship_combo_name_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  attribute_id BIGINT UNSIGNED NOT NULL,
  category_id BIGINT UNSIGNED NOT NULL,
  description VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_sfcn_attr (attribute_id),
  KEY idx_sfcn_cat (category_id),
  CONSTRAINT fk_sfcn_attr
    FOREIGN KEY (attribute_id) REFERENCES attribute_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_sfcn_cat
    FOREIGN KEY (category_id) REFERENCES sub_friendship_combo_category_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================
-- sub_friendship_combo_power_master（name -> friendship_combo_name_id / battle_type -> battle_type_id）
-- =========================
CREATE TABLE IF NOT EXISTS sub_friendship_combo_power_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  friendship_combo_name_id BIGINT UNSIGNED NOT NULL,
  rarity INT NOT NULL,
  battle_type_id BIGINT UNSIGNED NOT NULL,
  is_attribute_match TINYINT NOT NULL,
  power INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_sfcp_name (friendship_combo_name_id),
  KEY idx_sfcp_rarity (rarity),
  KEY idx_sfcp_bt (battle_type_id),
  CONSTRAINT fk_sfcp_name
    FOREIGN KEY (friendship_combo_name_id) REFERENCES sub_friendship_combo_name_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_sfcp_rarity
    FOREIGN KEY (rarity) REFERENCES rarity_master(rarity)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_sfcp_bt
    FOREIGN KEY (battle_type_id) REFERENCES battle_type_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================
-- series_info_master
-- =========================
CREATE TABLE IF NOT EXISTS series_info_master (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================
-- monster_main（参照カラムを *_id に統一）
-- friendship_combo_id / sub_friendship_combo_id は
-- sub_friendship_combo_name_master(id) を参照する想定
-- =========================
CREATE TABLE IF NOT EXISTS monster_main (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

  number INT NOT NULL,
  rarity INT NOT NULL,

  name VARCHAR(255) NOT NULL,
  evolution_stage_id BIGINT UNSIGNED NOT NULL,
  attribute_id BIGINT UNSIGNED NOT NULL,

  hp_max INT NOT NULL,
  hp_plus_max INT NOT NULL,
  attack_max INT NOT NULL,
  attack_plus_max INT NOT NULL,
  speed_max DECIMAL(7,3) NOT NULL,
  speed_plus_max DECIMAL(7,3) NOT NULL,

  luck_skill_id BIGINT UNSIGNED NULL,
  hit_type_id BIGINT UNSIGNED NOT NULL,
  tribe_id BIGINT UNSIGNED NOT NULL,
  battle_type_id BIGINT UNSIGNED NOT NULL,

  base_ability JSON,
  gauge_ability JSON,
  connect_skill JSON,

  shot_skill_id BIGINT UNSIGNED NULL,
  assist_skill_id BIGINT UNSIGNED NULL,

  strike_shot_name_id BIGINT UNSIGNED NOT NULL,
  strike_shot_effect_id BIGINT UNSIGNED NOT NULL,

  friendship_combo_id BIGINT UNSIGNED NOT NULL,
  sub_friendship_combo_id BIGINT UNSIGNED NULL,

  special_note INT NOT NULL,
  series_info_id BIGINT UNSIGNED NULL,

  icon_image VARCHAR(100) NOT NULL,
  monster_image VARCHAR(100) NOT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  KEY idx_mm_number (number),
  KEY idx_mm_rarity (rarity),
  KEY idx_mm_attr (attribute_id),
  KEY idx_mm_tribe (tribe_id),
  KEY idx_mm_bt (battle_type_id),

  CONSTRAINT fk_mm_rarity
    FOREIGN KEY (rarity) REFERENCES rarity_master(rarity)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_evo
    FOREIGN KEY (evolution_stage_id) REFERENCES evolution_stage_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_attr
    FOREIGN KEY (attribute_id) REFERENCES attribute_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_luck
    FOREIGN KEY (luck_skill_id) REFERENCES luck_skill_master(id)
    ON UPDATE RESTRICT ON DELETE SET NULL,

  CONSTRAINT fk_mm_hit
    FOREIGN KEY (hit_type_id) REFERENCES hit_type_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_tribe
    FOREIGN KEY (tribe_id) REFERENCES tribe_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_bt
    FOREIGN KEY (battle_type_id) REFERENCES battle_type_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_shot
    FOREIGN KEY (shot_skill_id) REFERENCES shot_skill_master(id)
    ON UPDATE RESTRICT ON DELETE SET NULL,

  CONSTRAINT fk_mm_assist
    FOREIGN KEY (assist_skill_id) REFERENCES assist_skill_master(id)
    ON UPDATE RESTRICT ON DELETE SET NULL,

  CONSTRAINT fk_mm_ss_name
    FOREIGN KEY (strike_shot_name_id) REFERENCES strike_shot_name_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_ss_effect
    FOREIGN KEY (strike_shot_effect_id) REFERENCES strike_shot_effect_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_fc_main
    FOREIGN KEY (friendship_combo_id) REFERENCES sub_friendship_combo_name_master(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_mm_fc_sub
    FOREIGN KEY (sub_friendship_combo_id) REFERENCES sub_friendship_combo_name_master(id)
    ON UPDATE RESTRICT ON DELETE SET NULL,

  CONSTRAINT fk_mm_series
    FOREIGN KEY (series_info_id) REFERENCES series_info_master(id)
    ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB;
