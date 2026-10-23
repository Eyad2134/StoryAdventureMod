package com.example.adventure.entity;

import com.example.adventure.AdventureMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Central registry for all custom entity types in the mod.
 */
public class ModEntities {

    // ── Hostile mobs ──────────────────────────────────────────────────

    public static final EntityType<ShadowShardEntity> SHADOW_SHARD = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AdventureMod.MOD_ID, "shadow_shard"),
            EntityType.Builder.create(ShadowShardEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.8f, 1.8f)
                    .build()
    );

    public static final EntityType<AquaBossEntity> AQUA_BOSS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AdventureMod.MOD_ID, "aqua_boss"),
            EntityType.Builder.create(AquaBossEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.0f, 2.4f)
                    .build()
    );

    public static final EntityType<MoonBossEntity> MOON_BOSS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AdventureMod.MOD_ID, "moon_boss"),
            EntityType.Builder.create(MoonBossEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.2f, 2.8f)
                    .build()
    );

    public static final EntityType<InoskinoBossEntity> INOSKINO_BOSS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AdventureMod.MOD_ID, "inoskino_boss"),
            EntityType.Builder.create(InoskinoBossEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.0f, 2.2f)
                    .build()
    );

    public static final EntityType<ZgrejBossEntity> ZGREJ_BOSS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AdventureMod.MOD_ID, "zgrej_boss"),
            EntityType.Builder.create(ZgrejBossEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.4f, 3.2f)
                    .build()
    );

    // ── NPCs ──────────────────────────────────────────────────────────

    public static final EntityType<EmiliaNpcEntity> EMILIA_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AdventureMod.MOD_ID, "emilia_npc"),
            EntityType.Builder.create(EmiliaNpcEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.8f)
                    .build()
    );

    /**
     * Registers all entity types and binds their default attribute containers.
     * Called once from {@link AdventureMod#onInitialize()}.
     */
    public static void register() {
        AdventureMod.LOGGER.info("Registering entities for " + AdventureMod.MOD_ID);

        FabricDefaultAttributeRegistry.register(SHADOW_SHARD, ShadowShardEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(AQUA_BOSS, AquaBossEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MOON_BOSS, MoonBossEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(INOSKINO_BOSS, InoskinoBossEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ZGREJ_BOSS, ZgrejBossEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(EMILIA_NPC, EmiliaNpcEntity.createAttributes());
    }
}
