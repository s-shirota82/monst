package com.monst.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MonsterFullQueryRepository {

  private final JdbcTemplate jdbcTemplate;

  public MonsterFullQueryRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public long count(String q, Integer rarity, Long attributeId, Long tribeId, Long battleTypeId) {
    StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM monster_main m
        WHERE 1=1
        """);

    List<Object> params = new ArrayList<>();
    appendWhere(sql, params, q, rarity, attributeId, tribeId, battleTypeId);

    String sqlStr = Objects.requireNonNull(sql.toString());
    Long total = jdbcTemplate.queryForObject(sqlStr, Long.class, params.toArray(Object[]::new));
    return total == null ? 0L : total;
  }

  public List<Row> findPage(
      String q,
      Integer rarity,
      Long attributeId,
      Long tribeId,
      Long battleTypeId,
      int page,
      int size) {
    StringBuilder sql = new StringBuilder(baseSelectSql());

    List<Object> params = new ArrayList<>();
    appendWhere(sql, params, q, rarity, attributeId, tribeId, battleTypeId);

    sql.append(" ORDER BY m.number ASC ");
    sql.append(" LIMIT ? OFFSET ? ");
    params.add(size);
    params.add(page * size);

    String sqlStr = Objects.requireNonNull(sql.toString());
    return jdbcTemplate.query(sqlStr, this::mapRow, params.toArray(Object[]::new));
  }

  public Optional<Row> findById(long id) {
    StringBuilder sql = new StringBuilder(baseSelectSql());
    sql.append(" AND m.id = ? ");

    String sqlStr = Objects.requireNonNull(sql.toString());
    List<Row> list = jdbcTemplate.query(sqlStr, this::mapRow, id);
    return list.stream().findFirst();
  }

  private String baseSelectSql() {
    return """
        SELECT
          m.id,
          m.number,
          m.name,

          m.rarity AS rarity_value,
          rm.max_level AS rarity_max_level,

          esm.name AS evo_name,
          esm.level_cap_release AS evo_level_cap_release,
          esm.super_battle_release AS evo_super_battle_release,

          am.name  AS attribute_name,
          am.image_path AS attribute_image_path,

          htm.name AS hit_type_name,
          tm.name  AS tribe_name,
          btm.name AS battle_type_name,

          m.hp_max, m.hp_plus_max,
          m.attack_max, m.attack_plus_max,
          m.speed_max, m.speed_plus_max,

          lsm.name AS luck_skill_name,
          lsm.image_path AS luck_skill_image_path,

          m.base_ability,
          m.gauge_ability,
          m.connect_skill,

          ssm.name AS shot_skill_name,
          asm.name AS assist_skill_name,

          ssnm.name AS ss_name,
          ssem.effect AS ss_effect,

          sinf.name AS series_name,

          m.icon_image,
          m.monster_image,

          -- main friendship
          main_fc.name AS main_fc_name,
          main_fc.image_path AS main_fc_image_path,
          main_attr.name AS main_fc_attribute,
          main_attr.image_path AS main_fc_attribute_image_path,
          main_cat.name AS main_fc_category,
          main_fc.description AS main_fc_description,

          -- sub friendship
          sub_fc.name AS sub_fc_name,
          sub_fc.image_path AS sub_fc_image_path,
          sub_attr.name AS sub_fc_attribute,
          sub_attr.image_path AS sub_fc_attribute_image_path,
          sub_cat.name AS sub_fc_category,
          sub_fc.description AS sub_fc_description,

          -- main power
          main_p.power AS main_fc_power,

          -- sub power
          sub_p.power AS sub_fc_power

        FROM monster_main m
        JOIN rarity_master rm
          ON rm.rarity = m.rarity

        JOIN evolution_stage_master esm
          ON esm.id = m.evolution_stage_id

        JOIN attribute_master am
          ON am.id = m.attribute_id

        JOIN hit_type_master htm
          ON htm.id = m.hit_type_id

        JOIN tribe_master tm
          ON tm.id = m.tribe_id

        JOIN battle_type_master btm
          ON btm.id = m.battle_type_id

        LEFT JOIN luck_skill_master lsm
          ON lsm.id = m.luck_skill_id

        LEFT JOIN shot_skill_master ssm
          ON ssm.id = m.shot_skill_id

        LEFT JOIN assist_skill_master asm
          ON asm.id = m.assist_skill_id

        JOIN strike_shot_name_master ssnm
          ON ssnm.id = m.strike_shot_name_id

        JOIN strike_shot_effect_master ssem
          ON ssem.id = m.strike_shot_effect_id

        LEFT JOIN series_info_master sinf
          ON sinf.id = m.series_info_id

        -- main friendship join
        JOIN sub_friendship_combo_name_master main_fc
          ON main_fc.id = m.friendship_combo_id
        JOIN attribute_master main_attr
          ON main_attr.id = main_fc.attribute_id
        JOIN sub_friendship_combo_category_master main_cat
          ON main_cat.id = main_fc.category_id

        -- sub friendship join (nullable)
        LEFT JOIN sub_friendship_combo_name_master sub_fc
          ON sub_fc.id = m.sub_friendship_combo_id
        LEFT JOIN attribute_master sub_attr
          ON sub_attr.id = sub_fc.attribute_id
        LEFT JOIN sub_friendship_combo_category_master sub_cat
          ON sub_cat.id = sub_fc.category_id

        -- power (main)
        LEFT JOIN sub_friendship_combo_power_master main_p
          ON main_p.friendship_combo_name_id = main_fc.id
         AND main_p.rarity = m.rarity
         AND main_p.battle_type_id = m.battle_type_id
         AND main_p.is_attribute_match = CASE WHEN main_fc.attribute_id = m.attribute_id THEN 1 ELSE 0 END

        -- power (sub)
        LEFT JOIN sub_friendship_combo_power_master sub_p
          ON sub_p.friendship_combo_name_id = sub_fc.id
         AND sub_p.rarity = m.rarity
         AND sub_p.battle_type_id = m.battle_type_id
         AND sub_p.is_attribute_match = CASE WHEN sub_fc.attribute_id = m.attribute_id THEN 1 ELSE 0 END

        WHERE 1=1
        """;
  }

  private void appendWhere(
      StringBuilder sql,
      List<Object> params,
      String q,
      Integer rarity,
      Long attributeId,
      Long tribeId,
      Long battleTypeId) {
    if (q != null && !q.isBlank()) {
      sql.append(" AND m.name LIKE ? ");
      params.add("%" + q + "%");
    }
    if (rarity != null) {
      sql.append(" AND m.rarity = ? ");
      params.add(rarity);
    }
    if (attributeId != null) {
      sql.append(" AND m.attribute_id = ? ");
      params.add(attributeId);
    }
    if (tribeId != null) {
      sql.append(" AND m.tribe_id = ? ");
      params.add(tribeId);
    }
    if (battleTypeId != null) {
      sql.append(" AND m.battle_type_id = ? ");
      params.add(battleTypeId);
    }
  }

  private Row mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Row(
        rs.getLong("id"),
        rs.getInt("number"),
        rs.getString("name"),

        rs.getInt("rarity_value"),
        rs.getInt("rarity_max_level"),

        rs.getString("evo_name"),
        rs.getInt("evo_level_cap_release"),
        rs.getInt("evo_super_battle_release"),

        rs.getString("attribute_name"),
        rs.getString("attribute_image_path"),

        rs.getString("hit_type_name"),
        rs.getString("tribe_name"),
        rs.getString("battle_type_name"),

        rs.getInt("hp_max"),
        rs.getInt("hp_plus_max"),
        rs.getInt("attack_max"),
        rs.getInt("attack_plus_max"),
        rs.getBigDecimal("speed_max"),
        rs.getBigDecimal("speed_plus_max"),

        rs.getString("luck_skill_name"),
        rs.getString("luck_skill_image_path"),

        rs.getString("base_ability"),
        rs.getString("gauge_ability"),
        rs.getString("connect_skill"),

        rs.getString("shot_skill_name"),
        rs.getString("assist_skill_name"),

        rs.getString("ss_name"),
        rs.getString("ss_effect"),

        rs.getString("series_name"),

        rs.getString("icon_image"),
        rs.getString("monster_image"),

        rs.getString("main_fc_name"),
        rs.getString("main_fc_image_path"),
        rs.getString("main_fc_attribute"),
        rs.getString("main_fc_attribute_image_path"),
        rs.getString("main_fc_category"),
        rs.getString("main_fc_description"),
        (Integer) rs.getObject("main_fc_power"),

        rs.getString("sub_fc_name"),
        rs.getString("sub_fc_image_path"),
        rs.getString("sub_fc_attribute"),
        rs.getString("sub_fc_attribute_image_path"),
        rs.getString("sub_fc_category"),
        rs.getString("sub_fc_description"),
        (Integer) rs.getObject("sub_fc_power"));
  }

  public record Row(
      long id,
      int number,
      String name,

      int rarityValue,
      int rarityMaxLevel,

      String evoName,
      int evoLevelCapRelease,
      int evoSuperBattleRelease,

      String attributeName,
      String attributeImagePath,

      String hitTypeName,
      String tribeName,
      String battleTypeName,

      int hpMax,
      int hpPlusMax,
      int attackMax,
      int attackPlusMax,
      java.math.BigDecimal speedMax,
      java.math.BigDecimal speedPlusMax,

      String luckSkillName,
      String luckSkillImagePath,

      String baseAbilityJson,
      String gaugeAbilityJson,
      String connectSkillJson,

      String shotSkillName,
      String assistSkillName,

      String ssName,
      String ssEffect,

      String seriesName,

      String iconImage,
      String monsterImage,

      String mainFcName,
      String mainFcImagePath,
      String mainFcAttribute,
      String mainFcAttributeImagePath,
      String mainFcCategory,
      String mainFcDescription,
      Integer mainFcPower,

      String subFcName,
      String subFcImagePath,
      String subFcAttribute,
      String subFcAttributeImagePath,
      String subFcCategory,
      String subFcDescription,
      Integer subFcPower) {
  }
}
