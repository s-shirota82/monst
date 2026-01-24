package com.monst.service;

import com.monst.dto.request.MonsterCreateRequest;
import com.monst.exception.BadRequestException;
import com.monst.repository.MasterRepository;
import com.monst.repository.MonsterRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MonsterService {

    private final MonsterRepository monsterRepository;
    private final MasterRepository masterRepository;

    // uploads/monsters が基点
    private static final @NonNull Path IMAGE_ROOT = Paths.get("uploads", "monsters");

    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "webp");

    public MonsterService(
            MonsterRepository monsterRepository,
            MasterRepository masterRepository) {
        this.monsterRepository = monsterRepository;
        this.masterRepository = masterRepository;
    }

    @Transactional
    public long create(
            @NonNull MonsterCreateRequest req,
            @NonNull MultipartFile iconImage,
            @NonNull MultipartFile monsterImage) {

        // =========================
        // 重複チェック
        // =========================
        if (monsterRepository.existsByNumber(req.getNumber())) {
            throw new BadRequestException("number is already registered: " + req.getNumber());
        }

        // =========================
        // マスタ存在チェック
        // =========================
        mustExist("rarity_master", "rarity", req.getRarity(), "rarity");
        mustExist("evolution_stage_master", "id", req.getEvolutionStageId(), "evolutionStageId");
        mustExist("attribute_master", "id", req.getAttributeId(), "attributeId");
        mustExist("hit_type_master", "id", req.getHitTypeId(), "hitTypeId");
        mustExist("tribe_master", "id", req.getTribeId(), "tribeId");
        mustExist("battle_type_master", "id", req.getBattleTypeId(), "battleTypeId");

        if (req.getLuckSkillId() != null) {
            mustExist("luck_skill_master", "id", req.getLuckSkillId(), "luckSkillId");
        }
        if (req.getShotSkillId() != null) {
            mustExist("shot_skill_master", "id", req.getShotSkillId(), "shotSkillId");
        }
        if (req.getAssistSkillId() != null) {
            mustExist("assist_skill_master", "id", req.getAssistSkillId(), "assistSkillId");
        }
        if (req.getSubFriendshipComboId() != null) {
            mustExist(
                    "sub_friendship_combo_name_master",
                    "id",
                    req.getSubFriendshipComboId(),
                    "subFriendshipComboId");
        }
        if (req.getSeriesInfoId() != null) {
            mustExist("series_info_master", "id", req.getSeriesInfoId(), "seriesInfoId");
        }

        mustExist(
                "strike_shot_name_master",
                "id",
                req.getStrikeShotNameId(),
                "strikeShotNameId");
        mustExist(
                "strike_shot_effect_master",
                "id",
                req.getStrikeShotEffectId(),
                "strikeShotEffectId");
        mustExist(
                "sub_friendship_combo_name_master",
                "id",
                req.getFriendshipComboId(),
                "friendshipComboId");

        // =========================
        // 画像保存
        // =========================
        String iconPath = saveImage(iconImage, "icon");
        String monsterPath = saveImage(monsterImage, "monster");

        // =========================
        // DB 登録
        // =========================
        return monsterRepository.insert(req, iconPath, monsterPath);
    }

    /**
     * マスタ存在チェック（Service 層の責務）
     */
    private void mustExist(
            @NonNull String table,
            @NonNull String column,
            long value,
            @NonNull String fieldName) {
        if (!masterRepository.existsByValue(table, column, value)) {
            throw new BadRequestException(fieldName + " not found: " + value);
        }
    }

    /**
     * 画像保存（uploads/monsters/{icon|monster}/）
     */
    private @NonNull String saveImage(
            @NonNull MultipartFile file,
            @NonNull String type) {
        if (file.isEmpty()) {
            throw new BadRequestException(type + " image is empty");
        }

        String ext = getExtensionLower(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BadRequestException(type + " image extension not allowed: " + ext);
        }

        try {
            Path dir = IMAGE_ROOT.resolve(type);
            Files.createDirectories(dir);

            String filename = type + "_" + UUID.randomUUID() + "." + ext;

            Path resolved = dir.resolve(filename);
            Path target = Objects.requireNonNull(resolved, "resolved image path must not be null");

            file.transferTo(target);

            return "uploads/monsters/" + type + "/" + filename;

        } catch (Exception e) {
            throw new IllegalStateException("failed to save image: " + type, e);
        }
    }

    private @NonNull String getExtensionLower(String filename) {
        if (filename == null)
            return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1)
            return "";
        return filename.substring(idx + 1).toLowerCase();
    }
}
