package net.thenextlvl.character.attribute;

import io.papermc.paper.entity.CollarColorable;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
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
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
public class AttributeTypes {
    private static final Set<AttributeType<?, ?>> attributeTypes = new LinkedHashSet<>();

    public static final AgeableAttributes AGEABLE = new AgeableAttributes();
    public static final AreaEffectCloudAttributes AREA_EFFECT_CLOUD = new AreaEffectCloudAttributes();
    public static final ArrowAttributes ARROW = new ArrowAttributes();
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
                ageable -> !ageable.isAdult(), (ageable, baby) -> {
                    if (baby) ageable.setBaby();
                    else ageable.setAdult();
                });
    }

    public static class AreaEffectCloudAttributes {
        public final AttributeType<AreaEffectCloud, @Nullable PotionType> BASE_POTION_TYPE = registerNullable(
                "area_effect_cloud:base_potion_type", AreaEffectCloud.class, PotionType.class,
                AreaEffectCloud::getBasePotionType, AreaEffectCloud::setBasePotionType
        );

        public final AttributeType<AreaEffectCloud, Color> COLOR = register(
                "area_effect_cloud:color", AreaEffectCloud.class, Color.class,
                AreaEffectCloud::getColor, AreaEffectCloud::setColor
        );

        public final AttributeType<AreaEffectCloud, Duration> DURATION_ON_USE = register(
                "area_effect_cloud:duration_on_use", AreaEffectCloud.class, Duration.class,
                cloud -> Tick.of(cloud.getDurationOnUse()), (cloud, duration) -> cloud.setDurationOnUse(Tick.tick().fromDuration(duration))
        );

        public final AttributeType<AreaEffectCloud, Particle> PARTICLE = register(
                "area_effect_cloud:particle", AreaEffectCloud.class, Particle.class,
                AreaEffectCloud::getParticle, AreaEffectCloud::setParticle
        );

        public final AttributeType<AreaEffectCloud, Float> RADIUS = register(
                "area_effect_cloud:radius", AreaEffectCloud.class, float.class,
                AreaEffectCloud::getRadius, AreaEffectCloud::setRadius
        );

        public final AttributeType<AreaEffectCloud, Duration> REAPPLICATION_DELAY = register(
                "area_effect_cloud:reapplication_delay", AreaEffectCloud.class, Duration.class,
                cloud -> Tick.of(cloud.getDurationOnUse()), (cloud, duration) -> cloud.setReapplicationDelay(Tick.tick().fromDuration(duration))
        );

        public final AttributeType<AreaEffectCloud, Duration> WAIT_TIME = register(
                "area_effect_cloud:wait_time", AreaEffectCloud.class, Duration.class,
                cloud -> Tick.of(cloud.getWaitTime()), (cloud, duration) -> cloud.setWaitTime(Tick.tick().fromDuration(duration))
        );
    }

    public static class ArrowAttributes {
        public final AttributeType<Arrow, @Nullable Color> TRAIL_COLOR = registerNullable(
                "arrow:trail_color", Arrow.class, Color.class,
                Arrow::getColor, Arrow::setColor
        );
    }

    public static class CatAttributes {
        public final AttributeType<Cat, Boolean> HEAD_UP = register(
                "cat:head_up", Cat.class, boolean.class, Cat::isHeadUp, Cat::setHeadUp
        );

        public final AttributeType<Cat, Boolean> LYING_DOWN = register(
                "cat:lying_down", Cat.class, boolean.class, Cat::isLyingDown, Cat::setLyingDown
        );

        public final AttributeType<Cat, Cat.Type> VARIANT = register(
                "cat:variant", Cat.class, Cat.Type.class, Cat::getCatType, Cat::setCatType
        );
    }

    public static class AllayAttributes {
        public final AttributeType<Allay, Boolean> DANCING = register(
                "allay:dancing", Allay.class, boolean.class,
                Allay::isDancing, (allay, dancing) -> {
                    if (dancing) allay.startDancing();
                    else allay.stopDancing();
                });
    }

    public static class CollarColorableAttributes {
        public final AttributeType<CollarColorable, DyeColor> COLLAR_COLOR = register(
                "collar_colorable:collar_color", CollarColorable.class, DyeColor.class,
                CollarColorable::getCollarColor, CollarColorable::setCollarColor
        );
    }

    public static class CreeperAttributes {
        public final AttributeType<Creeper, Boolean> POWERED = register(
                "creeper:powered", Creeper.class, boolean.class,
                Creeper::isPowered, Creeper::setPowered
        );
    }

    public static class DamageableAttributes {
        public final AttributeType<Damageable, Double> ABSORPTION_AMOUNT = register(
                "damageable:absorption_amount", Damageable.class, double.class,
                Damageable::getAbsorptionAmount, Damageable::setAbsorptionAmount
        );

        public final AttributeType<Attributable, Double> MAX_HEALTH = register(
                "damageable:max_health", Attributable.class, double.class, attributable -> {
                    var attribute = attributable.getAttribute(Attribute.MAX_HEALTH);
                    if (attribute != null) return attribute.getDefaultValue();
                    return 20d;
                }, (attributable, maxHealth) -> {
                    var attribute = attributable.getAttribute(Attribute.MAX_HEALTH);
                    if (attribute != null) attribute.setBaseValue(maxHealth);
                });

        public final AttributeType<Damageable, Double> HEALTH = register(
                "damageable:health", Damageable.class, double.class,
                Damageable::getHealth, Damageable::setHealth
        );
    }

    public static class EndermanAttributes {
        public final AttributeType<Enderman, @Nullable BlockData> CARRIED_BLOCK = registerNullable(
                "enderman:carried_block", Enderman.class, BlockData.class,
                Enderman::getCarriedBlock, Enderman::setCarriedBlock
        );

        public final AttributeType<Enderman, Boolean> SCREAMING = register(
                "enderman:screaming", Enderman.class, boolean.class,
                Enderman::isScreaming, Enderman::setScreaming
        );

        public final AttributeType<Enderman, Boolean> STARED_AT = register(
                "enderman:stared_at", Enderman.class, Boolean.class,
                Enderman::hasBeenStaredAt, Enderman::setHasBeenStaredAt
        );
    }

    public static class EntityAttributes {
        public final AttributeType<Entity, Boolean> VISUAL_FIRE = register(
                "entity:visual_fire", Entity.class, boolean.class,
                Entity::isVisualFire, Entity::setVisualFire
        );

        public final AttributeType<Entity, Integer> FIRE_TICKS = register(
                "entity:fire_ticks", Entity.class, int.class,
                Entity::getFireTicks, Entity::setFireTicks
        );

        public final AttributeType<Entity, Boolean> GLOWING = register(
                "entity:glowing", Entity.class, boolean.class,
                Entity::isGlowing, Entity::setGlowing
        );

        public final AttributeType<Entity, Boolean> GRAVITY = register(
                "entity:gravity", Entity.class, boolean.class,
                Entity::hasGravity, Entity::setGravity
        );

        public final AttributeType<Entity, Boolean> INVISIBLE = register(
                "entity:invisible", Entity.class, boolean.class,
                Entity::isInvisible, Entity::setInvisible
        );

        public final AttributeType<Entity, Boolean> INVULNERABLE = register(
                "entity:invulnerable", Entity.class, boolean.class,
                Entity::isInvulnerable, Entity::setInvulnerable
        );

        public final AttributeType<Entity, Boolean> PHYSICS = register(
                "entity:physics", Entity.class, boolean.class,
                entity -> !entity.hasNoPhysics(), (entity, physics) -> entity.setNoPhysics(!physics)
        );

        public final AttributeType<Entity, Pose> POSE = register(
                "entity:pose", Entity.class, Pose.class,
                Entity::getPose, (entity, pose) -> entity.setPose(pose, true)
        );

        public final AttributeType<Entity, Boolean> FREEZING = register(
                "entity:freezing", Entity.class, boolean.class,
                Entity::isFrozen, (entity, shaking) -> entity.setFreezeTicks(shaking ? entity.getMaxFreezeTicks() : 0)
        );

        public final AttributeType<Entity, Boolean> SILENT = register(
                "entity:silent", Entity.class, boolean.class,
                Entity::isSilent, Entity::setSilent
        );

        public final AttributeType<Entity, Boolean> SNEAKING = register(
                "entity:sneaking", Entity.class, boolean.class,
                Entity::isSneaking, Entity::setSneaking
        );
    }

    public static class FoxAttributes {
        public final AttributeType<Fox, Boolean> CROUCHING = register(
                "fox:crouching", Fox.class, boolean.class,
                Fox::isCrouching, Fox::setCrouching
        );

        public final AttributeType<Fox, Boolean> LEAPING = register(
                "fox:leaping", Fox.class, boolean.class,
                Fox::isLeaping, Fox::setLeaping
        );

        public final AttributeType<Fox, Boolean> SLEEPING = register(
                "fox:sleeping", Fox.class, boolean.class,
                Fox::isSleeping, Fox::setSleeping
        );

        public final AttributeType<Fox, Fox.Type> VARIANT = register(
                "fox:variant", Fox.class, Fox.Type.class,
                Fox::getFoxType, Fox::setFoxType
        );
    }

    public static class FrogAttributes {
        public final AttributeType<Frog, Frog.Variant> VARIANT = register(
                "frog:variant", Frog.class, Frog.Variant.class,
                Frog::getVariant, Frog::setVariant
        );
    }

    public static class LivingEntityAttributes {
        public final AttributeType<LivingEntity, Integer> ARROWS_IN_BODY = register(
                "living_entity:arrows_in_body", LivingEntity.class, int.class,
                LivingEntity::getArrowsInBody, LivingEntity::setArrowsInBody
        );

        public final AttributeType<LivingEntity, Integer> BEE_STINGERS_IN_BODY = register(
                "living_entity:bee_stingers_in_body", LivingEntity.class, int.class,
                LivingEntity::getBeeStingersInBody, LivingEntity::setBeeStingersInBody
        );

        public final AttributeType<LivingEntity, Float> BODY_YAW = register(
                "living_entity:body_yaw", LivingEntity.class, float.class,
                LivingEntity::getBodyYaw, LivingEntity::setBodyYaw
        );

        public final AttributeType<LivingEntity, Boolean> COLLIDABLE = register(
                "living_entity:collidable", LivingEntity.class, boolean.class,
                LivingEntity::isCollidable, LivingEntity::setCollidable
        );
    }

    public static class MobAttributes {
        public final AttributeType<Mob, Boolean> AGGRESSIVE = register(
                "mob:aggressive", Mob.class, boolean.class, Mob::isAggressive, Mob::setAggressive
        );

        public final AttributeType<Mob, Boolean> AWARE = register(
                "mob:aware", Mob.class, boolean.class, Mob::isAware, Mob::setAware
        );

        public final AttributeType<Mob, Boolean> LEFT_HANDED = register(
                "mob:left_handed", Mob.class, boolean.class, Mob::isLeftHanded, Mob::setLeftHanded
        );
    }

    public static class SittableAttributes {
        public final AttributeType<Sittable, Boolean> SITTING = register(
                "sittable:sitting", Sittable.class, boolean.class,
                Sittable::isSitting, Sittable::setSitting
        );
    }

    public static class SteerableAttributes {
        public final AttributeType<Steerable, Boolean> SADDLE = register(
                "steerable:saddled", Steerable.class, boolean.class,
                Steerable::hasSaddle, Steerable::setSaddle
        );
    }

    public static class TameableAttributes {
        public final AttributeType<Tameable, Boolean> TAMED = register(
                "tameable:tamed", Tameable.class, boolean.class, Tameable::isTamed, Tameable::setTamed
        );
    }

    public static <E, T> AttributeType<E, @Nullable T> registerNullable(
            @KeyPattern String key, Class<E> entityType, Class<T> dataType,
            Function<E, @Nullable T> getter, BiConsumer<E, @Nullable T> setter
    ) {
        return register(key, entityType, dataType, getter, setter);
    }

    public static <E, T> AttributeType<E, T> register(
            @KeyPattern String key, Class<E> entityType, Class<T> dataType,
            Function<E, T> getter, BiConsumer<E, T> setter
    ) {
        var attributeType = new AttributeType<>(key, entityType, dataType, getter, setter);
        if (attributeTypes.add(attributeType)) return attributeType;
        throw new IllegalStateException("Cannot register attribute types twice: " + key);
    }
}
