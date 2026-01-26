package com.monst.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AbilityRepository {

    private final JdbcTemplate jdbcTemplate;

    public AbilityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<Long, String> findNamesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return Map.of();

        String in = ids.stream().map(x -> "?").reduce((a, b) -> a + "," + b).orElse("?");
        String sql = "SELECT id, name FROM ability_master WHERE id IN (" + in + ")";

        Object[] params = ids.toArray();
        List<Map.Entry<Long, String>> rows = jdbcTemplate.query(sql,
                (rs, rowNum) -> Map.entry(rs.getLong("id"), rs.getString("name")), params);

        Map<Long, String> map = new HashMap<>();
        for (var e : rows)
            map.put(e.getKey(), e.getValue());
        return map;
    }
}
