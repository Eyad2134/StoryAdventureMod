package com.example.adventure;

import net.fabricmc.api.ModInitializer;
import com.example.adventure.events.QuestTriggerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventureMod implements ModInitializer {
    public static final String MOD_ID = "adventure_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Story Adventure Mod...");
        QuestTriggerHandler.register();
    }
}
