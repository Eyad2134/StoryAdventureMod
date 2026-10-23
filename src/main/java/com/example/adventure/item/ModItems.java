package com.example.adventure.item;

import com.example.adventure.AdventureMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

/**
 * Custom items used across the adventure storyline.
 */
public class ModItems {

    /** Dropped by Aqua when defeated. Used to open the throne-room door for Moon. */
    public static final Item DIMENSIONAL_KEY = Registry.register(
            Registries.ITEM,
            Identifier.of(AdventureMod.MOD_ID, "dimensional_key"),
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.EPIC))
    );

    /** Trophy drop from the Shadow Shard. */
    public static final Item SHADOW_SHARD_FRAGMENT = Registry.register(
            Registries.ITEM,
            Identifier.of(AdventureMod.MOD_ID, "shadow_shard_fragment"),
            new Item(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON))
    );

    public static void register() {
        AdventureMod.LOGGER.info("Registering items for " + AdventureMod.MOD_ID);

        // Add items to creative tab so they're discoverable
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(DIMENSIONAL_KEY);
            entries.add(SHADOW_SHARD_FRAGMENT);
        });
    }
}
