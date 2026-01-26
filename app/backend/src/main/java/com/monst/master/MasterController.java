package com.monst.master;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{type}/select/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> selectAll(@PathVariable String type) {
        return masterService.selectAll(MasterType.fromPath(type));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/{type}/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse register(@PathVariable String type, @RequestBody JsonNode body) {
        long id = masterService.register(MasterType.fromPath(type), body);
        return new IdResponse(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{type}/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public IdResponse update(@PathVariable String type, @PathVariable long id, @RequestBody JsonNode body) {
        long updatedId = masterService.update(MasterType.fromPath(type), id, body);
        return new IdResponse(updatedId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/{type}/register-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse registerWithImage(
            @PathVariable String type,
            @RequestPart("data") String json,
            @RequestPart("image") MultipartFile image) throws Exception {

        MasterType mt = MasterType.fromPath(type);
        if (!mt.supportsImage()) {
            throw new IllegalArgumentException("This master type does not support image: " + type);
        }

        ObjectNode body = (ObjectNode) objectMapper.readTree(json);

        // 画像保存 → image_path を JSON に注入
        String imagePath = masterImageService.save(image, type);
        body.put("image_path", Objects.requireNonNull(imagePath));

        long id = masterService.register(mt, body);
        return new IdResponse(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{type}/update-with-image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public IdResponse updateWithImage(
            @PathVariable String type,
            @PathVariable long id,
            @RequestPart("data") String json,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        MasterType mt = MasterType.fromPath(type);
        if (!mt.supportsImage()) {
            throw new IllegalArgumentException("This master type does not support image: " + type);
        }

        ObjectNode body = (ObjectNode) objectMapper.readTree(json);

        // image がある時だけ差し替える（無ければ JSON の image_path をそのまま使う運用）
        if (image != null && !image.isEmpty()) {
            String imagePath = masterImageService.save(image, type);
            body.put("image_path", Objects.requireNonNull(imagePath));
        } else {
            // null にしたくないなら「送ってきた JSON に image_path を入れる」運用に寄せる
            if (!body.has("image_path")) {
                body.putNull("image_path");
            }
        }

        long updatedId = masterService.update(mt, id, body);
        return new IdResponse(updatedId);
    }

    public record IdResponse(long id) {
    }
}
