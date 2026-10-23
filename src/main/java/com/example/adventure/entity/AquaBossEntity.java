package com.example.adventure.entity;

import com.example.adventure.events.QuestTriggerHandler;
import com.example.adventure.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
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
 * Boss 1 — Aqua.
 * <p>
 * Guardian of the Purple Forest dimension. A bright cyan-themed warrior with
 * 2 phases. In Phase 1 he fights defensively. When his HP drops to 30% he
 * sends a dramatic chat message and enters Phase 2 with boosted speed/damage.
 * Drops the Dimensional Key on death.
 */
public class AquaBossEntity extends HostileEntity implements GeoEntity {

    // ── Phase management ──────────────────────────────────────────────
    public enum Phase { PHASE_1, PHASE_2 }
    private Phase currentPhase = Phase.PHASE_1;
    private boolean hasTransitioned = false;

    // ── Boss bar ──────────────────────────────────────────────────────
    private final ServerBossBar bossBar = new ServerBossBar(
            Text.literal("§bAqua"),
            BossBar.Color.BLUE,
            BossBar.Style.PROGRESS
    );

    // ── GeckoLib ──────────────────────────────────────────────────────
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_P1 = RawAnimation.begin().thenLoop("animation.aqua.idle_phase1");
    private static final RawAnimation WALK_P1 = RawAnimation.begin().thenLoop("animation.aqua.walk_phase1");
    private static final RawAnimation ATTACK_P1 = RawAnimation.begin().thenPlay("animation.aqua.attack_phase1");
    private static final RawAnimation TRANSITION = RawAnimation.begin().thenPlay("animation.aqua.phase_transition");
    private static final RawAnimation IDLE_P2 = RawAnimation.begin().thenLoop("animation.aqua.idle_phase2");
    private static final RawAnimation WALK_P2 = RawAnimation.begin().thenLoop("animation.aqua.walk_phase2");
    private static final RawAnimation ATTACK_P2 = RawAnimation.begin().thenPlay("animation.aqua.attack_phase2");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("animation.aqua.death");

    // Transition lockout — entity is invulnerable during the cinematic switch
    private int transitionTicks = 0;
    private static final int TRANSITION_DURATION = 60; // 3 seconds

    // ── Constructor ───────────────────────────────────────────────────

    public AquaBossEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 150;
    }

    // ── Attributes ────────────────────────────────────────────────────

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6);
    }

    // ── AI Goals ──────────────────────────────────────────────────────

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    // ── Tick ──────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            // Transition countdown
            if (transitionTicks > 0) {
                transitionTicks--;
                if (transitionTicks == 0) {
                    finalizePhaseTransition();
                }
            }

            // Phase transition trigger at 30% HP
            if (!hasTransitioned && currentPhase == Phase.PHASE_1
                    && this.getHealth() <= this.getMaxHealth() * 0.3f) {
                beginPhaseTransition();
            }
        }
    }

    // ── Phase Transition ──────────────────────────────────────────────

    private void beginPhaseTransition() {
        hasTransitioned = true;
        transitionTicks = TRANSITION_DURATION;

        // Dramatic chat message
        broadcastMessage("§b[Aqua] §f" +
                "مش هسيب الحياه دي. انا لازم اسندك يا صاحبي.. مووون");

        // Play transition animation
        this.triggerAnim("phase_controller", "transition");
    }

    private void finalizePhaseTransition() {
        this.currentPhase = Phase.PHASE_2;

        // Boost stats for Phase 2
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(16.0);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.38);

        bossBar.setColor(BossBar.Color.WHITE);
        bossBar.setName(Text.literal("§b§lAqua §7— §cPhase 2"));
    }

    // ── Damage ────────────────────────────────────────────────────────

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Invulnerable during transition cinematic
        if (transitionTicks > 0) return false;
        return super.damage(source, amount);
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        String key = (currentPhase == Phase.PHASE_1) ? "attack_p1" : "attack_p2";
        this.triggerAnim("attack_controller", key);
        return super.tryAttack(target);
    }

    // ── Death ─────────────────────────────────────────────────────────

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient()) {
            this.dropStack(new ItemStack(ModItems.DIMENSIONAL_KEY, 1));

            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                QuestTriggerHandler.onBossDefeated(player, "aqua_defeated");
            }
        }
    }

    // ── Boss bar tracking ─────────────────────────────────────────────

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        bossBar.removePlayer(player);
    }

    // ── GeckoLib Animation ────────────────────────────────────────────

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Main movement/idle — switches animation set based on current phase
        controllers.add(new AnimationController<>(this, "movement_controller", 5, state -> {
            if (this.isDead()) return state.setAndContinue(DEATH_ANIM);

            if (currentPhase == Phase.PHASE_2) {
                return state.setAndContinue(state.isMoving() ? WALK_P2 : IDLE_P2);
            }
            return state.setAndContinue(state.isMoving() ? WALK_P1 : IDLE_P1);
        }));

        // One-shot attack controller
        controllers.add(new AnimationController<>(this, "attack_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("attack_p1", ATTACK_P1)
                .triggerableAnim("attack_p2", ATTACK_P2));

        // One-shot phase-transition controller
        controllers.add(new AnimationController<>(this, "phase_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("transition", TRANSITION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ── Helpers ────────────────────────────────────────────────────────

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    private void broadcastMessage(String message) {
        this.getWorld().getPlayers().forEach(p ->
                p.sendMessage(Text.literal(message), false));
    }
}
