package net.thenextlvl.character.plugin.property;

import com.destroystokyo.paper.PaperSkinParts;
import com.google.common.base.Preconditions;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import core.paper.brigadier.arguments.EnumArgumentType;
import core.paper.brigadier.arguments.codecs.EnumStringCodec;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.entity.CollarColorable;
import io.papermc.paper.registry.RegistryKey;
import net.thenextlvl.character.plugin.command.argument.BlockDataArgumentType;
import net.thenextlvl.character.plugin.command.argument.ColorArgumentType;
import net.thenextlvl.character.property.EntityProperties;
import net.thenextlvl.character.property.EntityProperty;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Allay;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Tameable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public final class PropertyAccessors {
    public static final List<PropertyAccessor<?, ?>> ALL = List.<PropertyAccessor<?, ?>>of(
            integer(EntityProperties.MANNEQUIN_SKIN_PARTS, mannequin -> mannequin.getSkinParts().getRaw(), (mannequin, raw) -> mannequin.setSkinParts(new PaperSkinParts(raw))),
            property(EntityProperties.MANNEQUIN_PROFILE, Mannequin::getProfile, Mannequin::setProfile, null),
            bool(EntityProperties.MANNEQUIN_IMMOVABLE, Mannequin::isImmovable, Mannequin::setImmovable),
            enumeration(EntityProperties.MANNEQUIN_MAIN_HAND, Mannequin::getMainHand, Mannequin::setMainHand),

            bool(EntityProperties.ARMOR_STAND_BASE_PLATE, ArmorStand::hasBasePlate, ArmorStand::setBasePlate),
            bool(EntityProperties.ARMOR_STAND_VISIBLE, ArmorStand::isVisible, ArmorStand::setVisible),
            bool(EntityProperties.ARMOR_STAND_ARMS, ArmorStand::hasArms, ArmorStand::setArms),
            bool(EntityProperties.ARMOR_STAND_SMALL, ArmorStand::isSmall, ArmorStand::setSmall),
            bool(EntityProperties.ARMOR_STAND_MARKER, ArmorStand::isMarker, ArmorStand::setMarker),
            bool(EntityProperties.ARMOR_STAND_CAN_MOVE, ArmorStand::canMove, ArmorStand::setCanMove),
            bool(EntityProperties.ARMOR_STAND_CAN_TICK, ArmorStand::canTick, ArmorStand::setCanTick),

            integer(EntityProperties.AGEABLE_AGE, Ageable::getAge, Ageable::setAge),

            enumeration(EntityProperties.AREA_EFFECT_CLOUD_BASE_POTION_TYPE, AreaEffectCloud::getBasePotionType, AreaEffectCloud::setBasePotionType),
            property(EntityProperties.AREA_EFFECT_CLOUD_COLOR, AreaEffectCloud::getColor, AreaEffectCloud::setColor, new ColorArgumentType()),
            integer(EntityProperties.AREA_EFFECT_CLOUD_DURATION_ON_USE, AreaEffectCloud::getDurationOnUse, AreaEffectCloud::setDurationOnUse),
            integer(EntityProperties.AREA_EFFECT_CLOUD_DURATION, AreaEffectCloud::getDuration, AreaEffectCloud::setDuration),
            enumeration(EntityProperties.AREA_EFFECT_CLOUD_PARTICLE, AreaEffectCloud::getParticle, (cloud, particle) -> cloud.setParticle(particle)),
            floating(EntityProperties.AREA_EFFECT_CLOUD_RADIUS, AreaEffectCloud::getRadius, AreaEffectCloud::setRadius),
            property(EntityProperties.AREA_EFFECT_CLOUD_REAPPLICATION_DELAY, AreaEffectCloud::getReapplicationDelay, AreaEffectCloud::setReapplicationDelay, ArgumentTypes.time()),
            property(EntityProperties.AREA_EFFECT_CLOUD_WAIT_TIME, AreaEffectCloud::getWaitTime, AreaEffectCloud::setWaitTime, ArgumentTypes.time()),

            property(EntityProperties.ARROW_COLOR, Arrow::getColor, Arrow::setColor, new ColorArgumentType()),

            property(EntityProperties.ATTRIBUTABLE_ATTRIBUTES, Snapshots::attributes, Snapshots::applyAttributes, null),

            bool(EntityProperties.CAT_HEAD_UP, Cat::isHeadUp, Cat::setHeadUp),
            bool(EntityProperties.CAT_LYING_DOWN, Cat::isLyingDown, Cat::setLyingDown),
            property(EntityProperties.CAT_VARIANT, Cat::getCatType, Cat::setCatType, ArgumentTypes.resource(RegistryKey.CAT_VARIANT)),

            bool(EntityProperties.ALLAY_DANCING, Allay::isDancing, (allay, dancing) -> { if (dancing) allay.startDancing(); else allay.stopDancing(); }),
            bool(EntityProperties.ALLAY_CAN_DUPLICATE, Allay::canDuplicate, Allay::setCanDuplicate),
            longInteger(EntityProperties.ALLAY_DUPLICATION_COOLDOWN, Allay::getDuplicationCooldown, Allay::setDuplicationCooldown),

            enumeration(EntityProperties.COLLAR_COLORABLE_COLLAR_COLOR, CollarColorable::getCollarColor, CollarColorable::setCollarColor),

            bool(EntityProperties.CREEPER_POWERED, Creeper::isPowered, Creeper::setPowered),
            integer(EntityProperties.CREEPER_MAX_FUSE_TICKS, Creeper::getMaxFuseTicks, Creeper::setMaxFuseTicks),
            integer(EntityProperties.CREEPER_EXPLOSION_RADIUS, Creeper::getExplosionRadius, Creeper::setExplosionRadius),

            doubleFloating(EntityProperties.DAMAGEABLE_ABSORPTION_AMOUNT, Damageable::getAbsorptionAmount, Damageable::setAbsorptionAmount),

            bool(EntityProperties.ENDERMAN_SCREAMING, Enderman::isScreaming, Enderman::setScreaming),
            bool(EntityProperties.ENDERMAN_STARED_AT, Enderman::hasBeenStaredAt, Enderman::setHasBeenStaredAt),
            property(EntityProperties.ENDERMAN_CARRIED_BLOCK, Enderman::getCarriedBlock, Enderman::setCarriedBlock, new BlockDataArgumentType()),

            enumeration(EntityProperties.ENTITY_VISUAL_FIRE, Entity::getVisualFire, (entity, visualFire) -> entity.setVisualFire(visualFire)),
            integer(EntityProperties.ENTITY_FIRE_TICKS, Entity::getFireTicks, Entity::setFireTicks),
            bool(EntityProperties.ENTITY_GLOWING, Entity::isGlowing, Entity::setGlowing),
            bool(EntityProperties.ENTITY_GRAVITY, Entity::hasGravity, Entity::setGravity),
            bool(EntityProperties.ENTITY_INVISIBLE, Entity::isInvisible, Entity::setInvisible),
            bool(EntityProperties.ENTITY_INVULNERABLE, Entity::isInvulnerable, Entity::setInvulnerable),
            bool(EntityProperties.ENTITY_NO_PHYSICS, Entity::hasNoPhysics, Entity::setNoPhysics),
            enumeration(EntityProperties.ENTITY_POSE, Entity::getPose, (entity, pose) -> entity.setPose(pose, true)),
            integer(EntityProperties.ENTITY_FREEZE_TICKS, Entity::getFreezeTicks, Entity::setFreezeTicks),
            bool(EntityProperties.ENTITY_LOCK_FREEZE_TICKS, Entity::isFreezeTickingLocked, Entity::lockFreezeTicks),
            bool(EntityProperties.ENTITY_SILENT, Entity::isSilent, Entity::setSilent),
            bool(EntityProperties.ENTITY_SNEAKING, Entity::isSneaking, Entity::setSneaking),

            bool(EntityProperties.FOX_CROUCHING, Fox::isCrouching, Fox::setCrouching),
            bool(EntityProperties.FOX_LEAPING, Fox::isLeaping, Fox::setLeaping),
            bool(EntityProperties.FOX_SLEEPING, Fox::isSleeping, Fox::setSleeping),
            enumeration(EntityProperties.FOX_VARIANT, Fox::getFoxType, Fox::setFoxType),

            bool(EntityProperties.LIVING_ENTITY_GLIDING, LivingEntity::isGliding, LivingEntity::setGliding),
            property(EntityProperties.LIVING_ENTITY_EQUIPMENT, Snapshots::equipment, Snapshots::applyEquipment, null),
            bool(EntityProperties.LIVING_ENTITY_AI, LivingEntity::hasAI, LivingEntity::setAI),
            integer(EntityProperties.LIVING_ENTITY_ARROWS_IN_BODY, LivingEntity::getArrowsInBody, (entity, arrows) -> entity.setArrowsInBody(arrows, false)),
            integer(EntityProperties.LIVING_ENTITY_BEE_STINGERS_IN_BODY, LivingEntity::getBeeStingersInBody, LivingEntity::setBeeStingersInBody),
            floating(EntityProperties.LIVING_ENTITY_BODY_YAW, LivingEntity::getBodyYaw, LivingEntity::setBodyYaw),
            // todo: can we persist the health without issues?
            // doubleFloating("living_entity:health", LivingEntity.class, LivingEntity::getHealth, LivingEntity::setHealth),
            bool(EntityProperties.LIVING_ENTITY_COLLIDABLE, LivingEntity::isCollidable, LivingEntity::setCollidable),

            bool(EntityProperties.MOB_AGGRESSIVE, Mob::isAggressive, Mob::setAggressive),
            bool(EntityProperties.MOB_AWARE, Mob::isAware, Mob::setAware),
            bool(EntityProperties.MOB_LEFT_HANDED, Mob::isLeftHanded, Mob::setLeftHanded),

            bool(EntityProperties.SITTABLE_SITTING, Sittable::isSitting, Sittable::setSitting),

            bool(EntityProperties.STEERABLE_SADDLED, Steerable::hasSaddle, Steerable::setSaddle),

            bool(EntityProperties.TAMEABLE_TAMED, Tameable::isTamed, Tameable::setTamed)
    );

    private static final Map<EntityProperty<?, ?>, PropertyAccessor<?, ?>> accessors = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(PropertyAccessor::property, accessor -> accessor));

    static {
        EntityProperties.values().forEach(property -> Preconditions.checkState(accessors.containsKey(property),
                "Missing accessor for entity property %s", property.key()));
    }

    private PropertyAccessors() {
    }

    @SuppressWarnings("unchecked")
    public static <E, T> Optional<PropertyAccessor<E, T>> of(final EntityProperty<E, T> property) {
        return Optional.ofNullable((PropertyAccessor<E, T>) accessors.get(property));
    }

    private static <E, T> PropertyAccessor<E, T> property(
            final EntityProperty<E, T> property,
            final Function<E, @Nullable T> getter, final BiConsumer<E, @Nullable T> setter,
            @Nullable final ArgumentType<T> argumentType
    ) {
        return new PropertyAccessor<>(property, getter, setter, argumentType);
    }

    private static <E> PropertyAccessor<E, Boolean> bool(final EntityProperty<E, Boolean> property, final Function<E, Boolean> getter, final BiConsumer<E, Boolean> setter) {
        return property(property, getter, setter, BoolArgumentType.bool());
    }

    private static <E> PropertyAccessor<E, Integer> integer(final EntityProperty<E, Integer> property, final Function<E, Integer> getter, final BiConsumer<E, Integer> setter) {
        return property(property, getter, setter, IntegerArgumentType.integer());
    }

    private static <E> PropertyAccessor<E, Long> longInteger(final EntityProperty<E, Long> property, final Function<E, Long> getter, final BiConsumer<E, Long> setter) {
        return property(property, getter, setter, LongArgumentType.longArg());
    }

    private static <E> PropertyAccessor<E, Float> floating(final EntityProperty<E, Float> property, final Function<E, Float> getter, final BiConsumer<E, Float> setter) {
        return property(property, getter, setter, FloatArgumentType.floatArg());
    }

    private static <E> PropertyAccessor<E, Double> doubleFloating(final EntityProperty<E, Double> property, final Function<E, Double> getter, final BiConsumer<E, Double> setter) {
        return property(property, getter, setter, DoubleArgumentType.doubleArg());
    }

    private static <E, T extends Enum<T>> PropertyAccessor<E, T> enumeration(final EntityProperty<E, T> property, final Function<E, T> getter, final BiConsumer<E, T> setter) {
        return property(property, getter, setter, EnumArgumentType.of(property.valueType(), EnumStringCodec.lowerHyphen()));
    }
}
