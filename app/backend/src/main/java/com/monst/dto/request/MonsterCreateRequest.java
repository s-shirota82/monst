package com.monst.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonsterCreateRequest {

        @NotNull
        private Integer number;

        @NotNull
        private Integer rarity;

        @NotBlank
        private String name;

        @NotNull
        private Long evolutionStageId;

        @NotNull
        private Long attributeId;

        @NotNull
        @Min(0)
        private Integer hpMax;

        @NotNull
        @Min(0)
        private Integer hpPlusMax;

        @NotNull
        @Min(0)
        private Integer attackMax;

        @NotNull
        @Min(0)
        private Integer attackPlusMax;

        @NotNull
        private BigDecimal speedMax;

        @NotNull
        private BigDecimal speedPlusMax;

        private Long luckSkillId;

        @NotNull
        private Long hitTypeId;

        @NotNull
        private Long tribeId;

        @NotNull
        private Long battleTypeId;

        private JsonNode baseAbility;
        private JsonNode gaugeAbility;
        private JsonNode connectSkill;

        private Long shotSkillId;
        private Long assistSkillId;

        @NotNull
        private Long strikeShotNameId;

        @NotNull
        private Long strikeShotEffectId;

        @NotNull
        private Long friendshipComboId;

        private Long subFriendshipComboId;

        @NotNull
        private Integer specialNote;

        private Long seriesInfoId;
}
