package com.example.adventure.entity;

import com.example.adventure.AdventureMod;
import com.example.adventure.events.QuestTriggerHandler;
import com.example.adventure.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * شظية الظلمة — Shard of Darkness.
 * <p>
 * The first hostile entity players encounter. A roaming shadow creature that
 * attacks on sight. Killing it triggers the FTB Quest that sends the player
 * toward the broken portal in the Overworld.
 */
public class ShadowShardEntity extends HostileEntity implements GeoEntity {

    // ── GeckoLib ──────────────────────────────────────────────────────
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.shadow_shard.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.shadow_shard.walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.shadow_shard.attack");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("animation.shadow_shard.death");

    // ── Constructor ───────────────────────────────────────────────────

    public ShadowShardEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    // ── Attributes ────────────────────────────────────────────────────

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0);
    }

    // ── AI Goals ──────────────────────────────────────────────────────

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 12.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    // ── Combat ────────────────────────────────────────────────────────

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        this.triggerAnim("attack_controller", "attack");
        return super.tryAttack(target);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient()) {
            // Drop a shadow shard fragment
            this.dropStack(new ItemStack(ModItems.SHADOW_SHARD_FRAGMENT, 1));

            // Trigger the FTB Quest for any nearby player who killed us
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                QuestTriggerHandler.onShadowShardKilled(player);
            }
        }
    }

    // ── GeckoLib Animation ────────────────────────────────────────────

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Main movement/idle controller
        controllers.add(new AnimationController<>(this, "movement_controller", 5, state -> {
            if (this.isDead()) {
                return state.setAndContinue(DEATH);
            }
            if (state.isMoving()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }));

        // One-shot attack controller
        controllers.add(new AnimationController<>(this, "attack_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
