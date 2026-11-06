package net.thenextlvl.character.plugin.character.movement;

import net.thenextlvl.character.event.CharacterAttackEvent;
import net.thenextlvl.character.goal.AttackGoal;
import net.thenextlvl.character.goal.FollowGoal;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.goal.LookAtGoal;
import net.thenextlvl.character.goal.MovementGoal;
import net.thenextlvl.character.goal.PatrolGoal;
import net.thenextlvl.character.goal.Target;
import net.thenextlvl.character.goal.WanderGoal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.PaperCharacter;
import net.thenextlvl.character.property.EntityProperties;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Vex;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Wither;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

/**
 * Pursues the goals of a character with the entity of one viewer, modelled after vanilla's
 * {@code PathNavigation}, {@code MoveControl}, {@code JumpControl} and {@code LookControl}.
 * <p>
 * Has no task of its own, the character ticks the controllers of all its entities in sync,
 * see {@link PaperCharacter#tickControllers()}, always on the region of the entity.
 * Living entities move by velocity, so vanilla handles collisions, gravity and stepping up slabs and stairs,
 * see {@link PaperCharacter#setPhysics(Entity, boolean)}.
 * Other entities have no physics and only look at their targets.
 * <p>
 * Only one goal runs at a time and goals take turns: once the running goal is done,
 * the next goal that applies after it in their order starts, wrapping around at the end.
 * A running goal is never interrupted.
 * If the character moves alike for all viewers, its entities share their {@link GoalState},
 * so they all switch goals at the same time.
 * <p>
 * Without an applicable goal, an entity that a goal moved away walks back to the character's location.
 * Entities that were never moved by a goal are left alone, so look goals only rotate them,
 * even if they were moved otherwise, like by knockback.
 * <p>
 * Stops itself once the character has no goals and the entity is no longer away.
 * Doors are only opened if the character moves alike for all viewers, see {@link PaperCharacter#movesAlike()}.
 */
@NullMarked
public final class EntityController {
    // vanilla follow goals recalculate their path every 10 ticks
    private static final int REPATH_INTERVAL = 10;
    private static final int STUCK_TICKS = 40;
    private static final double ARRIVAL_HEIGHT = 1;
    // close enough to the character's location to put the entity right onto it, even if it floats
    private static final double HOME_DISTANCE = 0.5;
    private static final double REPATH_DISTANCE = 1;
    private static final double DIRECT_APPROACH_DISTANCE = 2;
    private static final float ROTATION_EPSILON = 0.5f;
    private static final double MOVED_EPSILON = 0.01;
    // closer than this to the wanted position horizontally, the entity no longer steers towards it
    private static final double STEERING_DISTANCE = 0.3;
    // a follow goal only starts again once the target is this much farther away than the distance to keep
    private static final double FOLLOW_START_DISTANCE = 1;

    // like vanilla's MoveControl, JumpControl and LookControl
    private static final float MAX_TURN = 90;
    private static final float MAX_HEAD_YAW = 75;
    private static final float DEFAULT_HEAD_ROTATION_SPEED = 10;
    private static final float DEFAULT_MAX_HEAD_PITCH = 40;
    private static final float BODY_ROTATION_SPEED = 5;
    private static final double JUMP_VELOCITY = 0.42;
    // like vanilla, climbing ladders: vanilla sets 0.2 after the move, gravity and drag leave (0.2 - 0.08) * 0.98 for the next one
    private static final double CLIMBING_VELOCITY = (0.2 - 0.08) * 0.98;
    private static final double LADDER_THICKNESS = 3 / 16d;
    // how far below the wanted position the feet may be when stepping off a ladder or vine
    private static final double CLIMB_TOLERANCE = 0.05;
    // the walking speed needed to jump over a gap, a bit below a player's, who easily makes it
    private static final double JUMP_SPEED = 0.2;
    // how much of its walking an entity can still steer in mid air
    private static final double AIR_CONTROL = 0.1;
    // like vanilla's jump delay
    private static final int JUMP_DELAY = 10;
    private static final double WATER_JUMP_VELOCITY = 0.3;
    private static final double BUOYANCY = 0.04;
    // once rising, a swimming entity rises this far above the jump threshold before it sinks back, so it bobs visibly
    private static final double BOBBING = 0.25;
    // the height of the water surface within the topmost water block
    private static final double WATER_SURFACE = 0.9;
    private static final double DEFAULT_STEP_HEIGHT = 0.6;

    // the distance per tick at a movement speed attribute of 1, derived from vanilla's ground and water friction,
    // mobs walk at speed², as vanilla uses the speed both as input and as acceleration
    private static final double MOB_GROUND_SPEED = 2.202;
    private static final double WATER_SPEED = 0.1;
    // other entities walk and swim like players do at their default movement speed
    private static final double WALKING_SPEED = 0.2158;
    private static final double SWIMMING_SPEED = 0.098;
    private static final double FLYING_SPEED = 0.15;
    // mannequins sprint like players, once the target is far away, until it is close again
    private static final double SPRINTING_MULTIPLIER = 1.3;
    private static final double SPRINT_START_DISTANCE = 8;
    private static final double SPRINT_STOP_DISTANCE = 4;

    // a new wander target every period, the same for every entity of the character
    private static final long WANDER_PERIOD_MILLIS = 8_000;
    private static final double WANDER_REST_CHANCE = 0.3;
    private static final int WANDER_HEIGHT = 7;
    // like vanilla's LookAtPlayerGoal
    private static final int MIN_LOOK_TICKS = 40;
    private static final int MAX_LOOK_TICKS = 80;

    private final CharacterPlugin plugin;
    private final PaperCharacter character;
    private final UUID viewer;
    private final Entity entity;

    private final AtomicBoolean ticking = new AtomicBoolean();
    private volatile boolean stopped;
    private boolean opensDoors;
    private boolean physics;
    // whether a goal moved the entity away from the character's location
    private boolean displaced;
    private volatile boolean teleporting;

    private List<Location> path = List.of();
    private boolean pathReaches;
    private @Nullable Location pathGoal;
    private int pathIndex;
    private int repathTicks;
    private int stuckTicks;
    private float movementYaw = Float.NaN;
    private boolean jumping;
    private boolean climbing;
    private boolean surfacing;
    private boolean sprinting;
    private int jumpDelay;
    private @Nullable Location previous;
    private PathFinder.@Nullable Swimming swimming;

    // like vanilla villagers, a player interacting with the entity has its attention for a few seconds
    private static final int INTERACTION_TICKS = 60;
    private volatile @Nullable UUID interacting;
    private volatile int interactionTicks;

    private final GoalState own = new GoalState();
    // the goal this entity pursued last, to start over when the leading entity switches goals
    private @Nullable Goal pursued;

    private @Nullable Location wanderTarget;
    private long wanderPeriod = -1;

    public EntityController(final CharacterPlugin plugin, final PaperCharacter character, final UUID viewer, final Entity entity) {
        this.plugin = plugin;
        this.character = character;
        this.viewer = viewer;
        this.entity = entity;
    }

    public Entity entity() {
        return entity;
    }

    // whether the entity currently moves by physics, it keeps its AI until it arrived
    public boolean hasPhysics() {
        return physics;
    }

    public boolean isStopped() {
        return stopped;
    }

    /**
     * Pursues the goals for one tick, must be called on the region of the entity.
     *
     * @return {@code false} once the controller is done and can be removed
     */
    public boolean tick() {
        if (stopped) return false;
        // the entity may change regions between ticks, never tick it twice at once
        if (!ticking.compareAndSet(false, true)) return true;
        try {
            tickNow();
        } finally {
            ticking.set(false);
        }
        return !stopped;
    }

    private void tickNow() {
        if (!entity.isValid()) {
            stop();
            return;
        }
        final var home = character.getLocation();
        final var current = entity.getLocation();
        if (!home.getWorld().equals(current.getWorld()) || teleporting) return;
        if (Float.isNaN(movementYaw)) movementYaw = bodyYaw(current);

        final var goals = character.getGoals();
        final var player = plugin.getServer().getPlayer(viewer);
        opensDoors = character.movesAlike();

        final var interaction = interaction(current);
        final var action = interaction != null ? interaction : action(goals, home, current, player);
        if (attackCooldown > 0) attackCooldown--;
        setAggressive(action.attack() != null);
        if (action == Action.IDLE) {
            rotate(null, current, false, home);
            if (goals.isEmpty()) stop();
            return;
        }

        if (isKnockedBack()) {
            character.doors().close();
            return;
        }

        final var movement = action.movement();
        if (movement != null && movement.target() == home && horizontalDistance(current, home) <= HOME_DISTANCE) {
            settle(home);
            if (goals.isEmpty()) stop();
            return;
        }

        // looking or holding does not move the entity, but one that was moved away keeps its physics and swims
        if (movement == null) {
            jumping = false;
            climbing = false;
            if (physics) {
                final var velocity = isAirborne(current) ? entity.getVelocity() : new Vector();
                entity.setVelocity(velocity.setY(flies() ? 0 : vertical(current, null)));
            }
            character.doors().close();
            rotate(action.look(), current, false, home);
            if (action.attack() != null) attack(action.attack(), current);
            return;
        }

        setPhysics(true);
        final var velocity = navigate(movement, current);
        final var moving = velocity.getX() != 0 || velocity.getZ() != 0;
        // flying entities move in all directions on their own
        if (!flies()) velocity.setY(vertical(current, movement));
        // like vanilla, an entity keeps its momentum in mid air and can only steer a little,
        // which is still enough to get onto a block it jumps up to once it is high enough
        if (isAirborne(current)) {
            final var momentum = entity.getVelocity().setY(0);
            final var steered = momentum.clone().add(velocity.clone().setY(0).multiply(AIR_CONTROL));
            final var limit = Math.max(momentum.length(), Math.hypot(velocity.getX(), velocity.getZ()));
            if (steered.length() > limit && steered.length() > 0) steered.multiply(limit / steered.length());
            velocity.setX(steered.getX()).setZ(steered.getZ());
        }
        entity.setVelocity(velocity);
        trackProgress(current, moving);
        if (moving && sprinting) spawnSprintParticle(current, velocity, player);

        if (moving && opensDoors) {
            final var ahead = current.clone().add(velocity.clone().setY(0).normalize().multiply(halfWidth() + 0.6));
            character.doors().open(entity, ahead, halfWidth());
        }
        character.doors().close();
        // the head looks at the target, if any, while the body faces the way it walks
        rotate(action.look(), current, moving, home);
        if (action.attack() != null) attack(action.attack(), current);
    }

    // attacking

    // like vanilla's MeleeAttackGoal, once a second
    private static final int ATTACK_INTERVAL = 20;
    // vanilla's melee attack range, how far the attack box reaches beyond the hitbox
    private static final double ATTACK_REACH = 0.828;
    private int attackCooldown;
    private boolean aggressive;

    // whether the hitboxes are within vanilla's melee attack range
    private boolean isInReach(final Location current, final Player target) {
        final var reach = entity.getWidth() / 2 + ATTACK_REACH + target.getWidth() / 2;
        final var location = target.getLocation();
        if (horizontalDistance(current, location) > reach) return false;
        return location.getY() < current.getY() + entity.getHeight() && current.getY() < location.getY() + target.getHeight();
    }

    /*
     * Every viewer sees their own entity swing at the target,
     * but only the target's own entity hurts them, the target would be hurt by every viewer's entity otherwise.
     */
    private void attack(final Player target, final Location current) {
        if (attackCooldown > 0 || !isInReach(current, target)) return;
        attackCooldown = ATTACK_INTERVAL;
        if (!(entity instanceof final LivingEntity living)) return;
        living.swingMainHand();
        if (!target.getUniqueId().equals(viewer)) return;
        if (!new CharacterAttackEvent(character, target).callEvent()) return;
        // vanilla's attack with weapon damage and knockback, which needs both on the same region
        if (living instanceof Mob && plugin.getServer().isOwnedByCurrentRegion(target)) {
            living.attack(target);
            return;
        }
        final var weapon = living.getEquipment() != null ? living.getEquipment().getItemInMainHand() : null;
        final var sharpness = weapon != null ? weapon.getEnchantmentLevel(Enchantment.SHARPNESS) : 0;
        final var fireAspect = weapon != null ? weapon.getEnchantmentLevel(Enchantment.FIRE_ASPECT) : 0;
        final var knockback = weapon != null ? weapon.getEnchantmentLevel(Enchantment.KNOCKBACK) : 0;
        final var damage = attackDamage(living) + (sharpness > 0 ? 0.5 * sharpness + 0.5 : 0);
        final var source = DamageSource.builder(DamageType.MOB_ATTACK).withCausingEntity(living).withDirectEntity(living).build();
        final var yaw = Math.toRadians(living.getYaw());
        plugin.supply(target, () -> {
            target.damage(damage, source);
            if (fireAspect > 0) target.setFireTicks(Math.max(target.getFireTicks(), fireAspect * 80));
            if (knockback > 0) target.knockback(knockback * 0.5, Math.sin(yaw), -Math.cos(yaw));
        });
    }

    private double attackDamage(final LivingEntity living) {
        var attribute = living.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attribute == null) {
            living.registerAttribute(Attribute.ATTACK_DAMAGE);
            attribute = living.getAttribute(Attribute.ATTACK_DAMAGE);
        }
        return attribute != null ? attribute.getValue() : 1;
    }

    // like vanilla, mobs show that they attack, like zombies raising their arms
    private void setAggressive(final boolean aggressive) {
        if (this.aggressive == aggressive || !(entity instanceof final Mob mob)) return;
        this.aggressive = aggressive;
        mob.setAggressive(aggressive || character.getProperty(EntityProperties.MOB_AGGRESSIVE).orElse(false));
    }

    // falling or jumping, not standing, climbing, swimming or flying
    private boolean isAirborne(final Location current) {
        if (flies() || entity.isOnGround()) return false;
        return !isOnClimbable(current) && !PathFinder.isWater(current.getBlock());
    }

    // in a ladder, vine or scaffolding, or right above the top of one, from where it steps onto the ledge next to it
    private static boolean isOnClimbable(final Location current) {
        final var block = current.getBlock();
        return PathFinder.isClimbable(block) || PathFinder.isClimbable(block.getRelative(0, -1, 0));
    }

    /*
     * Only damage by entities and explosions knocks back, damaging blocks like magma, fire or cactus do not,
     * and they hurt again right after, which would never give the entity control back.
     */
    private boolean isKnockedBack() {
        if (!(entity instanceof final LivingEntity living)) return false;
        if (living.getNoDamageTicks() <= living.getMaximumNoDamageTicks() / 2) return false;
        final var cause = living.getLastDamageCause();
        if (cause == null) return false;
        final var source = cause.getDamageSource();
        return source.getCausingEntity() != null || source.getDirectEntity() != null
               || cause.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
               || cause.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;
    }

    private void stop() {
        stopped = true;
    }

    // back at the character's location, the entity is put right onto it and stops moving
    private void settle(final Location home) {
        setPhysics(false);
        displaced = false;
        clearPath();
        final var current = entity.getLocation();
        if (current.distanceSquared(home) > 1.0E-6) {
            // keep the rotation, it turns towards the character's rotation like any other
            final var location = home.clone();
            location.setRotation(current.getYaw(), current.getPitch());
            teleporting = true;
            entity.teleportAsync(location, PLUGIN).whenComplete((success, throwable) -> teleporting = false);
            return;
        }
        rotate(null, current, false, home);
    }

    private void setPhysics(final boolean physics) {
        if (this.physics == physics) return;
        this.physics = physics;
        if (physics) displaced = true;
        character.setPhysics(entity, physics);
        // flying entities have no gravity while they fly
        if (physics && flies()) entity.setGravity(false);
    }

    // counts the ticks the entity tries to move without getting anywhere
    private void trackProgress(final Location current, final boolean moving) {
        final var previous = this.previous;
        this.previous = current;
        if (!moving) return;
        if (previous != null && previous.distanceSquared(current) < MOVED_EPSILON * MOVED_EPSILON) stuckTicks++;
        else stuckTicks = 0;
    }

    // goals

    // the accuracy is how close a path has to get to the target, like vanilla's
    private record Movement(Location target, double distance, double speed, int accuracy) {
    }

    // what the running goal does this tick, either moving or looking
    // what the running goal does this tick: moving, looking and attacking
    /*
     * What the running goal does this tick: moving, looking and attacking.
     * A held action is a satisfied goal, the entity stands still but may still look at something.
     */
    private record Action(@Nullable Movement movement, @Nullable Location look, @Nullable Player attack, boolean hold) {
        // no goal applies, the entity keeps its own state
        private static final Action IDLE = new Action(null, null, null, false);
        // a movement goal applies, but has nothing to do right now, so the entity stands still where it is
        private static final Action HOLD = new Action(null, null, null, true);

        private Action(@Nullable final Movement movement, @Nullable final Location look) {
            this(movement, look, null, false);
        }

        private Action(@Nullable final Movement movement, @Nullable final Location look, @Nullable final Player attack) {
            this(movement, look, attack, false);
        }

        // stands still, looking at the given location
        private static Action hold(@Nullable final Location look) {
            return look != null ? new Action(null, look, null, true) : HOLD;
        }
    }

    /**
     * Makes the entity stop and look at the given player for a few seconds.
     *
     * @param player the player interacting with the entity
     */
    public void interact(final UUID player) {
        // combat is never interrupted
        if (state().running instanceof AttackGoal) return;
        interacting = player;
        interactionTicks = INTERACTION_TICKS;
    }

    // looks at the interacting player, interrupting the goals
    private @Nullable Action interaction(final Location current) {
        final var interacting = this.interacting;
        final var interactionTicks = this.interactionTicks;
        if (interacting == null || (this.interactionTicks = interactionTicks - 1) <= 0) {
            this.interacting = null;
            return null;
        }
        final var player = plugin.getServer().getPlayer(interacting);
        if (player == null || !player.getWorld().equals(current.getWorld())) {
            this.interacting = null;
            return null;
        }
        return new Action(null, player.getEyeLocation());
    }

    // the state of the character if all entities move alike, so they switch goals at the same time
    private GoalState state() {
        return character.movesAlike() ? character.goalState() : own;
    }

    private Action action(final List<Goal> goals, final Location home, final Location current, @Nullable final Player player) {
        final var state = state();
        if (state == own || state.lead(character.ticks())) return lead(state, goals, home, current, player);
        return follow(state, goals, home, current, player);
    }

    /*
     * Continues the running goal, or once it is done, starts the next goal after it that has something to do,
     * so goals take turns instead of the first one always winning.
     * If no goal has something to do, but a movement goal is satisfied, like a follow goal that is close enough
     * or a wander goal that rests, the entity stands still where it is.
     */
    private Action lead(final GoalState state, final List<Goal> goals, final Location home, final Location current, @Nullable final Player player) {
        final var running = state.running;
        if (running != null && goals.contains(running)) {
            final var action = action(state, running, home, current, player, false, true);
            if (action != null && !action.hold()) return pursue(running, action);
        }
        if (running != null) state.lastGoal = goals.indexOf(running);
        state.running = null;
        Action hold = null;
        for (var i = 1; i <= goals.size(); i++) {
            final var index = Math.floorMod(state.lastGoal + i, goals.size());
            final var goal = goals.get(index);
            final var action = action(state, goal, home, current, player, true, true);
            if (action == null) continue;
            if (action.hold()) {
                // the first satisfied goal that looks at something, like a follow goal at its target
                if (hold == null || (hold.look() == null && action.look() != null)) hold = action;
                continue;
            }
            state.running = goal;
            state.mode = GoalState.Mode.RUNNING;
            return pursue(goal, action);
        }
        state.mode = hold != null ? GoalState.Mode.HOLD : GoalState.Mode.NONE;
        pursue(null, null);
        return hold != null ? hold : fallback(home);
    }

    // does what the leading entity decided, an entity that is done before the leader waits for it
    private Action follow(final GoalState state, final List<Goal> goals, final Location home, final Location current, @Nullable final Player player) {
        return switch (state.mode) {
            case RUNNING -> {
                final var running = state.running;
                if (running == null || !goals.contains(running)) yield Action.HOLD;
                final var action = action(state, running, home, current, player, false, false);
                yield pursue(running, action != null ? action : Action.HOLD);
            }
            case HOLD -> {
                pursue(null, null);
                // looks at what its own satisfied goal looks at, like the target it follows
                yield goals.stream()
                        .map(goal -> action(state, goal, home, current, player, false, false))
                        .filter(action -> action != null && action.hold() && action.look() != null)
                        .findFirst().orElse(Action.HOLD);
            }
            case NONE -> {
                pursue(null, null);
                yield fallback(home);
            }
        };
    }

    // starts over with a new path when the pursued goal changes
    private @Nullable Action pursue(@Nullable final Goal goal, @Nullable final Action action) {
        if (!Objects.equals(goal, pursued)) clearPath();
        pursued = goal;
        return action;
    }

    // without an applicable goal, walk back to the character's location if a goal moved the entity away
    private Action fallback(final Location home) {
        return displaced ? new Action(new Movement(home, 0, MovementGoal.DEFAULT_SPEED, 0), null) : Action.IDLE;
    }

    /*
     * What the goal does this tick, HOLD if it applies but is satisfied, or null if it does not apply.
     * Only the leading entity decides when the goal is done, following entities do not change the state.
     */
    private @Nullable Action action(final GoalState state, final Goal goal, final Location home, final Location current,
                                    @Nullable final Player player, final boolean start, final boolean lead) {
        // only living entities have physics to move with
        if (goal instanceof MovementGoal && !(entity instanceof LivingEntity)) return null;
        return switch (goal) {
            case final AttackGoal attack -> {
                // the nearest player to the character's location, like when following
                final var target = resolvePlayer(attack.target(), player, home);
                if (target == null || !target.getWorld().equals(home.getWorld()) || target.isDead()) yield null;
                if (target.getGameMode() == GameMode.CREATIVE || target.getGameMode() == GameMode.SPECTATOR) yield null;
                if (target.getLocation().distanceSquared(home) > attack.range() * attack.range()) yield null;
                if (isInReach(current, target)) yield new Action(null, target.getEyeLocation(), target);
                // keeps its eyes on the target while chasing it, once it is close enough to see it
                yield new Action(new Movement(target.getLocation(), 0, attack.speed(), 1), inLookRange(current, target.getEyeLocation()), target);
            }
            case final FollowGoal follow -> {
                // the nearest player to the character's location, the entity itself is always closest to the one it follows
                final var target = resolve(follow.target(), player, home, false);
                if (target == null || !target.getWorld().equals(home.getWorld())) yield null;
                if (target.distanceSquared(home) > follow.range() * follow.range()) yield null;
                // like vanilla, it only starts following again once the target is farther away than where it stops
                final var distance = start ? follow.distance() + FOLLOW_START_DISTANCE : follow.distance();
                // it looks at the target while following it and while waiting close to it, once it is close enough to see it
                final var eyes = inLookRange(current, resolve(follow.target(), player, home, true));
                if (arrived(current, target, distance)) yield Action.hold(eyes);
                // the target can not be reached, it is tried again with a new path
                if (lead && !start && stuckTicks >= STUCK_TICKS) yield Action.hold(eyes);
                yield new Action(new Movement(target, follow.distance(), follow.speed(), 1), eyes);
            }
            case final WanderGoal wander -> {
                final var target = wander(wander, home, current);
                // resting or at the target, it stays around there instead of walking home
                yield target != null ? new Action(new Movement(target, 0, wander.speed(), 0), null) : Action.HOLD;
            }
            case final PatrolGoal patrol -> {
                final var point = patrol(state, patrol, current, start, lead);
                yield point != null ? new Action(new Movement(point, 0, patrol.speed(), 0), null) : null;
            }
            case final LookAtGoal lookAt -> {
                final var target = resolve(lookAt.target(), player, current, true);
                if (target == null || !target.getWorld().equals(current.getWorld())) yield null;
                if (target.distanceSquared(eyes(current)) > lookAt.range() * lookAt.range()) yield null;
                if (!lead) yield new Action(null, target);
                if (start) state.lookTicks = ThreadLocalRandom.current().nextInt(MIN_LOOK_TICKS, MAX_LOOK_TICKS + 1);
                else if (--state.lookTicks <= 0) yield null;
                yield new Action(null, target);
            }
        };
    }

    /*
     * Wander targets only depend on the character, the goal and the current period,
     * so every entity of the character walks to the same targets at the same time, regardless of the viewer.
     */
    private @Nullable Location wander(final WanderGoal wander, final Location home, final Location current) {
        final var period = System.currentTimeMillis() / WANDER_PERIOD_MILLIS;
        if (period != wanderPeriod) {
            wanderPeriod = period;
            wanderTarget = wanderTarget(wander, home, period);
            stuckTicks = 0;
        }
        final var target = wanderTarget;
        if (target == null || stuckTicks >= STUCK_TICKS || arrived(current, target, 0)) return null;
        return target;
    }

    private @Nullable Location wanderTarget(final WanderGoal wander, final Location home, final long period) {
        final var random = new SplittableRandom(Objects.hash(character.getName(), wander, period));
        if (random.nextDouble() < WANDER_REST_CHANCE) return null;
        final var angle = random.nextDouble(Math.PI * 2);
        final var distance = random.nextDouble(wander.radius());
        final var x = (int) Math.floor(home.getX() + Math.cos(angle) * distance);
        final var z = (int) Math.floor(home.getZ() + Math.sin(angle) * distance);
        // like vanilla's random stroll, up to seven blocks up or down
        return pathFinder().ground(x, home.getBlockY(), z, WANDER_HEIGHT);
    }

    // the point to walk to, the goal is done once a point is reached and starts with the next one
    private @Nullable Location patrol(final GoalState state, final PatrolGoal patrol, final Location current, final boolean start, final boolean lead) {
        final var points = patrol.points().stream()
                .filter(point -> point.getWorld().equals(current.getWorld()))
                .toList();
        if (points.isEmpty()) return null;
        final var point = points.get(state.patrolIndex % points.size());
        if (stuckTicks < STUCK_TICKS && !arrived(current, point, 0)) return point;
        // following entities wait at the point until the leader moves on
        if (!lead) return null;
        state.patrolIndex = (state.patrolIndex + 1) % points.size();
        stuckTicks = 0;
        return start ? points.get(state.patrolIndex) : null;
    }

    // returns the horizontal velocity towards the target, zero if the entity should not move
    private Vector navigate(final Movement movement, final Location current) {
        jumping = false;
        climbing = false;
        final var target = movement.target();
        if (arrived(current, target, movement.distance())) {
            clearPath();
            return new Vector();
        }

        if (flies() && ignoresBlocks()) return fly(current, target, true, movement.speed());

        if (pathGoal == null || pathGoal.distanceSquared(target) > REPATH_DISTANCE * REPATH_DISTANCE || repathTicks-- <= 0) {
            final var found = pathFinder().find(current, target, movement.accuracy());
            path = found.nodes();
            pathReaches = found.reaches();
            pathGoal = target.clone();
            pathIndex = 0;
            repathTicks = REPATH_INTERVAL;
        }
        followPath(current);

        final Location wanted;
        if (pathIndex < path.size()) wanted = path.get(pathIndex);
            // the path ended at the goal, walk the last bit to the exact target,
            // a path that ends before the goal stops there, instead of pushing into whatever is in the way
        else if (pathReaches && horizontalDistance(current, target) <= DIRECT_APPROACH_DISTANCE) wanted = target;
        else if (climbsWalls()) wanted = target;
        else {
            stuckTicks++;
            return new Vector();
        }
        if (flies()) return fly(current, wanted, wanted == target, movement.speed());
        updateSprinting(current, target);
        return move(current, wanted, wanted == target, movement.speed());
    }

    // like vanilla's PathNavigation#followThePath, advances to the next node once close enough
    private void followPath(final Location current) {
        if (pathIndex >= path.size()) return;
        final var node = path.get(pathIndex);
        final var waypoint = waypointDistance();
        final var close = Math.abs(current.getX() - node.getX()) < waypoint
                && Math.abs(current.getZ() - node.getZ()) < waypoint
                && Math.abs(current.getY() - node.getY()) < 1;
        if (close || shouldTargetNextNode(current)) pathIndex++;
    }

    // like vanilla's PathNavigation#shouldTargetNextNodeInDirection, skips a node the entity would walk back to
    private boolean shouldTargetNextNode(final Location current) {
        if (pathIndex + 1 >= path.size()) return false;
        final var position = current.toVector();
        final var next = path.get(pathIndex).toVector();
        if (position.distanceSquared(next) >= 4) return false;
        final var toNext = next.subtract(position);
        final var toNextNext = path.get(pathIndex + 1).toVector().subtract(position);
        final var nearer = toNextNext.lengthSquared() < toNext.lengthSquared();
        final var veryClose = toNext.lengthSquared() < 0.5;
        if (!nearer && !veryClose) return false;
        return toNextNext.normalize().dot(toNext.normalize()) < 0;
    }

    /*
     * Like players, an entity on a ladder faces it and stays close to it.
     * Climbing up, it presses against the ladder, which is also what makes vanilla climb,
     * climbing down, it moves up to the ladder without touching it, as pressing against it would make it climb up again.
     * Vines and scaffolding have no wall, so it stays in the middle of them.
     */
    private Vector hugLadder(final Location current, final double modifier, final boolean up) {
        final var block = current.getBlock();
        final var speed = speed(modifier, false);
        if (block.getBlockData() instanceof final Ladder ladder) {
            final var wall = ladder.getFacing().getOppositeFace().getDirection();
            movementYaw = (float) Math.toDegrees(Math.atan2(-wall.getX(), wall.getZ()));
            if (up) return wall.multiply(speed * 0.5);
            // right in front of the ladder, which is 3/16 of a block thick
            final var offset = Math.max(0, 0.5 - LADDER_THICKNESS - halfWidth() - 0.01);
            final var target = block.getLocation().add(0.5, 0, 0.5).add(wall.clone().multiply(offset));
            return towards(current, target, speed);
        }
        return towards(current, block.getLocation().add(0.5, 0, 0.5), speed);
    }

    // horizontally towards the target, without overshooting it
    private static Vector towards(final Location current, final Location target, final double speed) {
        final var dx = target.getX() - current.getX();
        final var dz = target.getZ() - current.getZ();
        final var distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < 0.01) return new Vector();
        final var step = Math.min(speed, distance);
        return new Vector(dx / distance * step, 0, dz / distance * step);
    }

    /*
     * Whether the entity is fast enough to jump over a block, like players.
     * A jump keeps the entity in the air for about 12 ticks, while air drag leaves it about 7.4 times its speed,
     * so it needs about a quarter of a block per tick to clear a block, which slow mobs like zombies are not.
     */
    private boolean jumpsGaps() {
        return entity instanceof LivingEntity && !flies() && speed(MovementGoal.DEFAULT_SPEED, false) >= JUMP_SPEED;
    }

    private boolean climbsWalls() {
        return entity instanceof Spider;
    }

    private boolean flies() {
        return entity instanceof Ghast || entity instanceof Phantom || entity instanceof Vex || entity instanceof Allay || entity instanceof Bee
                || entity instanceof Parrot || entity instanceof Bat || entity instanceof Blaze || entity instanceof Wither
                || entity instanceof EnderDragon || entity instanceof HappyGhast;
    }

    // entities without collisions, like vexes, fly through blocks
    private boolean ignoresBlocks() {
        return entity.hasNoPhysics();
    }

    // like vanilla's flying move controls, straight towards the wanted position in all directions
    private Vector fly(final Location current, final Location wanted, final boolean last, final double modifier) {
        final var delta = wanted.toVector().subtract(current.toVector());
        final var length = delta.length();
        if (length < 1.0E-3) return new Vector();
        if (delta.getX() * delta.getX() + delta.getZ() * delta.getZ() > 1.0E-4)
            movementYaw = rotateTowards(movementYaw, (float) Math.toDegrees(Math.atan2(-delta.getX(), delta.getZ())), MAX_TURN);
        var speed = flyingSpeed(modifier);
        if (last) speed = Math.min(speed, length);
        return delta.multiply(speed / length);
    }

    // vanilla's flying mobs all have their own move control, so this is a sensible average of them
    private double flyingSpeed(final double modifier) {
        final var attribute = entity instanceof final LivingEntity living ? living.getAttribute(Attribute.FLYING_SPEED) : null;
        final var relative = attribute != null ? attribute.getValue() / Attribute.FLYING_SPEED.getDefaultValue() : 1;
        return FLYING_SPEED * relative * modifier;
    }

    // the direction of a wall right next to the entity
    private @Nullable Vector wallDirection(final Location current) {
        final var block = current.getBlock();
        for (final var face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            final var relative = block.getRelative(face);
            if (relative.isPassable() || relative.isLiquid()) continue;
            return face.getDirection();
        }
        return null;
    }

    /*
     * Like players, sprinting mannequins kick up particles of the block below them,
     * which the client only shows for entities that have the sprinting flag, that can not be set for mannequins.
     * Every viewer has their own entity, so only the viewer sees them.
     */
    private void spawnSprintParticle(final Location current, final Vector velocity, @Nullable final Player viewer) {
        if (viewer == null || !entity.isOnGround()) return;
        final var block = current.clone().subtract(0, 0.2, 0).getBlock();
        if (block.isEmpty() || block.isLiquid() || block.getType() == Material.BARRIER) return;
        final var random = ThreadLocalRandom.current();
        final var width = entity.getWidth();
        final var location = current.clone().add((random.nextDouble() - 0.5) * width, 0.1, (random.nextDouble() - 0.5) * width);
        viewer.spawnParticle(Particle.BLOCK, location, 0, -velocity.getX() * 4, 1.5, -velocity.getZ() * 4, 1, block.getBlockData());
    }

    private void updateSprinting(final Location current, final Location target) {
        if (!(entity instanceof Mannequin)) return;
        final var distance = horizontalDistance(current, target);
        if (distance > SPRINT_START_DISTANCE) sprinting = true;
        else if (distance < SPRINT_STOP_DISTANCE) sprinting = false;
    }

    // like vanilla's MoveControl: turn towards the wanted position and walk the way the entity faces
    private Vector move(final Location current, final Location wanted, final boolean last, final double modifier) {
        final var dx = wanted.getX() - current.getX();
        final var dy = wanted.getY() - current.getY();
        final var dz = wanted.getZ() - current.getZ();
        final var horizontalSquared = dx * dx + dz * dz;
        // close above or below the wanted position, the direction to it would flip around and the entity would spin,
        // so it keeps walking the way it faces to drop down from a ledge it is still standing on,
        // and entities that climb walls push against the wall to climb up, vanilla makes them climb when they touch it
        if (horizontalSquared < STEERING_DISTANCE * STEERING_DISTANCE) {
            // up a ladder, vine or scaffolding
            if (dy > 0 && PathFinder.isClimbable(current.getBlock())) {
                climbing = true;
                return hugLadder(current, modifier, true);
            }
            // down a ladder, vine or scaffolding, walking into the wall would make it climb up again,
            // but while it still stands on the top of a ladder, it moves to the middle of it to drop in
            if (dy < -0.5 && (PathFinder.isClimbable(current.getBlock())
                    || PathFinder.isClimbable(current.getBlock().getRelative(0, -1, 0)))) {
                if (PathFinder.isClimbable(current.getBlock()) && !entity.isOnGround()) return hugLadder(current, modifier, false);
                final var distance = Math.sqrt(horizontalSquared);
                if (!entity.isOnGround() || distance < 0.01) return new Vector();
                final var speed = Math.min(speed(modifier, false), distance);
                return new Vector(dx / distance * speed, 0, dz / distance * speed);
            }
            if (dy < -0.5) {
                final var radians = Math.toRadians(movementYaw);
                final var speed = speed(modifier, PathFinder.isWater(current.getBlock()));
                return new Vector(-Math.sin(radians) * speed, 0, Math.cos(radians) * speed);
            }
            if (dy <= 0 || !climbsWalls()) return new Vector();
            final var wall = wallDirection(current);
            return wall != null ? wall.multiply(speed(modifier, false)) : new Vector();
        }

        final var wantedYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        // on a ladder, vine or scaffolding it moves straight towards the wanted position, like off the top onto a ledge,
        // turning first would make it walk out of the ladder and fall off
        if (isOnClimbable(current)) {
            movementYaw = wantedYaw;
            climbing = dy > 0;
            // climb until level with the wanted position, vanilla only steps up blocks on the ground,
            // moving sideways off the ladder or vine before that would make it fall
            if (dy > CLIMB_TOLERANCE && PathFinder.isClimbable(current.getBlock())) return hugLadder(current, modifier, true);
            final var distance = Math.sqrt(horizontalSquared);
            final var speed = Math.min(speed(modifier, false), distance);
            return new Vector(dx / distance * speed, 0, dz / distance * speed);
        }
        movementYaw = rotateTowards(movementYaw, wantedYaw, MAX_TURN);
        var speed = speed(modifier, PathFinder.isWater(current.getBlock()));
        // do not overshoot the target itself, nodes are only waypoints
        if (last) speed = Math.min(speed, Math.sqrt(horizontalSquared));

        final var radians = Math.toRadians(movementYaw);
        // only jump if a block in the way is higher than the entity can step up, not for every higher waypoint
        // climbing a ladder that is further away, like vanilla when walking into it
        climbing = dy > 0 && PathFinder.isClimbable(current.getBlock());
        // over a gap or a damaging block, the wanted position is two blocks away
        if (!climbing && horizontalSquared > 1.5 * 1.5 && isEdge(current, radians)) {
            jumping = true;
            return new Vector(-Math.sin(radians) * speed, 0, Math.cos(radians) * speed);
        }
        // climbers climb instead of jumping
        jumping = !climbing && !climbsWalls() && dy > stepHeight() && horizontalSquared < Math.max(1, entity.getWidth()) && isBlocked(current, radians);
        return new Vector(-Math.sin(radians) * speed, 0, Math.cos(radians) * speed);
    }

    // keeps the vertical velocity of vanilla, except for jumping and swimming
    private double vertical(final Location current, @Nullable final Movement movement) {
        final var velocity = entity.getVelocity().getY();
        if (PathFinder.isWater(current.getBlock())) switch (swimming()) {
            case DIVE -> {
                // keeps its depth while not moving
                if (movement == null) return 0;
                final var target = movement.target();
                final var speed = speed(movement.speed(), true);
                return Math.clamp((target.getY() - current.getY()) * 0.1, -speed, speed);
            }
            case SURFACE -> {
                if (jumping) return WATER_JUMP_VELOCITY;
                // like vanilla's FloatGoal, which keeps jumping while the water is deeper than the threshold,
                // but it keeps rising a bit longer before it sinks back, so it bobs instead of hovering
                final var depth = waterDepth(current);
                if (depth > waterJumpThreshold()) surfacing = true;
                else if (depth < waterJumpThreshold() - BOBBING) surfacing = false;
                if (surfacing) return velocity + BUOYANCY;
            }
            case SINK -> {
            }
        }
        if (climbing && PathFinder.isClimbable(current.getBlock())) return CLIMBING_VELOCITY;
        if (jumpDelay > 0) jumpDelay--;
        if (jumping && jumpDelay == 0 && entity.isOnGround()) {
            jumpDelay = JUMP_DELAY;
            return JUMP_VELOCITY;
        }
        return velocity;
    }

    // whether the entity is at the edge of a gap or a damaging block, where it has to jump off
    private boolean isEdge(final Location current, final double radians) {
        if (!entity.isOnGround()) return false;
        final var reach = halfWidth() + 0.3;
        final var x = (int) Math.floor(current.getX() - Math.sin(radians) * reach);
        final var z = (int) Math.floor(current.getZ() + Math.cos(radians) * reach);
        final var feet = (int) Math.floor(current.getY() + 0.01);
        final var floor = current.getWorld().getBlockAt(x, feet - 1, z);
        final var ahead = current.getWorld().getBlockAt(x, feet, z);
        return floor.isPassable() || floor.isLiquid() || PathFinder.isHarmful(floor) || PathFinder.isHarmful(ahead);
    }

    // whether the block right in front of the entity is too high to step up
    private boolean isBlocked(final Location current, final double radians) {
        final var reach = halfWidth() + 0.3;
        final var x = (int) Math.floor(current.getX() - Math.sin(radians) * reach);
        final var z = (int) Math.floor(current.getZ() + Math.cos(radians) * reach);
        final var block = current.getWorld().getBlockAt(x, (int) Math.floor(current.getY() + 0.01), z);
        if (block.isPassable() || PathFinder.isOpen(block) || block.isLiquid()) return false;
        return block.getBoundingBox().getMaxY() - current.getY() > stepHeight();
    }

    // how deep the entity is in the water, from its feet up to the surface
    private static double waterDepth(final Location current) {
        var block = current.getBlock();
        if (!PathFinder.isWater(block)) return 0;
        while (PathFinder.isWater(block.getRelative(0, 1, 0))) block = block.getRelative(0, 1, 0);
        return block.getY() + WATER_SURFACE - current.getY();
    }

    // like vanilla's fluid jump threshold, small entities float on top of the water
    private double waterJumpThreshold() {
        return eyeHeight() < 0.4 ? 0 : 0.4;
    }

    /*
     * The distance per tick at the given speed modifier, like vanilla goals use it with the movement speed attribute.
     * Entities other than mobs, like mannequins, keep the default movement speed of all living entities,
     * which is far higher than a player's, so they walk like players and only changes to the attribute count.
     */
    private double speed(final double modifier, final boolean water) {
        final var attribute = entity instanceof final LivingEntity living ? living.getAttribute(Attribute.MOVEMENT_SPEED) : null;
        if (entity instanceof Mob) {
            final var speed = (attribute != null ? attribute.getValue() : 0) * modifier;
            return water ? WATER_SPEED * speed : MOB_GROUND_SPEED * speed * speed;
        }
        final var relative = attribute != null ? attribute.getValue() / Attribute.MOVEMENT_SPEED.getDefaultValue() : 1;
        if (water) return SWIMMING_SPEED * relative * modifier;
        return WALKING_SPEED * relative * modifier * (sprinting ? SPRINTING_MULTIPLIER : 1);
    }

    private double stepHeight() {
        if (!(entity instanceof final LivingEntity living)) return DEFAULT_STEP_HEIGHT;
        final var attribute = living.getAttribute(Attribute.STEP_HEIGHT);
        return attribute != null ? attribute.getValue() : DEFAULT_STEP_HEIGHT;
    }

    private double followRange() {
        if (!(entity instanceof final LivingEntity living)) return 0;
        final var attribute = living.getAttribute(Attribute.FOLLOW_RANGE);
        return attribute != null ? attribute.getValue() : 0;
    }

    // vanilla's maximum distance to a waypoint before targeting the next one
    private double waypointDistance() {
        final var width = entity.getWidth();
        return width > 0.75 ? width / 2 : 0.75 - width / 2;
    }

    private boolean arrived(final Location current, final Location target, final double distance) {
        if (distance > 0) return current.distanceSquared(target) <= distance * distance;
        return horizontalDistance(current, target) <= waypointDistance() && Math.abs(current.getY() - target.getY()) <= ARRIVAL_HEIGHT;
    }

    private static double horizontalDistance(final Location from, final Location to) {
        final var dx = to.getX() - from.getX();
        final var dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private double halfWidth() {
        return entity.getWidth() / 2;
    }

    private void clearPath() {
        sprinting = false;
        path = List.of();
        pathReaches = false;
        pathGoal = null;
        pathIndex = 0;
        stuckTicks = 0;
    }

    private PathFinder pathFinder() {
        return new PathFinder(plugin.getServer(), entity.getWorld(), entity.getHeight(), eyeHeight(),
                opensDoors, swimming(), followRange(), climbsWalls(), flies(), jumpsGaps());
    }

    private double eyeHeight() {
        return entity instanceof final LivingEntity living ? living.getEyeHeight() : entity.getHeight();
    }

    /*
     * Aquatic entities dive, entities that can swim stay at the surface, everything else sinks.
     * Mobs can swim if vanilla lets their navigation float, which their float goal does when the mob is created,
     * mannequins swim like players.
     */
    private PathFinder.Swimming swimming() {
        final var swimming = this.swimming;
        if (swimming != null) return swimming;
        final PathFinder.Swimming result;
        if (Tag.ENTITY_TYPES_AQUATIC.isTagged(entity.getType())) result = PathFinder.Swimming.DIVE;
        else if (entity instanceof final Mob mob)
            result = mob.getPathfinder().canFloat() ? PathFinder.Swimming.SURFACE : PathFinder.Swimming.SINK;
        else if (entity instanceof Mannequin) result = PathFinder.Swimming.SURFACE;
        else result = PathFinder.Swimming.SINK;
        return this.swimming = result;
    }

    // looking

    /*
     * Like vanilla's LookControl and BodyRotationControl, the head turns gradually towards the look target,
     * the movement direction or the character's rotation once home, and stays within 75 degrees of the body.
     * The body faces the movement direction and slowly follows the head while standing.
     */
    private void rotate(@Nullable final Location look, final Location current, final boolean moving, final Location home) {
        final var headYaw = current.getYaw();
        final var pitch = current.getPitch();
        var bodyYaw = bodyYaw(current);
        final var atHome = !displaced && horizontalDistance(current, home) <= HOME_DISTANCE;

        final float wantedYaw;
        final float wantedPitch;
        final var direction = look != null ? look.toVector().subtract(eyes(current).toVector()) : null;
        if (direction != null && direction.lengthSquared() > 1.0E-4) {
            final var facing = eyes(current).setDirection(direction);
            wantedYaw = facing.getYaw();
            wantedPitch = facing.getPitch();
        } else if (moving) {
            wantedYaw = movementYaw;
            wantedPitch = 0;
        } else if (atHome) {
            wantedYaw = home.getYaw();
            wantedPitch = home.getPitch();
        } else {
            // like vanilla, the head returns to the body without a target
            wantedYaw = bodyYaw;
            wantedPitch = 0;
        }

        var newHeadYaw = rotateTowards(headYaw, wantedYaw, headRotationSpeed());
        final var newPitch = rotateTowards(pitch, wantedPitch, maxHeadPitch());
        if (moving) bodyYaw = movementYaw;
        else if (look == null && atHome) bodyYaw = rotateTowards(bodyYaw, home.getYaw(), headRotationSpeed());
        else bodyYaw = rotateTowards(bodyYaw, newHeadYaw, BODY_ROTATION_SPEED);
        newHeadYaw = bodyYaw + Math.clamp(wrapDegrees(newHeadYaw - bodyYaw), -MAX_HEAD_YAW, MAX_HEAD_YAW);

        if (differs(headYaw, newHeadYaw) || differs(pitch, newPitch)) entity.setRotation(newHeadYaw, newPitch);
        if (entity instanceof final LivingEntity living && differs(living.getBodyYaw(), bodyYaw))
            living.setBodyYaw(bodyYaw);
    }

    private float bodyYaw(final Location current) {
        return entity instanceof final LivingEntity living ? living.getBodyYaw() : current.getYaw();
    }

    private float headRotationSpeed() {
        return entity instanceof final Mob mob ? mob.getHeadRotationSpeed() : DEFAULT_HEAD_ROTATION_SPEED;
    }

    private float maxHeadPitch() {
        return entity instanceof final Mob mob ? mob.getMaxHeadPitch() : DEFAULT_MAX_HEAD_PITCH;
    }

    private Location eyes(final Location location) {
        return location.clone().add(0, entity instanceof final LivingEntity living ? living.getEyeHeight() : 0, 0);
    }

    private static float rotateTowards(final float from, final float to, final float maxDelta) {
        return from + Math.clamp(wrapDegrees(to - from), -maxDelta, maxDelta);
    }

    private static float wrapDegrees(final float degrees) {
        var wrapped = degrees % 360;
        if (wrapped >= 180) wrapped -= 360;
        if (wrapped < -180) wrapped += 360;
        return wrapped;
    }

    private static boolean differs(final float a, final float b) {
        return Math.abs(wrapDegrees(a - b)) > ROTATION_EPSILON;
    }

    // targets

    // the target, if it is close enough to look at, like a look goal with the default range
    private @Nullable Location inLookRange(final Location current, @Nullable final Location target) {
        if (target == null || !target.getWorld().equals(current.getWorld())) return null;
        final var range = LookAtGoal.DEFAULT_RANGE;
        return target.distanceSquared(eyes(current)) <= range * range ? target : null;
    }

    // like resolve, but only players
    private @Nullable Player resolvePlayer(final Target target, @Nullable final Player viewer, final Location origin) {
        return switch (target) {
            case final Target.Viewer ignored -> viewer;
            case final Target.OfPlayer ofPlayer -> plugin.getServer().getPlayer(ofPlayer.player());
            case final Target.NearestPlayer nearest -> viewer == null ? null : origin.getNearbyPlayers(nearest.radius()).stream()
                    .filter(player -> player.getGameMode() != GameMode.SPECTATOR)
                    .filter(player -> player.equals(viewer) || viewer.canSee(player))
                    .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(origin)))
                    .orElse(null);
            case final Target.Position ignored -> null;
        };
    }

    // the nearest player is the one closest to the origin
    private @Nullable Location resolve(final Target target, @Nullable final Player viewer, final Location origin, final boolean eyes) {
        return switch (target) {
            case final Target.Viewer ignored -> viewer != null ? position(viewer, eyes) : null;
            case final Target.OfPlayer ofPlayer -> {
                final var player = plugin.getServer().getPlayer(ofPlayer.player());
                yield player != null ? position(player, eyes) : null;
            }
            // only players the viewer can see, vanished or hidden players would give themselves away
            case final Target.NearestPlayer nearest ->
                    viewer == null ? null : origin.getNearbyPlayers(nearest.radius()).stream()
                            .filter(player -> player.getGameMode() != GameMode.SPECTATOR)
                            .filter(player -> player.equals(viewer) || viewer.canSee(player))
                            .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(origin)))
                            .map(player -> position(player, eyes))
                            .orElse(null);
            case final Target.Position position -> position.location();
        };
    }

    private static Location position(final Player player, final boolean eyes) {
        return eyes ? player.getEyeLocation() : player.getLocation();
    }
}
