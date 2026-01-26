package com.monst.master;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/master")
public class MasterController {

    private final MasterService masterService;
    private final MasterImageService masterImageService;
    private final ObjectMapper objectMapper;

    public MasterController(
            MasterService masterService,
            MasterImageService masterImageService,
            ObjectMapper objectMapper) {
        this.masterService = masterService;
        this.masterImageService = masterImageService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/{type}/select/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> selectAll(@PathVariable String type) {
        MasterType mt = MasterType.fromPath(type);
        return masterService.selectAll(mt);
    }

    // ---- JSON版（既存） ----
    @PostMapping(path = "/{type}/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@PathVariable String type, @RequestBody JsonNode body) {
        MasterType mt = MasterType.fromPath(type);
        long id = masterService.register(mt, body);
        return Map.of("id", id);
    }

    @PutMapping(path = "/{type}/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> update(@PathVariable String type, @PathVariable long id, @RequestBody JsonNode body) {
        MasterType mt = MasterType.fromPath(type);
        long updatedId = masterService.update(mt, id, body);
        return Map.of("id", updatedId);
    }

    // ---- 画像あり multipart版（追加） ----
    @PostMapping(path = "/{type}/register-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> registerWithImage(
            @PathVariable String type,
            @RequestPart("data") String json,
            @RequestPart("image") MultipartFile image) throws Exception {
        MasterType mt = MasterType.fromPath(type);
        if (!mt.supportsImage()) {
            throw new IllegalArgumentException("This master type does not support image: " + type);
        }

        ObjectNode body = (ObjectNode) objectMapper.readTree(json);
        String path = masterImageService.save(image, type);
        body.put("image_path", path);

        long id = masterService.register(mt, body);
        return Map.of("id", id);
    }

    @PutMapping(path = "/{type}/update-with-image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateWithImage(
            @PathVariable String type,
            @PathVariable long id,
            @RequestPart("data") String json,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        MasterType mt = MasterType.fromPath(type);
        if (!mt.supportsImage()) {
            throw new IllegalArgumentException("This master type does not support image: " + type);
        }

        ObjectNode body = (ObjectNode) objectMapper.readTree(json);

        // image が来た場合だけ更新。来ない場合は image_path を null にしない（JSONに含めない運用推奨）
        if (image != null && !image.isEmpty()) {
            String path = masterImageService.save(image, type);
            body.put("image_path", path);
        } else {
            // image無しの更新でも image_path が必須にならないように、無ければ null 許容にしている
            if (!body.has("image_path")) {
                body.putNull("image_path");
            }
        }

        long updatedId = masterService.update(mt, id, body);
        return Map.of("id", updatedId);
    }
}
