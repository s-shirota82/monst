package com.monst.repository;

import com.monst.entity.User;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByEmail(String email) {
        var sql = "SELECT id, email, password, name FROM users WHERE email = ?";
        var list = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name")), email);
        return list.stream().findFirst();
    }

    public long insert(String email, String passwordHash, String name) {
        var sql = "INSERT INTO users(email, password, name) VALUES(?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, name);
            return ps;
        }, keyHolder);

        var key = keyHolder.getKey();
        if (key == null)
            throw new IllegalStateException("Failed to get generated id");
        return key.longValue();
    }
}
