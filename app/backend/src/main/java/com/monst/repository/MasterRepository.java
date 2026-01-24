package com.monst.repository;

import lombok.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * マスタ存在チェック用（JdbcTemplate）
 *
 * 注意:
 * - table / column は外部入力を渡さないこと（SQLインジェクション対策）
 * - Service側で固定値のみ渡す運用にする
 */
@Repository
public class MasterRepository {

    private final JdbcTemplate jdbcTemplate;

    public MasterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 例: rarity_master の rarity をチェックする用途
     */
    public boolean existsRarity(int rarity) {
        return existsByValue("rarity_master", "rarity", rarity);
    }

    /**
     * id で存在チェック（多くの master に使う用途）
     */
    public boolean existsById(@NonNull String table, long id) {
        return existsByValue(table, "id", id);
    }

    /**
     * 任意カラムで存在チェック（Service側から固定の table/column を渡す前提）
     *
     * @param table  テーブル名（固定値のみ渡す）
     * @param column カラム名（固定値のみ渡す）
     * @param value  値
     */
    public boolean existsByValue(@NonNull String table, @NonNull String column, @NonNull Object value) {
        // table/column はプレースホルダにできないため、外部入力を渡さない運用が必須
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, value);
        return count != null && count > 0;
    }
}
