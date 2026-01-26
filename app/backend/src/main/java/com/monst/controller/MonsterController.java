package com.monst.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monst.dto.request.MonsterCreateRequest;
import com.monst.dto.response.MonsterCreateResponse;
import com.monst.dto.response.MonsterFullListResponse;
import com.monst.dto.response.MonsterFullResponse;
import com.monst.dto.response.MonsterUpdateResponse;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/monster")
public class MonsterController {

    private final MonsterService monsterService;
    private final MonsterFullQueryService monsterFullQueryService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public MonsterController(
            MonsterService monsterService,
            MonsterFullQueryService monsterFullQueryService,
            ObjectMapper objectMapper,
            Validator validator) {
        this.monsterService = monsterService;
        this.monsterFullQueryService = monsterFullQueryService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MonsterCreateResponse register(
            @RequestPart("data") String json,
            @RequestPart("iconImage") MultipartFile iconImage,
            @RequestPart("monsterImage") MultipartFile monsterImage) throws Exception {

        MonsterCreateRequest request = objectMapper.readValue(json, MonsterCreateRequest.class);

        Set<ConstraintViolation<MonsterCreateRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed: ");
            for (var v : violations) {
                sb.append(v.getPropertyPath()).append(" ").append(v.getMessage()).append("; ");
            }
            String message = Objects.requireNonNull(sb.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        long id = monsterService.create(request, iconImage, monsterImage);
        return new MonsterCreateResponse(id);
    }

    /**
     * モンスター一覧取得（詳細レスポンス形式）
     * GET /monster/select/all
     * - includeImages=true のとき base64 を含める
     */
    @GetMapping(path = "/select/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public MonsterFullListResponse selectAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer rarity,
            @RequestParam(required = false) Long attributeId,
            @RequestParam(required = false) Long tribeId,
            @RequestParam(required = false) Long battleTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeImages) {
        return monsterFullQueryService.selectAll(
                q,
                rarity,
                attributeId,
                tribeId,
                battleTypeId,
                page,
                size,
                includeImages);
    }

    @GetMapping(path = "/select/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MonsterFullResponse selectById(@PathVariable long id) {
        return monsterFullQueryService.selectById(id);
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MonsterUpdateResponse update(
            @PathVariable long id,
            @RequestPart("data") String json,
            @RequestPart(value = "iconImage", required = false) MultipartFile iconImage,
            @RequestPart(value = "monsterImage", required = false) MultipartFile monsterImage) throws Exception {

        MonsterCreateRequest request = objectMapper.readValue(json, MonsterCreateRequest.class);

        Set<ConstraintViolation<MonsterCreateRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed: ");
            for (var v : violations) {
                sb.append(v.getPropertyPath()).append(" ").append(v.getMessage()).append("; ");
            }
            String message = Objects.requireNonNull(sb.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        long updatedId = monsterService.update(id, request, iconImage, monsterImage);
        return new MonsterUpdateResponse(updatedId);
    }
}
