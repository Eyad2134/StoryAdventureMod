package com.example.adventure.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.text.Text;

public class MoonBossEntity extends HostileEntity implements GeoEntity {
    private int phase = 1;
    private boolean isInvulnerable = true;
    private int windowTimer = 0;

    public MoonBossEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (isInvulnerable && phase < 4) return false;

        float finalDamage = super.damage(source, amount);
        if (this.getHealth() <= 0 && phase == 1) {
            triggerPhase2();
        }
        return finalDamage;
    }

    private void triggerPhase2() {
        this.phase = 2;
        this.setHealth(100f);
        this.sendChat("انا اسف يا اكوا معرفتش اخد حقك");
        this.sendChat("مش هسيب حقه");
    }

    @Override
    public void tick() {
        super.tick();
        windowTimer++;
        if (windowTimer >= 400) {
            isInvulnerable = false;
            if (windowTimer >= 500) {
                isInvulnerable = true;
                windowTimer = 0;
            }
        }
    }

    private void sendChat(String msg) {
        this.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.literal("§4[Moon] §f" + msg), false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (phase == 1) return state.setAnimation(new AnimationState().setLooping(true).setAnimationName("idle_phase1"));
            if (phase == 2) return state.setAnimation(new AnimationState().setLooping(true).setAnimationName("attack_phase2"));
            return state.setAnimation(new AnimationState().setLooping(true).setAnimationName("idle"));
        }));
    }
}
