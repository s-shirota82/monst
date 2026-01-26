package com.monst.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record MonsterFullResponse(
        long id,
        int number,
        String name,

        Rarity rarity,
        EvolutionStage evolutionStage,

        NamedImage attribute,
        String hitType,
        String tribe,
        String battleType,

        Status status,

        NamedImage luckSkill,

        Abilities abilities,
        ConnectSkill connectSkill,

        Skills skills,
        StrikeShot strikeShot,

        FriendshipCombo friendshipCombo,
        String series,

        Images images) {

    public record Rarity(int value, int maxLevel) {
    }

    public record EvolutionStage(String name, boolean levelCapRelease, boolean superBattleRelease) {
    }

    /** 名前 + 画像（path/mimeType/base64） */
    public record NamedImage(String name, ImageData image) {
    }

    /** 画像データ（base64） */
    public record ImageData(String path, String mimeType, String base64) {
    }

    public record Status(Stat hp, Stat attack, Speed speed) {
        public record Stat(int max, int plusMax) {
        }

        public record Speed(BigDecimal max, BigDecimal plusMax) {
        }
    }

    public record Abilities(List<Ability> base, List<Ability> gauge) {
    }

    public record Ability(String name, String stage) {
    }

    public record ConnectSkill(String condition, List<Ability> abilities) {
    }

    public record Skills(String shot, String assist) {
    }

    public record StrikeShot(String name, String effect) {
    }

    public record FriendshipCombo(Friendship main, Friendship sub) {
        public record Friendship(
                String name,
                NamedImage attribute,
                String category,
                String description,
                Integer power,
                ImageData image) {
        }
    }

    public record Images(ImageData icon, ImageData monster) {
    }
}
