package com.monst.entity;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.JsonNode;

public record Monster(
        Long id,
        Integer number,
        Integer rarity,
        String name,
        Long evolutionStageId,
        Long attributeId,
        Integer hpMax,
        Integer hpPlusMax,
        Integer attackMax,
        Integer attackPlusMax,
        BigDecimal speedMax,
        BigDecimal speedPlusMax,
        Long luckSkillId,
        Long hitTypeId,
        Long tribeId,
        Long battleTypeId,
        JsonNode baseAbility,
        JsonNode gaugeAbility,
        JsonNode connectSkill,
        Long shotSkillId,
        Long assistSkillId,
        Long strikeShotNameId,
        Long strikeShotEffectId,
        Long friendshipComboId,
        Long subFriendshipComboId,
        Integer specialNote,
        Long seriesInfoId,
        String iconImage,
        String monsterImage) {
}
