package com.monst.master;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MasterRepository {

    private final JdbcTemplate jdbcTemplate;

    public MasterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> selectAll(MasterType type) {
        String cols = String.join(", ", type.selectColumns());
        String sql = "SELECT " + cols + " FROM " + type.table() + " ORDER BY id ASC";
        return jdbcTemplate.queryForList(sql);
    }

    public long insert(MasterType type, Map<String, Object> values) {
        String cols = String.join(", ", type.upsertColumns());
        String placeholders = type.upsertColumns().stream().map(c -> "?").collect(Collectors.joining(", "));
        String sql = "INSERT INTO " + type.table() + " (" + cols + ") VALUES (" + placeholders + ")";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = Objects.requireNonNull(
                    con.prepareStatement(sql, new String[] { "id" }),
                    "PreparedStatement must not be null");

            int i = 1;
            for (String c : type.upsertColumns()) {
                ps.setObject(i++, values.get(c));
            }
            return ps;
        }, kh);

        var key = kh.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to get generated id");
        }
        return key.longValue();
    }

    public int updateById(MasterType type, long id, Map<String, Object> values) {
        String setClause = type.upsertColumns().stream()
                .map(c -> c + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + type.table() + " SET " + setClause + " WHERE id = ?";

        Object[] params = new Object[type.upsertColumns().size() + 1];
        int i = 0;
        for (String c : type.upsertColumns()) {
            params[i++] = values.get(c);
        }
        params[i] = id;

        return jdbcTemplate.update(sql, params);
    }
}
