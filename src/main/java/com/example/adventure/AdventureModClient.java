package com.example.adventure;

import com.example.adventure.client.renderer.*;
import com.example.adventure.entity.ModEntities;
import com.example.adventure.events.ScreenShakeHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class AdventureModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderers (GeckoLib)
        EntityRendererRegistry.register(ModEntities.SHADOW_SHARD, ShadowShardRenderer::new);
        EntityRendererRegistry.register(ModEntities.AQUA_BOSS, AquaBossRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOON_BOSS, MoonBossRenderer::new);
        EntityRendererRegistry.register(ModEntities.INOSKINO_BOSS, InoskinoBossRenderer::new);
        EntityRendererRegistry.register(ModEntities.ZGREJ_BOSS, ZgrejBossRenderer::new);
        EntityRendererRegistry.register(ModEntities.EMILIA_NPC, EmiliaNpcRenderer::new);

        // Register client-side screen shake receiver
        ScreenShakeHandler.registerClientReceiver();
    }
}
