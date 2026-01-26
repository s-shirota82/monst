package com.monst.master;

import java.util.List;

public enum MasterType {

    // 画像あり
    ATTRIBUTE("attribute_master", List.of("id", "name", "image_path"), List.of("name", "image_path"), true),
    LUCK_SKILL("luck_skill_master", List.of("id", "name", "image_path"), List.of("name", "image_path"), true),
    FRIENDSHIP_NAME(
            "sub_friendship_combo_name_master",
            List.of("id", "name", "attribute_id", "category_id", "description", "image_path"),
            List.of("name", "attribute_id", "category_id", "description", "image_path"),
            true),

    // 画像なし（既存どおり）
    TRIBE("tribe_master", List.of("id", "name"), List.of("name"), false),
    BATTLE_TYPE("battle_type_master", List.of("id", "name"), List.of("name"), false),
    HIT_TYPE("hit_type_master", List.of("id", "name"), List.of("name"), false),
    SHOT_SKILL("shot_skill_master", List.of("id", "name"), List.of("name"), false),
    ASSIST_SKILL("assist_skill_master", List.of("id", "name"), List.of("name"), false),
    STRIKE_SHOT_NAME("strike_shot_name_master", List.of("id", "name"), List.of("name"), false),
    SERIES_INFO("series_info_master", List.of("id", "name"), List.of("name"), false),
    ABILITY_STAGE("ability_stage_master", List.of("id", "stage"), List.of("stage"), false),

    RARITY("rarity_master", List.of("id", "rarity", "max_level", "label"), List.of("rarity", "max_level", "label"),
            false),
    EVOLUTION_STAGE(
            "evolution_stage_master",
            List.of("id", "name", "level_cap_release", "super_battle_release"),
            List.of("name", "level_cap_release", "super_battle_release"),
            false),
    ABILITY("ability_master", List.of("id", "name", "is_super", "has_stage"), List.of("name", "is_super", "has_stage"),
            false),

    STRIKE_SHOT_EFFECT("strike_shot_effect_master", List.of("id", "name_id", "effect"), List.of("name_id", "effect"),
            false),

    FRIENDSHIP_CATEGORY("sub_friendship_combo_category_master", List.of("id", "name"), List.of("name"), false),
    FRIENDSHIP_POWER(
            "sub_friendship_combo_power_master",
            List.of("id", "friendship_combo_name_id", "rarity", "battle_type_id", "is_attribute_match", "power"),
            List.of("friendship_combo_name_id", "rarity", "battle_type_id", "is_attribute_match", "power"),
            false);

    private final String table;
    private final List<String> selectColumns;
    private final List<String> upsertColumns;
    private final boolean supportsImage;

    MasterType(String table, List<String> selectColumns, List<String> upsertColumns, boolean supportsImage) {
        this.table = table;
        this.selectColumns = selectColumns;
        this.upsertColumns = upsertColumns;
        this.supportsImage = supportsImage;
    }

    public String table() {
        return table;
    }

    public List<String> selectColumns() {
        return selectColumns;
    }

    public List<String> upsertColumns() {
        return upsertColumns;
    }

    public boolean supportsImage() {
        return supportsImage;
    }

    public static MasterType fromPath(String type) {
        return switch (type) {
            case "attribute" -> ATTRIBUTE;
            case "luckSkill" -> LUCK_SKILL;
            case "friendshipName" -> FRIENDSHIP_NAME;

            case "tribe" -> TRIBE;
            case "battleType" -> BATTLE_TYPE;
            case "hitType" -> HIT_TYPE;
            case "shotSkill" -> SHOT_SKILL;
            case "assistSkill" -> ASSIST_SKILL;
            case "strikeShotName" -> STRIKE_SHOT_NAME;
            case "seriesInfo" -> SERIES_INFO;
            case "abilityStage" -> ABILITY_STAGE;

            case "rarity" -> RARITY;
            case "evolutionStage" -> EVOLUTION_STAGE;
            case "ability" -> ABILITY;
            case "strikeShotEffect" -> STRIKE_SHOT_EFFECT;

            case "friendshipCategory" -> FRIENDSHIP_CATEGORY;
            case "friendshipPower" -> FRIENDSHIP_POWER;

            default -> throw new IllegalArgumentException("Unknown master type: " + type);
        };
    }
}
