package com.monst.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monst.dto.response.MonsterFullListResponse;
import com.monst.dto.response.MonsterFullResponse;
import com.monst.repository.AbilityRepository;
import com.monst.repository.MonsterFullQueryRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MonsterFullQueryService {

    private final MonsterFullQueryRepository repo;
    private final AbilityRepository abilityRepository;
    private final ObjectMapper objectMapper;
    private final ImageBase64Service imageBase64Service;

    public MonsterFullQueryService(
            MonsterFullQueryRepository repo,
            AbilityRepository abilityRepository,
            ObjectMapper objectMapper,
            ImageBase64Service imageBase64Service) {
        this.repo = repo;
        this.abilityRepository = abilityRepository;
        this.objectMapper = objectMapper;
        this.imageBase64Service = imageBase64Service;
    }

    public MonsterFullListResponse selectAll(
            String q,
            Integer rarity,
            Long attributeId,
            Long tribeId,
            Long battleTypeId,
            int page,
            int size,
            boolean includeImages) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        long total = repo.count(q, rarity, attributeId, tribeId, battleTypeId);
        List<MonsterFullQueryRepository.Row> rows = repo.findPage(q, rarity, attributeId, tribeId, battleTypeId,
                safePage, safeSize);

        Set<Long> abilityIds = new HashSet<>();
        for (var r : rows) {
            collectAbilityIds(r.baseAbilityJson(), abilityIds);
            collectAbilityIds(r.gaugeAbilityJson(), abilityIds);
            collectAbilityIdsFromConnect(r.connectSkillJson(), abilityIds);
        }
        Map<Long, String> abilityNameMap = abilityRepository.findNamesByIds(new ArrayList<>(abilityIds));

        List<MonsterFullResponse> items = rows.stream()
                .map(r -> toResponse(r, abilityNameMap, includeImages))
                .toList();

        return new MonsterFullListResponse(items, safePage, safeSize, total);
    }

    public MonsterFullResponse selectById(long id) {
        MonsterFullQueryRepository.Row row = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monster not found: " + id));

        Set<Long> abilityIds = new HashSet<>();
        collectAbilityIds(row.baseAbilityJson(), abilityIds);
        collectAbilityIds(row.gaugeAbilityJson(), abilityIds);
        collectAbilityIdsFromConnect(row.connectSkillJson(), abilityIds);

        Map<Long, String> abilityNameMap = abilityRepository.findNamesByIds(new ArrayList<>(abilityIds));

        // 詳細は常に画像を含める（必要なら引数化）
        return toResponse(row, abilityNameMap, true);
    }

    private MonsterFullResponse toResponse(
            MonsterFullQueryRepository.Row r,
            Map<Long, String> abilityNameMap,
            boolean includeImages) {
        MonsterFullResponse.Rarity rarity = new MonsterFullResponse.Rarity(r.rarityValue(), r.rarityMaxLevel());

        MonsterFullResponse.EvolutionStage evo = new MonsterFullResponse.EvolutionStage(
                r.evoName(),
                r.evoLevelCapRelease() != 0,
                r.evoSuperBattleRelease() != 0);

        MonsterFullResponse.NamedImage attribute = new MonsterFullResponse.NamedImage(
                r.attributeName(),
                imageBase64Service.load(r.attributeImagePath(), includeImages));

        MonsterFullResponse.NamedImage luckSkill = null;
        if (r.luckSkillName() != null) {
            luckSkill = new MonsterFullResponse.NamedImage(
                    r.luckSkillName(),
                    imageBase64Service.load(r.luckSkillImagePath(), includeImages));
        }

        MonsterFullResponse.Status status = new MonsterFullResponse.Status(
                new MonsterFullResponse.Status.Stat(r.hpMax(), r.hpPlusMax()),
                new MonsterFullResponse.Status.Stat(r.attackMax(), r.attackPlusMax()),
                new MonsterFullResponse.Status.Speed(r.speedMax(), r.speedPlusMax()));

        MonsterFullResponse.Abilities abilities = new MonsterFullResponse.Abilities(
                expandAbilities(r.baseAbilityJson(), abilityNameMap),
                expandAbilities(r.gaugeAbilityJson(), abilityNameMap));

        MonsterFullResponse.ConnectSkill connectSkill = expandConnectSkill(r.connectSkillJson(), abilityNameMap);

        MonsterFullResponse.Skills skills = new MonsterFullResponse.Skills(r.shotSkillName(), r.assistSkillName());

        MonsterFullResponse.StrikeShot ss = new MonsterFullResponse.StrikeShot(r.ssName(), r.ssEffect());

        MonsterFullResponse.FriendshipCombo.Friendship main = new MonsterFullResponse.FriendshipCombo.Friendship(
                r.mainFcName(),
                new MonsterFullResponse.NamedImage(
                        r.mainFcAttribute(),
                        imageBase64Service.load(r.mainFcAttributeImagePath(), includeImages)),
                r.mainFcCategory(),
                r.mainFcDescription(),
                r.mainFcPower(),
                imageBase64Service.load(r.mainFcImagePath(), includeImages));

        MonsterFullResponse.FriendshipCombo.Friendship sub = null;
        if (r.subFcName() != null) {
            sub = new MonsterFullResponse.FriendshipCombo.Friendship(
                    r.subFcName(),
                    new MonsterFullResponse.NamedImage(
                            r.subFcAttribute(),
                            imageBase64Service.load(r.subFcAttributeImagePath(), includeImages)),
                    r.subFcCategory(),
                    r.subFcDescription(),
                    r.subFcPower(),
                    imageBase64Service.load(r.subFcImagePath(), includeImages));
        }

        MonsterFullResponse.FriendshipCombo fc = new MonsterFullResponse.FriendshipCombo(main, sub);

        MonsterFullResponse.Images images = new MonsterFullResponse.Images(
                imageBase64Service.load(r.iconImage(), includeImages),
                imageBase64Service.load(r.monsterImage(), includeImages));

        return new MonsterFullResponse(
                r.id(),
                r.number(),
                r.name(),
                rarity,
                evo,
                attribute,
                r.hitTypeName(),
                r.tribeName(),
                r.battleTypeName(),
                status,
                luckSkill,
                abilities,
                connectSkill,
                skills,
                ss,
                fc,
                r.seriesName(),
                images);
    }

    // ===== JSON 展開（既存） =====
    private void collectAbilityIds(String json, Set<Long> out) {
        if (json == null || json.isBlank())
            return;
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node == null || node.isNull())
                return;
            if (node.isArray()) {
                for (JsonNode e : node) {
                    Long id = readAbilityId(e);
                    if (id != null)
                        out.add(id);
                }
            }
        } catch (Exception ignore) {
        }
    }

    private void collectAbilityIdsFromConnect(String json, Set<Long> out) {
        if (json == null || json.isBlank())
            return;
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node == null || node.isNull())
                return;

            JsonNode abilities = node.get("abilities");
            if (abilities != null && abilities.isArray()) {
                for (JsonNode e : abilities) {
                    Long id = readAbilityId(e);
                    if (id != null)
                        out.add(id);
                }
            }
        } catch (Exception ignore) {
        }
    }

    private List<MonsterFullResponse.Ability> expandAbilities(String json, Map<Long, String> abilityNameMap) {
        if (json == null || json.isBlank())
            return List.of();
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node == null || !node.isArray())
                return List.of();

            List<MonsterFullResponse.Ability> list = new ArrayList<>();
            for (JsonNode e : node) {
                Long id = readAbilityId(e);
                String name = (id != null) ? abilityNameMap.getOrDefault(id, "UNKNOWN") : "UNKNOWN";
                String stage = readStage(e);
                list.add(new MonsterFullResponse.Ability(name, stage));
            }
            return list;
        } catch (Exception ex) {
            return List.of();
        }
    }

    private MonsterFullResponse.ConnectSkill expandConnectSkill(String json, Map<Long, String> abilityNameMap) {
        if (json == null || json.isBlank())
            return new MonsterFullResponse.ConnectSkill(null, List.of());
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node == null || !node.isObject())
                return new MonsterFullResponse.ConnectSkill(null, List.of());

            String conditionText = textOrNull(node, "conditionText");
            String condition = (conditionText != null) ? conditionText : textOrNull(node, "condition");

            List<MonsterFullResponse.Ability> list = new ArrayList<>();
            JsonNode abilities = node.get("abilities");
            if (abilities != null && abilities.isArray()) {
                for (JsonNode e : abilities) {
                    Long id = readAbilityId(e);
                    String name = (id != null) ? abilityNameMap.getOrDefault(id, "UNKNOWN") : "UNKNOWN";
                    String stage = readStage(e);
                    list.add(new MonsterFullResponse.Ability(name, stage));
                }
            }
            return new MonsterFullResponse.ConnectSkill(condition, list);
        } catch (Exception ex) {
            return new MonsterFullResponse.ConnectSkill(null, List.of());
        }
    }

    private Long readAbilityId(JsonNode e) {
        if (e == null || e.isNull())
            return null;
        if (e.hasNonNull("abilityId"))
            return e.get("abilityId").asLong();
        if (e.hasNonNull("id"))
            return e.get("id").asLong();
        return null;
    }

    private String readStage(JsonNode e) {
        if (e == null || e.isNull())
            return null;
        if (e.hasNonNull("stage"))
            return e.get("stage").asText();
        return null;
    }

    private String textOrNull(JsonNode node, String key) {
        JsonNode v = node.get(key);
        if (v == null || v.isNull())
            return null;
        return v.asText();
    }
}
