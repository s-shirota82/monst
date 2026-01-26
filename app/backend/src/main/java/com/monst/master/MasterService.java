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
        Map<String, Object> values = parseValues(type, body);
        return repo.insert(type, values);
    }

    public long update(MasterType type, long id, JsonNode body) {
        Map<String, Object> values = parseValues(type, body);
        int updated = repo.updateById(type, id, values);
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Master record not found: id=" + id);
        }
        return id;
    }

    private Map<String, Object> parseValues(MasterType type, JsonNode body) {
        if (body == null || !body.isObject()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "body must be JSON object");
        }

        Map<String, Object> map = new HashMap<>();
        for (String col : type.upsertColumns()) {
            JsonNode v = body.get(col);

            // image_path は NULL 許容にしたので「無くてもOK」にする
            if ("image_path".equals(col)) {
                if (v == null || v.isNull()) {
                    map.put(col, null);
                } else {
                    map.put(col, v.asText());
                }
                continue;
            }

            if (v == null || v.isNull()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing field: " + col);
            }

            if (v.isBoolean()) {
                map.put(col, v.asBoolean() ? 1 : 0);
            } else if (v.isInt() || v.isLong()) {
                map.put(col, v.asLong());
            } else {
                map.put(col, v.asText());
            }
        }
        return map;
    }
}
