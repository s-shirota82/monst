package com.monst.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monst.dto.request.MonsterCreateRequest;
import com.monst.dto.response.MonsterCreateResponse;
import com.monst.dto.response.MonsterFullListResponse;
import com.monst.dto.response.MonsterFullResponse;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * 管理者のみ：モンスター登録
     * POST /monster/register
     */
    @PreAuthorize("hasRole('ADMIN')")
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
     * 管理者のみ：モンスター更新
     * PUT /monster/update/{id}
     * （あなたの Update 実装に合わせて body/part を調整してください）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public MonsterCreateResponse update(
            @PathVariable long id,
            @RequestPart("data") String json,
            @RequestPart(value = "iconImage", required = false) MultipartFile iconImage,
            @RequestPart(value = "monsterImage", required = false) MultipartFile monsterImage) throws Exception {

        MonsterCreateRequest request = objectMapper.readValue(json, MonsterCreateRequest.class);

        // update は service 側で id も含めて処理する想定（必要なら MonsterUpdateRequest を別途作る）
        long updatedId = monsterService.update(id, request, iconImage, monsterImage);
        return new MonsterCreateResponse(updatedId);
    }

    /**
     * 公開：モンスター一覧
     * GET /monster/select/all
     */
    @GetMapping(path = "/select/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public MonsterFullListResponse selectAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer rarity,
            @RequestParam(required = false) Long attributeId,
            @RequestParam(required = false) Long tribeId,
            @RequestParam(required = false) Long battleTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return monsterFullQueryService.selectAll(
                q, rarity, attributeId, tribeId, battleTypeId, page, size);
    }

    /**
     * 公開：モンスター詳細
     * GET /monster/select/{id}
     */
    @GetMapping(path = "/select/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MonsterFullResponse selectById(@PathVariable long id) {
        return monsterFullQueryService.selectById(id);
    }
}
