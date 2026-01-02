package com.example.monst;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

  private final JdbcTemplate jdbc;

  public UserController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of("ok", true);
  }

  @GetMapping("/users")
  public List<Map<String, Object>> users() {
    return jdbc.queryForList("SELECT id, name, created_at FROM users ORDER BY id");
  }
}
