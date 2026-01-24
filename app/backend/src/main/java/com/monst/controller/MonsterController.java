package com.monst.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monst.dto.request.MonsterCreateRequest;
import com.monst.dto.response.MonsterCreateResponse;
import com.monst.service.MonsterService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

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
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public MonsterController(
            MonsterService monsterService,
            ObjectMapper objectMapper,
            Validator validator) {
        this.monsterService = monsterService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    /**
     * モンスター登録
     * POST /monster/register
     * Content-Type: multipart/form-data
     */
    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED) // 201
    public MonsterCreateResponse register(
            @RequestPart("data") String json,
            @RequestPart("iconImage") MultipartFile iconImage,
            @RequestPart("monsterImage") MultipartFile monsterImage) throws Exception {

        MonsterCreateRequest request = objectMapper.readValue(json, MonsterCreateRequest.class);

        // @Valid は @RequestPart String に効かないため手動バリデーション
        Set<ConstraintViolation<MonsterCreateRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed: ");
            for (var v : violations) {
                sb.append(v.getPropertyPath())
                        .append(" ")
                        .append(v.getMessage())
                        .append("; ");
            }
            // ★ 400 を確定させる（IllegalArgumentException は 500 に吸われる）
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, sb.toString());
        }

        long id = monsterService.create(request, iconImage, monsterImage);
        return new MonsterCreateResponse(id);
    }
}
