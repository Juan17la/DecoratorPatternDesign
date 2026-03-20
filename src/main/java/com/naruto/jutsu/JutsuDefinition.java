package com.naruto.jutsu;

/**
 * Immutable data record describing a single jutsu entry from the catalog.
 * Consumed by CatalogJutsu to drive execute() behaviour.
 */
public record JutsuDefinition(
    String     name,
    JutsuRank  rank,
    String     clan,          // null = any clan
    String     elementType,   // Fire, Wind, Water, Earth, Lightning, General
    String     effectDesc,    // human-readable description shown in UI
    EffectType effectType,
    int        damage,
    int        duration,      // milliseconds
    int        targets,       // number of targets (0 = unlimited / all)
    int        chakraCost,
    int        chakraRestore
) {}
