package net.thenextlvl.character.property;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.entity.CollarColorable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.util.TriState;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Particle;
import org.bukkit.attribute.Attributable;
import org.bukkit.block.data.BlockData;
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
import org.bukkit.entity.Pose;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.MainHand;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * All entity properties a character can cache and persist.
 * <p>
 * A property only applies to characters whose entity type is compatible with {@link EntityProperty#entityType()}.
 *
 * @see net.thenextlvl.character.Character#setProperty(EntityProperty, Object)
 * @since 0.9.0
 */
public final class EntityProperties {
    private static final Map<Key, EntityProperty<?, ?>> properties = new LinkedHashMap<>();

    /**
     * The {@code mannequin:skin_parts} property of {@link Mannequin}.
     *
     * @since 0.9.0
     */
    public static final EntityProperty<Mannequin, Integer> MANNEQUIN_SKIN_PARTS = create("mannequin:skin_parts", Mannequin.class, Integer.class);

    /**
     * The {@code mannequin:profile} property of {@link Mannequin}.
     *
     * @see Mannequin#getProfile()
     * @since 0.9.0
     */
    public static final EntityProperty<Mannequin, ResolvableProfile> MANNEQUIN_PROFILE = create("mannequin:profile", Mannequin.class, ResolvableProfile.class);

    /**
     * The {@code mannequin:immovable} property of {@link Mannequin}.
     *
     * @see Mannequin#isImmovable()
     * @since 0.9.0
     */
    public static final EntityProperty<Mannequin, Boolean> MANNEQUIN_IMMOVABLE = create("mannequin:immovable", Mannequin.class, Boolean.class);

    /**
     * The {@code mannequin:main_hand} property of {@link Mannequin}.
     *
     * @see Mannequin#getMainHand()
     * @since 0.9.0
     */
    public static final EntityProperty<Mannequin, MainHand> MANNEQUIN_MAIN_HAND = create("mannequin:main_hand", Mannequin.class, MainHand.class);

    /**
     * The {@code armor_stand:base_plate} property of {@link ArmorStand}.
     *
     * @see ArmorStand#hasBasePlate()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Boolean> ARMOR_STAND_BASE_PLATE = create("armor_stand:base_plate", ArmorStand.class, Boolean.class);

    /**
     * The {@code armor_stand:visible} property of {@link ArmorStand}.
     *
     * @see ArmorStand#isVisible()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Boolean> ARMOR_STAND_VISIBLE = create("armor_stand:visible", ArmorStand.class, Boolean.class);

    /**
     * The {@code armor_stand:arms} property of {@link ArmorStand}.
     *
     * @see ArmorStand#hasArms()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Boolean> ARMOR_STAND_ARMS = create("armor_stand:arms", ArmorStand.class, Boolean.class);

    /**
     * The {@code armor_stand:small} property of {@link ArmorStand}.
     *
     * @see ArmorStand#isSmall()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Boolean> ARMOR_STAND_SMALL = create("armor_stand:small", ArmorStand.class, Boolean.class);

    /**
     * The {@code armor_stand:marker} property of {@link ArmorStand}.
     *
     * @see ArmorStand#isMarker()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Boolean> ARMOR_STAND_MARKER = create("armor_stand:marker", ArmorStand.class, Boolean.class);

    /**
     * The {@code armor_stand:can_move} property of {@link ArmorStand}.
     *
     * @see ArmorStand#canMove()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Boolean> ARMOR_STAND_CAN_MOVE = create("armor_stand:can_move", ArmorStand.class, Boolean.class);

    /**
     * The {@code armor_stand:can_tick} property of {@link ArmorStand}.
     *
     * @see ArmorStand#canTick()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Boolean> ARMOR_STAND_CAN_TICK = create("armor_stand:can_tick", ArmorStand.class, Boolean.class);

    /**
     * The {@code ageable:age} property of {@link Ageable}.
     *
     * @see Ageable#getAge()
     * @since 0.9.0
     */
    public static final EntityProperty<Ageable, Integer> AGEABLE_AGE = create("ageable:age", Ageable.class, Integer.class);

    /**
     * The {@code area_effect_cloud:base_potion_type} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getBasePotionType()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, PotionType> AREA_EFFECT_CLOUD_BASE_POTION_TYPE = create("area_effect_cloud:base_potion_type", AreaEffectCloud.class, PotionType.class);

    /**
     * The {@code area_effect_cloud:color} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getColor()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, Color> AREA_EFFECT_CLOUD_COLOR = create("area_effect_cloud:color", AreaEffectCloud.class, Color.class);

    /**
     * The {@code area_effect_cloud:duration_on_use} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getDurationOnUse()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, Integer> AREA_EFFECT_CLOUD_DURATION_ON_USE = create("area_effect_cloud:duration_on_use", AreaEffectCloud.class, Integer.class);

    /**
     * The {@code area_effect_cloud:duration} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getDuration()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, Integer> AREA_EFFECT_CLOUD_DURATION = create("area_effect_cloud:duration", AreaEffectCloud.class, Integer.class);

    /**
     * The {@code area_effect_cloud:particle} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getParticle()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, Particle> AREA_EFFECT_CLOUD_PARTICLE = create("area_effect_cloud:particle", AreaEffectCloud.class, Particle.class);

    /**
     * The {@code area_effect_cloud:radius} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getRadius()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, Float> AREA_EFFECT_CLOUD_RADIUS = create("area_effect_cloud:radius", AreaEffectCloud.class, Float.class);

    /**
     * The {@code area_effect_cloud:reapplication_delay} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getReapplicationDelay()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, Integer> AREA_EFFECT_CLOUD_REAPPLICATION_DELAY = create("area_effect_cloud:reapplication_delay", AreaEffectCloud.class, Integer.class);

    /**
     * The {@code area_effect_cloud:wait_time} property of {@link AreaEffectCloud}.
     *
     * @see AreaEffectCloud#getWaitTime()
     * @since 0.9.0
     */
    public static final EntityProperty<AreaEffectCloud, Integer> AREA_EFFECT_CLOUD_WAIT_TIME = create("area_effect_cloud:wait_time", AreaEffectCloud.class, Integer.class);

    /**
     * The {@code arrow:color} property of {@link Arrow}.
     *
     * @see Arrow#getColor()
     * @since 0.9.0
     */
    public static final EntityProperty<Arrow, Color> ARROW_COLOR = create("arrow:color", Arrow.class, Color.class);

    /**
     * The {@code attributable:attributes} property of {@link Attributable}.
     *
     * @see Attributable#getAttribute(org.bukkit.attribute.Attribute)
     * @since 0.9.0
     */
    public static final EntityProperty<Attributable, AttributesSnapshot> ATTRIBUTABLE_ATTRIBUTES = create("attributable:attributes", Attributable.class, AttributesSnapshot.class);

    /**
     * The {@code cat:head_up} property of {@link Cat}.
     *
     * @see Cat#isHeadUp()
     * @since 0.9.0
     */
    public static final EntityProperty<Cat, Boolean> CAT_HEAD_UP = create("cat:head_up", Cat.class, Boolean.class);

    /**
     * The {@code cat:lying_down} property of {@link Cat}.
     *
     * @see Cat#isLyingDown()
     * @since 0.9.0
     */
    public static final EntityProperty<Cat, Boolean> CAT_LYING_DOWN = create("cat:lying_down", Cat.class, Boolean.class);

    /**
     * The {@code cat:variant} property of {@link Cat}.
     *
     * @see Cat#getCatType()
     * @since 0.9.0
     */
    public static final EntityProperty<Cat, Cat.Type> CAT_VARIANT = create("cat:variant", Cat.class, Cat.Type.class);

    /**
     * The {@code allay:dancing} property of {@link Allay}.
     *
     * @see Allay#isDancing()
     * @since 0.9.0
     */
    public static final EntityProperty<Allay, Boolean> ALLAY_DANCING = create("allay:dancing", Allay.class, Boolean.class);

    /**
     * The {@code allay:can_duplicate} property of {@link Allay}.
     *
     * @see Allay#canDuplicate()
     * @since 0.9.0
     */
    public static final EntityProperty<Allay, Boolean> ALLAY_CAN_DUPLICATE = create("allay:can_duplicate", Allay.class, Boolean.class);

    /**
     * The {@code allay:duplication_cooldown} property of {@link Allay}.
     *
     * @see Allay#getDuplicationCooldown()
     * @since 0.9.0
     */
    public static final EntityProperty<Allay, Long> ALLAY_DUPLICATION_COOLDOWN = create("allay:duplication_cooldown", Allay.class, Long.class);

    /**
     * The {@code collar_colorable:collar_color} property of {@link CollarColorable}.
     *
     * @see CollarColorable#getCollarColor()
     * @since 0.9.0
     */
    public static final EntityProperty<CollarColorable, DyeColor> COLLAR_COLORABLE_COLLAR_COLOR = create("collar_colorable:collar_color", CollarColorable.class, DyeColor.class);

    /**
     * The {@code creeper:powered} property of {@link Creeper}.
     *
     * @see Creeper#isPowered()
     * @since 0.9.0
     */
    public static final EntityProperty<Creeper, Boolean> CREEPER_POWERED = create("creeper:powered", Creeper.class, Boolean.class);

    /**
     * The {@code creeper:max_fuse_ticks} property of {@link Creeper}.
     *
     * @see Creeper#getMaxFuseTicks()
     * @since 0.9.0
     */
    public static final EntityProperty<Creeper, Integer> CREEPER_MAX_FUSE_TICKS = create("creeper:max_fuse_ticks", Creeper.class, Integer.class);

    /**
     * The {@code creeper:explosion_radius} property of {@link Creeper}.
     *
     * @see Creeper#getExplosionRadius()
     * @since 0.9.0
     */
    public static final EntityProperty<Creeper, Integer> CREEPER_EXPLOSION_RADIUS = create("creeper:explosion_radius", Creeper.class, Integer.class);

    /**
     * The {@code damageable:absorption_amount} property of {@link Damageable}.
     *
     * @see Damageable#getAbsorptionAmount()
     * @since 0.9.0
     */
    public static final EntityProperty<Damageable, Double> DAMAGEABLE_ABSORPTION_AMOUNT = create("damageable:absorption_amount", Damageable.class, Double.class);

    /**
     * The {@code enderman:screaming} property of {@link Enderman}.
     *
     * @see Enderman#isScreaming()
     * @since 0.9.0
     */
    public static final EntityProperty<Enderman, Boolean> ENDERMAN_SCREAMING = create("enderman:screaming", Enderman.class, Boolean.class);

    /**
     * The {@code enderman:stared_at} property of {@link Enderman}.
     *
     * @see Enderman#hasBeenStaredAt()
     * @since 0.9.0
     */
    public static final EntityProperty<Enderman, Boolean> ENDERMAN_STARED_AT = create("enderman:stared_at", Enderman.class, Boolean.class);

    /**
     * The {@code enderman:carried_block} property of {@link Enderman}.
     *
     * @see Enderman#getCarriedBlock()
     * @since 0.9.0
     */
    public static final EntityProperty<Enderman, BlockData> ENDERMAN_CARRIED_BLOCK = create("enderman:carried_block", Enderman.class, BlockData.class);

    /**
     * The {@code entity:visual_fire} property of {@link Entity}.
     *
     * @see Entity#getVisualFire()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, TriState> ENTITY_VISUAL_FIRE = create("entity:visual_fire", Entity.class, TriState.class);

    /**
     * The {@code entity:fire_ticks} property of {@link Entity}.
     *
     * @see Entity#getFireTicks()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Integer> ENTITY_FIRE_TICKS = create("entity:fire_ticks", Entity.class, Integer.class);

    /**
     * The {@code entity:glowing} property of {@link Entity}.
     *
     * @see Entity#isGlowing()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_GLOWING = create("entity:glowing", Entity.class, Boolean.class);

    /**
     * The {@code entity:gravity} property of {@link Entity}.
     *
     * @see Entity#hasGravity()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_GRAVITY = create("entity:gravity", Entity.class, Boolean.class);

    /**
     * The {@code entity:invisible} property of {@link Entity}.
     *
     * @see Entity#isInvisible()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_INVISIBLE = create("entity:invisible", Entity.class, Boolean.class);

    /**
     * The {@code entity:invulnerable} property of {@link Entity}.
     *
     * @see Entity#isInvulnerable()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_INVULNERABLE = create("entity:invulnerable", Entity.class, Boolean.class);

    /**
     * The {@code entity:no_physics} property of {@link Entity}.
     *
     * @see Entity#hasNoPhysics()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_NO_PHYSICS = create("entity:no_physics", Entity.class, Boolean.class);

    /**
     * The {@code entity:pose} property of {@link Entity}.
     *
     * @see Entity#getPose()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Pose> ENTITY_POSE = create("entity:pose", Entity.class, Pose.class);

    /**
     * The {@code entity:freeze_ticks} property of {@link Entity}.
     *
     * @see Entity#getFreezeTicks()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Integer> ENTITY_FREEZE_TICKS = create("entity:freeze_ticks", Entity.class, Integer.class);

    /**
     * The {@code entity:lock_freeze_ticks} property of {@link Entity}.
     *
     * @see Entity#isFreezeTickingLocked()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_LOCK_FREEZE_TICKS = create("entity:lock_freeze_ticks", Entity.class, Boolean.class);

    /**
     * The {@code entity:silent} property of {@link Entity}.
     *
     * @see Entity#isSilent()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_SILENT = create("entity:silent", Entity.class, Boolean.class);

    /**
     * The {@code entity:sneaking} property of {@link Entity}.
     *
     * @see Entity#isSneaking()
     * @since 0.9.0
     */
    public static final EntityProperty<Entity, Boolean> ENTITY_SNEAKING = create("entity:sneaking", Entity.class, Boolean.class);

    /**
     * The {@code fox:crouching} property of {@link Fox}.
     *
     * @see Fox#isCrouching()
     * @since 0.9.0
     */
    public static final EntityProperty<Fox, Boolean> FOX_CROUCHING = create("fox:crouching", Fox.class, Boolean.class);

    /**
     * The {@code fox:leaping} property of {@link Fox}.
     *
     * @see Fox#isLeaping()
     * @since 0.9.0
     */
    public static final EntityProperty<Fox, Boolean> FOX_LEAPING = create("fox:leaping", Fox.class, Boolean.class);

    /**
     * The {@code fox:sleeping} property of {@link Fox}.
     *
     * @see Fox#isSleeping()
     * @since 0.9.0
     */
    public static final EntityProperty<Fox, Boolean> FOX_SLEEPING = create("fox:sleeping", Fox.class, Boolean.class);

    /**
     * The {@code fox:variant} property of {@link Fox}.
     *
     * @see Fox#getFoxType()
     * @since 0.9.0
     */
    public static final EntityProperty<Fox, Fox.Type> FOX_VARIANT = create("fox:variant", Fox.class, Fox.Type.class);

    /**
     * The {@code living_entity:gliding} property of {@link LivingEntity}.
     *
     * @see LivingEntity#isGliding()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, Boolean> LIVING_ENTITY_GLIDING = create("living_entity:gliding", LivingEntity.class, Boolean.class);

    /**
     * The {@code living_entity:equipment} property of {@link LivingEntity}.
     *
     * @see LivingEntity#getEquipment()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, EquipmentSnapshot> LIVING_ENTITY_EQUIPMENT = create("living_entity:equipment", LivingEntity.class, EquipmentSnapshot.class);

    /**
     * The {@code living_entity:ai} property of {@link LivingEntity}.
     *
     * @see LivingEntity#hasAI()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, Boolean> LIVING_ENTITY_AI = create("living_entity:ai", LivingEntity.class, Boolean.class);

    /**
     * The {@code living_entity:arrows_in_body} property of {@link LivingEntity}.
     *
     * @see LivingEntity#getArrowsInBody()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, Integer> LIVING_ENTITY_ARROWS_IN_BODY = create("living_entity:arrows_in_body", LivingEntity.class, Integer.class);

    /**
     * The {@code living_entity:bee_stingers_in_body} property of {@link LivingEntity}.
     *
     * @see LivingEntity#getBeeStingersInBody()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, Integer> LIVING_ENTITY_BEE_STINGERS_IN_BODY = create("living_entity:bee_stingers_in_body", LivingEntity.class, Integer.class);

    /**
     * The {@code living_entity:body_yaw} property of {@link LivingEntity}.
     *
     * @see LivingEntity#getBodyYaw()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, Float> LIVING_ENTITY_BODY_YAW = create("living_entity:body_yaw", LivingEntity.class, Float.class);

    /**
     * The {@code living_entity:collidable} property of {@link LivingEntity}.
     *
     * @see LivingEntity#isCollidable()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, Boolean> LIVING_ENTITY_COLLIDABLE = create("living_entity:collidable", LivingEntity.class, Boolean.class);

    /**
     * The {@code mob:aggressive} property of {@link Mob}.
     *
     * @see Mob#isAggressive()
     * @since 0.9.0
     */
    public static final EntityProperty<Mob, Boolean> MOB_AGGRESSIVE = create("mob:aggressive", Mob.class, Boolean.class);

    /**
     * The {@code mob:aware} property of {@link Mob}.
     *
     * @see Mob#isAware()
     * @since 0.9.0
     */
    public static final EntityProperty<Mob, Boolean> MOB_AWARE = create("mob:aware", Mob.class, Boolean.class);

    /**
     * The {@code mob:left_handed} property of {@link Mob}.
     *
     * @see Mob#isLeftHanded()
     * @since 0.9.0
     */
    public static final EntityProperty<Mob, Boolean> MOB_LEFT_HANDED = create("mob:left_handed", Mob.class, Boolean.class);

    /**
     * The {@code sittable:sitting} property of {@link Sittable}.
     *
     * @see Sittable#isSitting()
     * @since 0.9.0
     */
    public static final EntityProperty<Sittable, Boolean> SITTABLE_SITTING = create("sittable:sitting", Sittable.class, Boolean.class);

    /**
     * The {@code steerable:saddled} property of {@link Steerable}.
     *
     * @see Steerable#hasSaddle()
     * @since 0.9.0
     */
    public static final EntityProperty<Steerable, Boolean> STEERABLE_SADDLED = create("steerable:saddled", Steerable.class, Boolean.class);

    /**
     * The {@code tameable:tamed} property of {@link Tameable}.
     *
     * @see Tameable#isTamed()
     * @since 0.9.0
     */
    public static final EntityProperty<Tameable, Boolean> TAMEABLE_TAMED = create("tameable:tamed", Tameable.class, Boolean.class);

    private EntityProperties() {
    }

    /**
     * Returns all entity properties.
     *
     * @return all entity properties
     * @since 0.9.0
     */
    @Unmodifiable
    @Contract(pure = true)
    public static List<EntityProperty<?, ?>> values() {
        return List.copyOf(properties.values());
    }

    /**
     * Returns the entity property with the given key.
     *
     * @param key the key of the property
     * @return the entity property with the given key
     * @since 0.9.0
     */
    @Contract(pure = true)
    public static Optional<EntityProperty<?, ?>> getByKey(final Key key) {
        return Optional.ofNullable(properties.get(key));
    }

    private static <E, T> EntityProperty<E, T> create(@KeyPattern final String key, final Class<E> entityType, final Class<T> valueType) {
        final var property = new EntityProperty<>(Key.key(key), entityType, valueType);
        properties.put(property.key(), property);
        return property;
    }
}
