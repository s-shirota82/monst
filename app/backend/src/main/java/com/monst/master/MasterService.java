package com.monst.master;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MasterService {

    private final MasterRepository repo;

    public MasterService(MasterRepository repo) {
        this.repo = repo;
    }

    public List<Map<String, Object>> selectAll(MasterType type) {
        return repo.selectAll(type);
    }

    public long register(MasterType type, JsonNode body) {
        Map<String, Object> values = parseValues(type, body, true);
        return repo.insert(type, values);
    }

    public long update(MasterType type, long id, JsonNode body) {
        Map<String, Object> values = parseValues(type, body, false);
        int updated = repo.updateById(type, id, values);
        if (updated != 1)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Master record not found: id=" + id);
        return id;
    }

    private Map<String, Object> parseValues(MasterType type, JsonNode body, boolean isCreate) {
        if (body == null || !body.isObject()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "body must be JSON object");
        }

        Map<String, Object> map = new HashMap<>();
        for (String col : type.upsertColumns()) {
            JsonNode v = body.get(col);
            if (v == null || v.isNull()) {
                // create は必須、update も必須（完全更新方式）
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing field: " + col);
            }

            // ざっくり型変換（必要なら厳密化）
            if (v.isInt() || v.isLong())
                map.put(col, v.asLong());
            else if (v.isBoolean())
                map.put(col, v.asBoolean() ? 1 : 0);
            else
                map.put(col, v.asText());
        }
        return map;
    }
}
