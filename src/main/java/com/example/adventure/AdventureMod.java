package com.example.adventure;

import com.example.adventure.entity.ModEntities;
import com.example.adventure.events.QuestTriggerHandler;
import com.example.adventure.events.ScreenShakeHandler;
import com.example.adventure.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventureMod implements ModInitializer {
    public static final String MOD_ID = "adventure_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Story Adventure Mod...");

        // Register all entity types and their attributes
        ModEntities.register();

        // Register custom items (Dimensional Key, Shadow Shard drop, etc.)
        ModItems.register();

        // Register the S2C screen shake payload type
        ScreenShakeHandler.registerPayloads();

        // Register FTB Quests integration (kill triggers, boss completion)
        QuestTriggerHandler.register();

        LOGGER.info("Story Adventure Mod initialized.");
    }
}
