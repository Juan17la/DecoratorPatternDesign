package com.naruto.game;

import java.util.List;
import java.util.Map;

/**
 * Visual and passive description for each playable clan.
 * Consumed by the frontend clan selection screen.
 */
public class ClanAppearance {

    public final String clan;
    public final String skinTone;
    public final String eyeColor;
    public final String hairColor;
    public final String extraDetail;
    public final String passive;
    public final String jutsuTheme;

    private ClanAppearance(String clan, String skinTone, String eyeColor,
                           String hairColor, String extraDetail,
                           String passive, String jutsuTheme) {
        this.clan        = clan;
        this.skinTone    = skinTone;
        this.eyeColor    = eyeColor;
        this.hairColor   = hairColor;
        this.extraDetail = extraDetail;
        this.passive     = passive;
        this.jutsuTheme  = jutsuTheme;
    }

    /** All six clan appearances in selection order. */
    public static final List<ClanAppearance> ALL = List.of(
        new ClanAppearance(
            "UZUMAKI",
            "light", "purple", "long red", "",
            "+30% max chakra; slowly regenerates chakra between waves",
            "Sealing techniques, chakra chains, healing barriers"
        ),
        new ClanAppearance(
            "UCHIHA",
            "pale", "red (Sharingan)", "black straight", "small red eye glow",
            "+15% critical hit chance; crits deal 2x damage",
            "Fire-style, Sharingan-enhanced attacks, Susanoo fragments"
        ),
        new ClanAppearance(
            "HYUGA",
            "light", "light purple", "brown/dark straight", "",
            "Tower attacks ignore enemy chakra armor (armor-piercing)",
            "Gentle Fist chakra bursts, 8-Trigrams area attacks, Byakugan sight"
        ),
        new ClanAppearance(
            "NARA",
            "light", "dark", "black spiky pony-tied", "",
            "25% chance to slow enemies on hit (shadow binding)",
            "Shadow manipulation, crowd control, area denial"
        ),
        new ClanAppearance(
            "INUZUKA",
            "tan", "brown", "brown", "two red triangles on cheeks",
            "Tower attacks 50% faster than other clans at base level",
            "Beast attacks, rapid multi-hit strikes, tracking abilities"
        ),
        new ClanAppearance(
            "INOICHI",
            "light", "blue", "light blonde", "",
            "20% chance to confuse enemies on hit (briefly stop moving)",
            "Mind techniques, psychic disruption, intelligence-gathering buffs"
        )
    );

    /** Fixed appearance shared by all clans. */
    public static final Map<String, String> FIXED = Map.of(
        "torso",   "green flak jacket (Konoha standard)",
        "pants",   "dark navy blue",
        "headband","metal plate with Konoha leaf symbol",
        "sandals", "dark color"
    );
}
