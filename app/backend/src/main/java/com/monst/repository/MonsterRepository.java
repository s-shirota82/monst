package com.monst.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monst.dto.request.MonsterCreateRequest;

import java.sql.Types;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MonsterRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public MonsterRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean existsByNumber(int number) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM monster_main WHERE number = ?",
                Integer.class,
                number);
        return count != null && count > 0;
    }

    public long insert(MonsterCreateRequest req, String iconImagePath, String monsterImagePath) {
        final String sql = """
                INSERT INTO monster_main (
                  number, rarity, name,
                  evolution_stage_id, attribute_id,
                  hp_max, hp_plus_max, attack_max, attack_plus_max, speed_max, speed_plus_max,
                  luck_skill_id, hit_type_id, tribe_id, battle_type_id,
                  base_ability, gauge_ability, connect_skill,
                  shot_skill_id, assist_skill_id,
                  strike_shot_name_id, strike_shot_effect_id,
                  friendship_combo_id, sub_friendship_combo_id,
                  special_note, series_info_id,
                  icon_image, monster_image
                ) VALUES (
                  ?, ?, ?,
                  ?, ?,
                  ?, ?, ?, ?, ?, ?,
                  ?, ?, ?, ?,
                  CAST(? AS JSON), CAST(? AS JSON), CAST(? AS JSON),
                  ?, ?,
                  ?, ?,
                  ?, ?,
                  ?, ?,
                  ?, ?
                )
                """;

        String baseAbilityJson = toJsonOrNull(req.getBaseAbility());
        String gaugeAbilityJson = toJsonOrNull(req.getGaugeAbility());
        String connectSkillJson = toJsonOrNull(req.getConnectSkill());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sql, new String[] { "id" });

            int i = 1;
            ps.setInt(i++, req.getNumber());
            ps.setInt(i++, req.getRarity());
            ps.setString(i++, req.getName());

            ps.setLong(i++, req.getEvolutionStageId());
            ps.setLong(i++, req.getAttributeId());

            ps.setInt(i++, req.getHpMax());
            ps.setInt(i++, req.getHpPlusMax());
            ps.setInt(i++, req.getAttackMax());
            ps.setInt(i++, req.getAttackPlusMax());
            ps.setBigDecimal(i++, req.getSpeedMax());
            ps.setBigDecimal(i++, req.getSpeedPlusMax());

            setNullableLong(ps, i++, req.getLuckSkillId());
            ps.setLong(i++, req.getHitTypeId());
            ps.setLong(i++, req.getTribeId());
            ps.setLong(i++, req.getBattleTypeId());

            setNullableJson(ps, i++, baseAbilityJson);
            setNullableJson(ps, i++, gaugeAbilityJson);
            setNullableJson(ps, i++, connectSkillJson);

            setNullableLong(ps, i++, req.getShotSkillId());
            setNullableLong(ps, i++, req.getAssistSkillId());

            ps.setLong(i++, req.getStrikeShotNameId());
            ps.setLong(i++, req.getStrikeShotEffectId());

            ps.setLong(i++, req.getFriendshipComboId());
            setNullableLong(ps, i++, req.getSubFriendshipComboId());

            ps.setInt(i++, req.getSpecialNote());
            setNullableLong(ps, i++, req.getSeriesInfoId());

            // 画像はパスを保存
            ps.setString(i++, iconImagePath);
            ps.setString(i++, monsterImagePath);

            return ps;
        }, keyHolder);

        var key = keyHolder.getKey();
        if (key == null)
            throw new IllegalStateException("Failed to get generated id");
        return key.longValue();
    }

    private String toJsonOrNull(JsonNode node) {
        if (node == null)
            return null;
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON field", e);
        }
    }

    private void setNullableLong(java.sql.PreparedStatement ps, int index, Long value) throws java.sql.SQLException {
        if (value == null)
            ps.setNull(index, Types.BIGINT);
        else
            ps.setLong(index, value);
    }

    private void setNullableJson(java.sql.PreparedStatement ps, int index, String json) throws java.sql.SQLException {
        if (json == null)
            ps.setNull(index, Types.VARCHAR);
        else
            ps.setString(index, json);
    }
}
