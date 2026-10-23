package com.example.adventure.events;

import dev.ftb.quests.api.QuestAPI;
import net.fabricmc.fabric.api.entity.event.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import com.example.adventure.entity.ShadowShardEntity;
import net.minecraft.text.Text;

public class QuestTriggerHandler {
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ShadowShardEntity) {
                if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                    triggerShadowShardQuest(player);
                }
            }
        });
    }

    private static void triggerShadowShardQuest(ServerPlayerEntity player) {
        // Trigger quest ID "shadow_shard_killed" defined in FTB Quests JSON
        QuestAPI.getQuest(player.getServer(), "shadow_shard_killed").complete(player);
        player.sendMessage(Text.literal("§cThe darkness recedes... A path opens to the portal!"), false);
    }
}
