package com.example.adventure.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import net.minecraft.entity.mob.HostileEntity;

public class ZgrejBossEntity extends HostileEntity implements GeoEntity {
    public enum BossPhase { CALM, AGGRESSIVE, DESPERATE, ASCENDED, VOID, FINAL }
    private BossPhase currentPhase = BossPhase.CALM;

    public void transitionPhase() {
        BossPhase[] phases = BossPhase.values();
        int nextIndex = (currentPhase.ordinal() + 1) % phases.length;
        this.currentPhase = phases[nextIndex];
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            return switch (currentPhase) {
                case CALM -> state.setAnimation(new AnimationState().setAnimationName("idle_calm"));
                case AGGRESSIVE -> state.setAnimation(new AnimationState().setAnimationName("combat_aggressive"));
                case ASCENDED -> state.setAnimation(new AnimationState().setAnimationName("float_ascended"));
                case VOID -> state.setAnimation(new AnimationState().setAnimationName("void_form"));
                case FINAL -> state.setAnimation(new AnimationState().setAnimationName("ultimate_form"));
                default -> state.setAnimation(new AnimationState().setAnimationName("idle"));
            };
        }));
    }
}
