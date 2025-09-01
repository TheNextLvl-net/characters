package net.thenextlvl.character.attribute;

import io.papermc.paper.entity.CollarColorable;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.util.TriState;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Particle;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Allay;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Tameable;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
public class AttributeTypes {
    private static final Set<AttributeType<?, ?>> attributeTypes = new HashSet<>();

    public static final AgeableAttributes AGEABLE = new AgeableAttributes();
    public static final AreaEffectCloudAttributes AREA_EFFECT_CLOUD = new AreaEffectCloudAttributes();
    public static final ArrowAttributes ARROW = new ArrowAttributes();
    public static final AttributableAttributes ATTRIBUTABLE = new AttributableAttributes();
    public static final CatAttributes CAT = new CatAttributes();
    public static final CollarColorableAttributes COLLAR_COLORABLE = new CollarColorableAttributes();
    public static final CreeperAttributes CREEPER = new CreeperAttributes();
    public static final DamageableAttributes DAMAGEABLE = new DamageableAttributes();
    public static final EndermanAttributes ENDERMAN = new EndermanAttributes();
    public static final EntityAttributes ENTITY = new EntityAttributes();
    public static final FrogAttributes FROG = new FrogAttributes();
    public static final LivingEntityAttributes LIVING_ENTITY = new LivingEntityAttributes();
    public static final MobAttributes MOB = new MobAttributes();
    public static final SittableAttributes SITTABLE = new SittableAttributes();
    public static final SteerableAttributes STEERABLE = new SteerableAttributes();
    public static final TameableAttributes TAMEABLE = new TameableAttributes();

    public static Set<AttributeType<?, ?>> types() {
        return Set.copyOf(attributeTypes);
    }

    public static Optional<AttributeType<?, ?>> getByKey(Key key) {
        return attributeTypes.stream().filter(type -> type.key().equals(key)).findAny();
    }

    public static class AgeableAttributes {
        public final AttributeType<Ageable, Boolean> BABY = register(
                "ageable:baby", Ageable.class, boolean.class,
                ageable -> !ageable.isAdult(), ageable -> false, (ageable, baby) -> {
                    if (baby) ageable.setBaby();
                    else ageable.setAdult();
                });
    }

    public static class AreaEffectCloudAttributes {
        public final AttributeType<AreaEffectCloud, @Nullable PotionType> BASE_POTION_TYPE = registerNullable(
                "area_effect_cloud:base_potion_type", AreaEffectCloud.class, PotionType.class,
                AreaEffectCloud::getBasePotionType, areaEffectCloud -> null, AreaEffectCloud::setBasePotionType
        );

        public final AttributeType<AreaEffectCloud, Color> COLOR = registerNullable(
                "area_effect_cloud:color", AreaEffectCloud.class, Color.class,
                AreaEffectCloud::getColor, areaEffectCloud -> null, AreaEffectCloud::setColor
        );

        public final AttributeType<AreaEffectCloud, Duration> DURATION_ON_USE = register(
                "area_effect_cloud:duration_on_use", AreaEffectCloud.class, Duration.class,
                cloud -> Tick.of(cloud.getDurationOnUse()), areaEffectCloud -> Duration.ZERO,
                (cloud, duration) -> cloud.setDurationOnUse(Tick.tick().fromDuration(duration))
        );

        public final AttributeType<AreaEffectCloud, Particle> PARTICLE = register(
                "area_effect_cloud:particle", AreaEffectCloud.class, Particle.class,
                AreaEffectCloud::getParticle, areaEffectCloud -> Particle.ENTITY_EFFECT, AreaEffectCloud::setParticle
        );

        public final AttributeType<AreaEffectCloud, Float> RADIUS = register(
                "area_effect_cloud:radius", AreaEffectCloud.class, float.class,
                AreaEffectCloud::getRadius, areaEffectCloud -> 3F, AreaEffectCloud::setRadius
        );

        public final AttributeType<AreaEffectCloud, Duration> REAPPLICATION_DELAY = register(
                "area_effect_cloud:reapplication_delay", AreaEffectCloud.class, Duration.class,
                cloud -> Tick.of(cloud.getDurationOnUse()), areaEffectCloud -> Duration.ZERO,
                (cloud, duration) -> cloud.setReapplicationDelay(Tick.tick().fromDuration(duration))
        );

        public final AttributeType<AreaEffectCloud, Duration> WAIT_TIME = register(
                "area_effect_cloud:wait_time", AreaEffectCloud.class, Duration.class,
                cloud -> Tick.of(cloud.getWaitTime()), areaEffectCloud -> Duration.ofSeconds(1),
                (cloud, duration) -> cloud.setWaitTime(Tick.tick().fromDuration(duration))
        );
    }

    public static class ArrowAttributes {
        public final AttributeType<Arrow, @Nullable Color> TRAIL_COLOR = registerNullable(
                "arrow:trail_color", Arrow.class, Color.class,
                Arrow::getColor, arrow -> null, Arrow::setColor
        );
    }

    public static class AttributableAttributes {
        public final AttributeType<Attributable, Double> ARMOR = register(
                "attributable:armor", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.ARMOR, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.ARMOR, value)
        );

        public final AttributeType<Attributable, Double> ARMOR_TOUGHNESS = register(
                "attributable:armor_toughness", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.ARMOR_TOUGHNESS, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.ARMOR_TOUGHNESS, value)
        );

        public final AttributeType<Attributable, Double> ATTACK_DAMAGE = register(
                "attributable:attack_damage", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.ATTACK_DAMAGE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.ATTACK_DAMAGE, value)
        );

        public final AttributeType<Attributable, Double> ATTACK_KNOCKBACK = register(
                "attributable:attack_knockback", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.ATTACK_KNOCKBACK, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.ATTACK_KNOCKBACK, value)
        );

        public final AttributeType<Attributable, Double> ATTACK_SPEED = register(
                "attributable:attack_speed", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.ATTACK_SPEED, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.ATTACK_SPEED, value)
        );

        public final AttributeType<Attributable, Double> BLOCK_BREAK_SPEED = register(
                "attributable:block_break_speed", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.BLOCK_BREAK_SPEED, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.BLOCK_BREAK_SPEED, value)
        );

        public final AttributeType<Attributable, Double> BLOCK_INTERACTION_RANGE = register(
                "attributable:block_interaction_range", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.BLOCK_INTERACTION_RANGE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.BLOCK_INTERACTION_RANGE, value)
        );

        public final AttributeType<Attributable, Double> BURNING_TIME = register(
                "attributable:burning_time", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.BURNING_TIME, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.BURNING_TIME, value)
        );

        public final AttributeType<Attributable, Double> ENTITY_INTERACTION_RANGE = register(
                "attributable:entity_interaction_range", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.ENTITY_INTERACTION_RANGE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.ENTITY_INTERACTION_RANGE, value)
        );

        public final AttributeType<Attributable, Double> EXPLOSION_KNOCKBACK_RESISTANCE = register(
                "attributable:explosion_knockback_resistance", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.EXPLOSION_KNOCKBACK_RESISTANCE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.EXPLOSION_KNOCKBACK_RESISTANCE, value)
        );

        public final AttributeType<Attributable, Double> FALL_DAMAGE_MULTIPLIER = register(
                "attributable:fall_damage_multiplier", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.FALL_DAMAGE_MULTIPLIER, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.FALL_DAMAGE_MULTIPLIER, value)
        );

        public final AttributeType<Attributable, Double> FLYING_SPEED = register(
                "attributable:flying_speed", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.FLYING_SPEED, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.FLYING_SPEED, value)
        );

        public final AttributeType<Attributable, Double> FOLLOW_RANGE = register(
                "attributable:follow_range", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.FOLLOW_RANGE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.FOLLOW_RANGE, value)
        );

        public final AttributeType<Attributable, Double> GRAVITY = register(
                "attributable:gravity", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.GRAVITY, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.GRAVITY, value)
        );

        public final AttributeType<Attributable, Double> JUMP_STRENGTH = register(
                "attributable:jump_strength", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.JUMP_STRENGTH, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.JUMP_STRENGTH, value)
        );

        public final AttributeType<Attributable, Double> KNOCKBACK_RESISTANCE = register(
                "attributable:knockback_resistance", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.KNOCKBACK_RESISTANCE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.KNOCKBACK_RESISTANCE, value)
        );

        public final AttributeType<Attributable, Double> LUCK = register(
                "attributable:luck", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.LUCK, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.LUCK, value)
        );

        public final AttributeType<Attributable, Double> MAX_ABSORPTION = register(
                "attributable:max_absorption", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.MAX_ABSORPTION, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.MAX_ABSORPTION, value)
        );

        public final AttributeType<Attributable, Double> MAX_HEALTH = register(
                "attributable:max_health", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.MAX_HEALTH, 20),
                attributable -> 20D,
                (attributable, value) -> set(attributable, Attribute.MAX_HEALTH, value)
        );

        public final AttributeType<Attributable, Double> MINING_EFFICIENCY = register(
                "attributable:mining_efficiency", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.MINING_EFFICIENCY, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.MINING_EFFICIENCY, value)
        );

        public final AttributeType<Attributable, Double> MOVEMENT_EFFICIENCY = register(
                "attributable:movement_efficiency", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.MOVEMENT_EFFICIENCY, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.MOVEMENT_EFFICIENCY, value)
        );

        public final AttributeType<Attributable, Double> MOVEMENT_SPEED = register(
                "attributable:movement_speed", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.MOVEMENT_SPEED, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.MOVEMENT_SPEED, value)
        );

        public final AttributeType<Attributable, Double> OXYGEN_BONUS = register(
                "attributable:oxygen_bonus", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.OXYGEN_BONUS, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.OXYGEN_BONUS, value)
        );

        public final AttributeType<Attributable, Double> SAFE_FALL_DISTANCE = register(
                "attributable:safe_fall_distance", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.SAFE_FALL_DISTANCE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.SAFE_FALL_DISTANCE, value)
        );

        public final AttributeType<Attributable, Double> SCALE = register(
                "attributable:scale", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.SCALE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.SCALE, value)
        );

        public final AttributeType<Attributable, Double> SNEAKING_SPEED = register(
                "attributable:sneaking_speed", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.SNEAKING_SPEED, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.SNEAKING_SPEED, value)
        );

        public final AttributeType<Attributable, Double> SPAWN_REINFORCEMENTS = register(
                "attributable:spawn_reinforcements", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.SPAWN_REINFORCEMENTS, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.SPAWN_REINFORCEMENTS, value)
        );

        public final AttributeType<Attributable, Double> STEP_HEIGHT = register(
                "attributable:step_height", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.STEP_HEIGHT, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.STEP_HEIGHT, value)
        );

        public final AttributeType<Attributable, Double> SUBMERGED_MINING_SPEED = register(
                "attributable:submerged_mining_speed", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.SUBMERGED_MINING_SPEED, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.SUBMERGED_MINING_SPEED, value)
        );

        public final AttributeType<Attributable, Double> SWEEPING_DAMAGE_RATIO = register(
                "attributable:sweeping_damage_ratio", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.SWEEPING_DAMAGE_RATIO, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.SWEEPING_DAMAGE_RATIO, value)
        );

        public final AttributeType<Attributable, Double> TEMPT_RANGE = register(
                "attributable:tempt_range", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.TEMPT_RANGE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.TEMPT_RANGE, value)
        );

        public final AttributeType<Attributable, Double> WATER_MOVEMENT_EFFICIENCY = register(
                "attributable:water_movement_efficiency", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.WATER_MOVEMENT_EFFICIENCY, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.WATER_MOVEMENT_EFFICIENCY, value)
        );

        public final AttributeType<Attributable, Double> WAYPOINT_TRANSMIT_RANGE = register(
                "attributable:waypoint_transmit_range", Attributable.class, double.class,
                attributable -> get(attributable, Attribute.WAYPOINT_TRANSMIT_RANGE, 0),
                attributable -> 0D,
                (attributable, value) -> set(attributable, Attribute.WAYPOINT_TRANSMIT_RANGE, value)
        );

        private double get(Attributable attributable, Attribute attribute, double defaultValue) {
            var instance = attributable.getAttribute(attribute);
            return instance != null ? instance.getDefaultValue() : defaultValue;
        }

        private void set(Attributable attributable, Attribute attribute, double value) {
            var instance = attributable.getAttribute(attribute);
            if (instance != null) instance.setBaseValue(value);
        }
    }

    public static class CatAttributes {
        public final AttributeType<Cat, Boolean> HEAD_UP = register(
                "cat:head_up", Cat.class, boolean.class, Cat::isHeadUp, cat -> false, Cat::setHeadUp
        );

        public final AttributeType<Cat, Boolean> LYING_DOWN = register(
                "cat:lying_down", Cat.class, boolean.class, Cat::isLyingDown, cat -> false, Cat::setLyingDown
        );

        public final AttributeType<Cat, Cat.Type> VARIANT = register(
                "cat:variant", Cat.class, Cat.Type.class, Cat::getCatType, cat -> Cat.Type.ALL_BLACK, Cat::setCatType
        );
    }

    public static class AllayAttributes {
        public final AttributeType<Allay, Boolean> DANCING = register(
                "allay:dancing", Allay.class, boolean.class,
                Allay::isDancing, allay -> false, (allay, dancing) -> {
                    if (dancing) allay.startDancing();
                    else allay.stopDancing();
                });
    }

    public static class CollarColorableAttributes {
        public final AttributeType<CollarColorable, DyeColor> COLLAR_COLOR = register(
                "collar_colorable:collar_color", CollarColorable.class, DyeColor.class,
                CollarColorable::getCollarColor, collarColorable -> DyeColor.RED, CollarColorable::setCollarColor
        );
    }

    public static class CreeperAttributes {
        public final AttributeType<Creeper, Boolean> POWERED = register(
                "creeper:powered", Creeper.class, boolean.class,
                Creeper::isPowered, creeper -> false, Creeper::setPowered
        );
    }

    public static class DamageableAttributes {
        public final AttributeType<Damageable, Double> ABSORPTION_AMOUNT = register(
                "damageable:absorption_amount", Damageable.class, double.class,
                Damageable::getAbsorptionAmount, damageable -> 0D, Damageable::setAbsorptionAmount
        );
    }

    public static class EndermanAttributes {
        public final AttributeType<Enderman, @Nullable BlockData> CARRIED_BLOCK = registerNullable(
                "enderman:carried_block", Enderman.class, BlockData.class,
                Enderman::getCarriedBlock, enderman -> null, Enderman::setCarriedBlock
        );

        public final AttributeType<Enderman, Boolean> SCREAMING = register(
                "enderman:screaming", Enderman.class, boolean.class,
                Enderman::isScreaming, enderman -> false, Enderman::setScreaming
        );

        public final AttributeType<Enderman, Boolean> STARED_AT = register(
                "enderman:stared_at", Enderman.class, Boolean.class,
                Enderman::hasBeenStaredAt, enderman -> false, Enderman::setHasBeenStaredAt
        );
    }

    public static class EntityAttributes {
        public final AttributeType<Entity, TriState> VISUAL_FIRE = register(
                "entity:visual_fire", Entity.class, TriState.class,
                Entity::getVisualFire, entity -> TriState.NOT_SET, Entity::setVisualFire
        );

        public final AttributeType<Entity, Integer> FIRE_TICKS = register(
                "entity:fire_ticks", Entity.class, int.class,
                Entity::getFireTicks, entity -> 0, Entity::setFireTicks
        );

        public final AttributeType<Entity, Boolean> GLOWING = register(
                "entity:glowing", Entity.class, boolean.class,
                Entity::isGlowing, entity -> false, Entity::setGlowing
        );

        public final AttributeType<Entity, Boolean> GRAVITY = register(
                "entity:gravity", Entity.class, boolean.class,
                Entity::hasGravity, entity -> false, Entity::setGravity
        );

        public final AttributeType<Entity, Boolean> INVISIBLE = register(
                "entity:invisible", Entity.class, boolean.class,
                Entity::isInvisible, entity -> false, Entity::setInvisible
        );

        public final AttributeType<Entity, Boolean> INVULNERABLE = register(
                "entity:invulnerable", Entity.class, boolean.class,
                Entity::isInvulnerable, entity -> true, Entity::setInvulnerable
        );

        public final AttributeType<Entity, Boolean> PHYSICS = register(
                "entity:physics", Entity.class, boolean.class,
                entity -> !entity.hasNoPhysics(), entity -> false, (entity, physics) -> entity.setNoPhysics(!physics)
        );

        public final AttributeType<Entity, Pose> POSE = register(
                "entity:pose", Entity.class, Pose.class,
                Entity::getPose, entity -> Pose.STANDING, (entity, pose) -> entity.setPose(pose, true)
        );

        public final AttributeType<Entity, Boolean> FREEZING = register(
                "entity:freezing", Entity.class, boolean.class,
                Entity::isFrozen, entity -> false, (entity, freezing) -> entity.setFreezeTicks(freezing ? entity.getMaxFreezeTicks() : 0)
        );

        public final AttributeType<Entity, Boolean> LOCK_FREEZE_TICKS = register(
                "entity:lock_freeze_ticks", Entity.class, boolean.class,
                Entity::isFreezeTickingLocked, entity -> true, Entity::lockFreezeTicks
        );

        public final AttributeType<Entity, Boolean> SILENT = register(
                "entity:silent", Entity.class, boolean.class,
                Entity::isSilent, entity -> true, Entity::setSilent
        );

        public final AttributeType<Entity, Boolean> SNEAKING = register(
                "entity:sneaking", Entity.class, boolean.class,
                Entity::isSneaking, entity -> false, Entity::setSneaking
        );
    }

    public static class FoxAttributes {
        public final AttributeType<Fox, Boolean> CROUCHING = register(
                "fox:crouching", Fox.class, boolean.class,
                Fox::isCrouching, fox -> false, Fox::setCrouching
        );

        public final AttributeType<Fox, Boolean> LEAPING = register(
                "fox:leaping", Fox.class, boolean.class,
                Fox::isLeaping, fox -> false, Fox::setLeaping
        );

        public final AttributeType<Fox, Boolean> SLEEPING = register(
                "fox:sleeping", Fox.class, boolean.class,
                Fox::isSleeping, fox -> false, Fox::setSleeping
        );

        public final AttributeType<Fox, Fox.Type> VARIANT = register(
                "fox:variant", Fox.class, Fox.Type.class,
                Fox::getFoxType, fox -> Fox.Type.RED, Fox::setFoxType
        );
    }

    public static class FrogAttributes {
        public final AttributeType<Frog, Frog.Variant> VARIANT = register(
                "frog:variant", Frog.class, Frog.Variant.class,
                Frog::getVariant, frog -> Frog.Variant.COLD, Frog::setVariant
        );
    }

    public static class LivingEntityAttributes {
        public final AttributeType<LivingEntity, Boolean> AI = register(
                "living_entity:ai", LivingEntity.class, boolean.class,
                LivingEntity::hasAI, livingEntity -> false, LivingEntity::setAI
        );

        public final AttributeType<LivingEntity, Integer> ARROWS_IN_BODY = register(
                "living_entity:arrows_in_body", LivingEntity.class, int.class,
                LivingEntity::getArrowsInBody, livingEntity -> 0, LivingEntity::setArrowsInBody
        );

        public final AttributeType<LivingEntity, Integer> BEE_STINGERS_IN_BODY = register(
                "living_entity:bee_stingers_in_body", LivingEntity.class, int.class,
                LivingEntity::getBeeStingersInBody, livingEntity -> 0, LivingEntity::setBeeStingersInBody
        );

        public final AttributeType<LivingEntity, Float> BODY_YAW = register(
                "living_entity:body_yaw", LivingEntity.class, float.class,
                LivingEntity::getBodyYaw, livingEntity -> 0F, LivingEntity::setBodyYaw
        );

        public final AttributeType<LivingEntity, Boolean> COLLIDABLE = register(
                "living_entity:collidable", LivingEntity.class, boolean.class,
                LivingEntity::isCollidable, livingEntity -> true, LivingEntity::setCollidable
        );
    }

    public static class MobAttributes {
        public final AttributeType<Mob, Boolean> AGGRESSIVE = register(
                "mob:aggressive", Mob.class, boolean.class, Mob::isAggressive, mob -> false, Mob::setAggressive
        );

        public final AttributeType<Mob, Boolean> AWARE = register(
                "mob:aware", Mob.class, boolean.class, Mob::isAware, mob -> true, Mob::setAware
        );

        public final AttributeType<Mob, Boolean> LEFT_HANDED = register(
                "mob:left_handed", Mob.class, boolean.class, Mob::isLeftHanded, mob -> false, Mob::setLeftHanded
        );
    }

    public static class SittableAttributes {
        public final AttributeType<Sittable, Boolean> SITTING = register(
                "sittable:sitting", Sittable.class, boolean.class,
                Sittable::isSitting, sittable -> false, Sittable::setSitting
        );
    }

    public static class SteerableAttributes {
        public final AttributeType<Steerable, Boolean> SADDLE = register(
                "steerable:saddled", Steerable.class, boolean.class,
                Steerable::hasSaddle, steerable -> false, Steerable::setSaddle
        );
    }

    public static class TameableAttributes {
        public final AttributeType<Tameable, Boolean> TAMED = register(
                "tameable:tamed", Tameable.class, boolean.class, Tameable::isTamed, tameable -> false, Tameable::setTamed
        );
    }

    public static <E, T> AttributeType<E, @Nullable T> registerNullable(
            @KeyPattern String key, Class<E> entityType, Class<T> dataType,
            Function<E, @Nullable T> getter, Function<E, @Nullable T> defaultGetter, BiConsumer<E, @Nullable T> setter
    ) {
        return register(key, entityType, dataType, getter, defaultGetter, setter);
    }

    public static <E, T> AttributeType<E, T> register(
            @KeyPattern String key, Class<E> entityType, Class<T> dataType,
            Function<E, T> getter, Function<E, T> defaultGetter, BiConsumer<E, T> setter
    ) {
        var attributeType = new AttributeType<>(Key.key(key), entityType, dataType, getter, defaultGetter, setter);
        if (attributeTypes.add(attributeType)) return attributeType;
        throw new IllegalStateException("Cannot register attribute types twice: " + key);
    }
}
