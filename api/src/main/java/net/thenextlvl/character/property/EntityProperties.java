package net.thenextlvl.character.property;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.entity.CollarColorable;
import io.papermc.paper.math.Rotations;
import io.papermc.paper.world.WeatheringCopperState;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.Art;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Rotation;
import org.bukkit.attribute.Attributable;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractCubeMob;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Allay;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Cat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Display;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mob;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Pose;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.SizedFireball;
import org.bukkit.entity.Sniffer;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tadpole;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.entity.Trident;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Transformation;
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
     * <p>
     * Unless this property is set, a character is silent while it has no goals
     * and makes the sounds of its kind, like footsteps and ambient sounds, while it has any.
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

    /**
     * The {@code abstract_arrow:critical} property of {@link AbstractArrow}.
     *
     * @see AbstractArrow#isCritical()
     * @since 0.9.0
     */
    public static final EntityProperty<AbstractArrow, Boolean> ABSTRACT_ARROW_CRITICAL = create("abstract_arrow:critical", AbstractArrow.class, Boolean.class);

    /**
     * The {@code abstract_cube_mob:size} property of {@link AbstractCubeMob}.
     *
     * @see AbstractCubeMob#getSize()
     * @since 0.9.0
     */
    public static final EntityProperty<AbstractCubeMob, Integer> ABSTRACT_CUBE_MOB_SIZE = create("abstract_cube_mob:size", AbstractCubeMob.class, Integer.class);

    /**
     * The {@code abstract_horse:eating} property of {@link AbstractHorse}.
     *
     * @see AbstractHorse#isEating()
     * @since 0.9.0
     */
    public static final EntityProperty<AbstractHorse, Boolean> ABSTRACT_HORSE_EATING = create("abstract_horse:eating", AbstractHorse.class, Boolean.class);

    /**
     * The {@code abstract_horse:eating_grass} property of {@link AbstractHorse}.
     *
     * @see AbstractHorse#isEatingGrass()
     * @since 0.9.0
     */
    public static final EntityProperty<AbstractHorse, Boolean> ABSTRACT_HORSE_EATING_GRASS = create("abstract_horse:eating_grass", AbstractHorse.class, Boolean.class);

    /**
     * The {@code abstract_horse:rearing} property of {@link AbstractHorse}.
     *
     * @see AbstractHorse#isRearing()
     * @since 0.9.0
     */
    public static final EntityProperty<AbstractHorse, Boolean> ABSTRACT_HORSE_REARING = create("abstract_horse:rearing", AbstractHorse.class, Boolean.class);

    /**
     * The {@code abstract_skeleton:should_burn_in_day} property of {@link AbstractSkeleton}.
     *
     * @see AbstractSkeleton#shouldBurnInDay()
     * @since 0.9.0
     */
    public static final EntityProperty<AbstractSkeleton, Boolean> ABSTRACT_SKELETON_SHOULD_BURN_IN_DAY = create("abstract_skeleton:should_burn_in_day", AbstractSkeleton.class, Boolean.class);

    /**
     * The {@code ageable:age_lock} property of {@link Ageable}.
     *
     * @see Ageable#getAgeLock()
     * @since 0.9.0
     */
    public static final EntityProperty<Ageable, Boolean> AGEABLE_AGE_LOCK = create("ageable:age_lock", Ageable.class, Boolean.class);

    /**
     * The {@code armor_stand:body_rotations} property of {@link ArmorStand}.
     *
     * @see ArmorStand#getBodyRotations()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Rotations> ARMOR_STAND_BODY_ROTATIONS = create("armor_stand:body_rotations", ArmorStand.class, Rotations.class);

    /**
     * The {@code armor_stand:head_rotations} property of {@link ArmorStand}.
     *
     * @see ArmorStand#getHeadRotations()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Rotations> ARMOR_STAND_HEAD_ROTATIONS = create("armor_stand:head_rotations", ArmorStand.class, Rotations.class);

    /**
     * The {@code armor_stand:left_arm_rotations} property of {@link ArmorStand}.
     *
     * @see ArmorStand#getLeftArmRotations()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Rotations> ARMOR_STAND_LEFT_ARM_ROTATIONS = create("armor_stand:left_arm_rotations", ArmorStand.class, Rotations.class);

    /**
     * The {@code armor_stand:left_leg_rotations} property of {@link ArmorStand}.
     *
     * @see ArmorStand#getLeftLegRotations()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Rotations> ARMOR_STAND_LEFT_LEG_ROTATIONS = create("armor_stand:left_leg_rotations", ArmorStand.class, Rotations.class);

    /**
     * The {@code armor_stand:right_arm_rotations} property of {@link ArmorStand}.
     *
     * @see ArmorStand#getRightArmRotations()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Rotations> ARMOR_STAND_RIGHT_ARM_ROTATIONS = create("armor_stand:right_arm_rotations", ArmorStand.class, Rotations.class);

    /**
     * The {@code armor_stand:right_leg_rotations} property of {@link ArmorStand}.
     *
     * @see ArmorStand#getRightLegRotations()
     * @since 0.9.0
     */
    public static final EntityProperty<ArmorStand, Rotations> ARMOR_STAND_RIGHT_LEG_ROTATIONS = create("armor_stand:right_leg_rotations", ArmorStand.class, Rotations.class);

    /**
     * The {@code arrow:base_potion_type} property of {@link Arrow}.
     *
     * @see Arrow#getBasePotionType()
     * @since 0.9.0
     */
    public static final EntityProperty<Arrow, PotionType> ARROW_BASE_POTION_TYPE = create("arrow:base_potion_type", Arrow.class, PotionType.class);

    /**
     * The {@code axolotl:playing_dead} property of {@link Axolotl}.
     *
     * @see Axolotl#isPlayingDead()
     * @since 0.9.0
     */
    public static final EntityProperty<Axolotl, Boolean> AXOLOTL_PLAYING_DEAD = create("axolotl:playing_dead", Axolotl.class, Boolean.class);

    /**
     * The {@code axolotl:variant} property of {@link Axolotl}.
     *
     * @see Axolotl#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Axolotl, Axolotl.Variant> AXOLOTL_VARIANT = create("axolotl:variant", Axolotl.class, Axolotl.Variant.class);

    /**
     * The {@code bat:awake} property of {@link Bat}.
     *
     * @see Bat#isAwake()
     * @since 0.9.0
     */
    public static final EntityProperty<Bat, Boolean> BAT_AWAKE = create("bat:awake", Bat.class, Boolean.class);

    /**
     * The {@code bee:has_nectar} property of {@link Bee}.
     *
     * @see Bee#hasNectar()
     * @since 0.9.0
     */
    public static final EntityProperty<Bee, Boolean> BEE_HAS_NECTAR = create("bee:has_nectar", Bee.class, Boolean.class);

    /**
     * The {@code bee:has_stung} property of {@link Bee}.
     *
     * @see Bee#hasStung()
     * @since 0.9.0
     */
    public static final EntityProperty<Bee, Boolean> BEE_HAS_STUNG = create("bee:has_stung", Bee.class, Boolean.class);

    /**
     * The {@code bee:rolling_override} property of {@link Bee}.
     *
     * @see Bee#getRollingOverride()
     * @since 0.9.0
     */
    public static final EntityProperty<Bee, TriState> BEE_ROLLING_OVERRIDE = create("bee:rolling_override", Bee.class, TriState.class);

    /**
     * The {@code block_display:block} property of {@link BlockDisplay}.
     *
     * @see BlockDisplay#getBlock()
     * @since 0.9.0
     */
    public static final EntityProperty<BlockDisplay, BlockData> BLOCK_DISPLAY_BLOCK = create("block_display:block", BlockDisplay.class, BlockData.class);

    /**
     * The {@code bogged:sheared} property of {@link Bogged}.
     *
     * @see Bogged#isSheared()
     * @since 0.9.0
     */
    public static final EntityProperty<Bogged, Boolean> BOGGED_SHEARED = create("bogged:sheared", Bogged.class, Boolean.class);

    /**
     * The {@code camel:dashing} property of {@link Camel}.
     *
     * @see Camel#isDashing()
     * @since 0.9.0
     */
    public static final EntityProperty<Camel, Boolean> CAMEL_DASHING = create("camel:dashing", Camel.class, Boolean.class);

    /**
     * The {@code cat:sound_variant} property of {@link Cat}.
     *
     * @see Cat#getSoundVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Cat, Cat.SoundVariant> CAT_SOUND_VARIANT = create("cat:sound_variant", Cat.class, Cat.SoundVariant.class);

    /**
     * The {@code chested_horse:carrying_chest} property of {@link ChestedHorse}.
     *
     * @see ChestedHorse#isCarryingChest()
     * @since 0.9.0
     */
    public static final EntityProperty<ChestedHorse, Boolean> CHESTED_HORSE_CARRYING_CHEST = create("chested_horse:carrying_chest", ChestedHorse.class, Boolean.class);

    /**
     * The {@code chicken:sound_variant} property of {@link Chicken}.
     *
     * @see Chicken#getSoundVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Chicken, Chicken.SoundVariant> CHICKEN_SOUND_VARIANT = create("chicken:sound_variant", Chicken.class, Chicken.SoundVariant.class);

    /**
     * The {@code chicken:variant} property of {@link Chicken}.
     *
     * @see Chicken#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Chicken, Chicken.Variant> CHICKEN_VARIANT = create("chicken:variant", Chicken.class, Chicken.Variant.class);

    /**
     * The {@code colorable:color} property of {@link Colorable}.
     *
     * @see Colorable#getColor()
     * @since 0.9.0
     */
    public static final EntityProperty<Colorable, DyeColor> COLORABLE_COLOR = create("colorable:color", Colorable.class, DyeColor.class);

    /**
     * The {@code copper_golem:golem_state} property of {@link CopperGolem}.
     *
     * @see CopperGolem#getGolemState()
     * @since 0.9.0
     */
    public static final EntityProperty<CopperGolem, CopperGolem.State> COPPER_GOLEM_GOLEM_STATE = create("copper_golem:golem_state", CopperGolem.class, CopperGolem.State.class);

    /**
     * The {@code copper_golem:weathering_state} property of {@link CopperGolem}.
     *
     * @see CopperGolem#getWeatheringState()
     * @since 0.9.0
     */
    public static final EntityProperty<CopperGolem, WeatheringCopperState> COPPER_GOLEM_WEATHERING_STATE = create("copper_golem:weathering_state", CopperGolem.class, WeatheringCopperState.class);

    /**
     * The {@code cow:sound_variant} property of {@link Cow}.
     *
     * @see Cow#getSoundVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Cow, Cow.SoundVariant> COW_SOUND_VARIANT = create("cow:sound_variant", Cow.class, Cow.SoundVariant.class);

    /**
     * The {@code cow:variant} property of {@link Cow}.
     *
     * @see Cow#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Cow, Cow.Variant> COW_VARIANT = create("cow:variant", Cow.class, Cow.Variant.class);

    /**
     * The {@code display:billboard} property of {@link Display}.
     *
     * @see Display#getBillboard()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Display.Billboard> DISPLAY_BILLBOARD = create("display:billboard", Display.class, Display.Billboard.class);

    /**
     * The {@code display:brightness} property of {@link Display}.
     *
     * @see Display#getBrightness()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Display.Brightness> DISPLAY_BRIGHTNESS = create("display:brightness", Display.class, Display.Brightness.class);

    /**
     * The {@code display:display_height} property of {@link Display}.
     *
     * @see Display#getDisplayHeight()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Float> DISPLAY_DISPLAY_HEIGHT = create("display:display_height", Display.class, Float.class);

    /**
     * The {@code display:display_width} property of {@link Display}.
     *
     * @see Display#getDisplayWidth()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Float> DISPLAY_DISPLAY_WIDTH = create("display:display_width", Display.class, Float.class);

    /**
     * The {@code display:glow_color_override} property of {@link Display}.
     *
     * @see Display#getGlowColorOverride()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Color> DISPLAY_GLOW_COLOR_OVERRIDE = create("display:glow_color_override", Display.class, Color.class);

    /**
     * The {@code display:shadow_radius} property of {@link Display}.
     *
     * @see Display#getShadowRadius()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Float> DISPLAY_SHADOW_RADIUS = create("display:shadow_radius", Display.class, Float.class);

    /**
     * The {@code display:shadow_strength} property of {@link Display}.
     *
     * @see Display#getShadowStrength()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Float> DISPLAY_SHADOW_STRENGTH = create("display:shadow_strength", Display.class, Float.class);

    /**
     * The {@code display:transformation} property of {@link Display}.
     *
     * @see Display#getTransformation()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Transformation> DISPLAY_TRANSFORMATION = create("display:transformation", Display.class, Transformation.class);

    /**
     * The {@code display:view_range} property of {@link Display}.
     *
     * @see Display#getViewRange()
     * @since 0.9.0
     */
    public static final EntityProperty<Display, Float> DISPLAY_VIEW_RANGE = create("display:view_range", Display.class, Float.class);

    /**
     * The {@code ender_crystal:beam_target} property of {@link EnderCrystal}.
     *
     * @see EnderCrystal#getBeamTarget()
     * @since 0.9.0
     */
    public static final EntityProperty<EnderCrystal, Location> ENDER_CRYSTAL_BEAM_TARGET = create("ender_crystal:beam_target", EnderCrystal.class, Location.class);

    /**
     * The {@code ender_crystal:showing_bottom} property of {@link EnderCrystal}.
     *
     * @see EnderCrystal#isShowingBottom()
     * @since 0.9.0
     */
    public static final EntityProperty<EnderCrystal, Boolean> ENDER_CRYSTAL_SHOWING_BOTTOM = create("ender_crystal:showing_bottom", EnderCrystal.class, Boolean.class);

    /**
     * The {@code experience_orb:experience} property of {@link ExperienceOrb}.
     *
     * @see ExperienceOrb#getExperience()
     * @since 0.9.0
     */
    public static final EntityProperty<ExperienceOrb, Integer> EXPERIENCE_ORB_EXPERIENCE = create("experience_orb:experience", ExperienceOrb.class, Integer.class);

    /**
     * The {@code falling_block:block_data} property of {@link FallingBlock}.
     *
     * @see FallingBlock#getBlockData()
     * @since 0.9.0
     */
    public static final EntityProperty<FallingBlock, BlockData> FALLING_BLOCK_BLOCK_DATA = create("falling_block:block_data", FallingBlock.class, BlockData.class);

    /**
     * The {@code fox:faceplanted} property of {@link Fox}.
     *
     * @see Fox#isFaceplanted()
     * @since 0.9.0
     */
    public static final EntityProperty<Fox, Boolean> FOX_FACEPLANTED = create("fox:faceplanted", Fox.class, Boolean.class);

    /**
     * The {@code fox:interested} property of {@link Fox}.
     *
     * @see Fox#isInterested()
     * @since 0.9.0
     */
    public static final EntityProperty<Fox, Boolean> FOX_INTERESTED = create("fox:interested", Fox.class, Boolean.class);

    /**
     * The {@code frog:variant} property of {@link Frog}.
     *
     * @see Frog#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Frog, Frog.Variant> FROG_VARIANT = create("frog:variant", Frog.class, Frog.Variant.class);

    /**
     * The {@code ghast:charging} property of {@link Ghast}.
     *
     * @see Ghast#isCharging()
     * @since 0.9.0
     */
    public static final EntityProperty<Ghast, Boolean> GHAST_CHARGING = create("ghast:charging", Ghast.class, Boolean.class);

    /**
     * The {@code goat:left_horn} property of {@link Goat}.
     *
     * @see Goat#hasLeftHorn()
     * @since 0.9.0
     */
    public static final EntityProperty<Goat, Boolean> GOAT_LEFT_HORN = create("goat:left_horn", Goat.class, Boolean.class);

    /**
     * The {@code goat:right_horn} property of {@link Goat}.
     *
     * @see Goat#hasRightHorn()
     * @since 0.9.0
     */
    public static final EntityProperty<Goat, Boolean> GOAT_RIGHT_HORN = create("goat:right_horn", Goat.class, Boolean.class);

    /**
     * The {@code goat:screaming} property of {@link Goat}.
     *
     * @see Goat#isScreaming()
     * @since 0.9.0
     */
    public static final EntityProperty<Goat, Boolean> GOAT_SCREAMING = create("goat:screaming", Goat.class, Boolean.class);

    /**
     * The {@code horse:color} property of {@link Horse}.
     *
     * @see Horse#getColor()
     * @since 0.9.0
     */
    public static final EntityProperty<Horse, Horse.Color> HORSE_COLOR = create("horse:color", Horse.class, Horse.Color.class);

    /**
     * The {@code horse:style} property of {@link Horse}.
     *
     * @see Horse#getStyle()
     * @since 0.9.0
     */
    public static final EntityProperty<Horse, Horse.Style> HORSE_STYLE = create("horse:style", Horse.class, Horse.Style.class);

    /**
     * The {@code interaction:interaction_height} property of {@link Interaction}.
     *
     * @see Interaction#getInteractionHeight()
     * @since 0.9.0
     */
    public static final EntityProperty<Interaction, Float> INTERACTION_INTERACTION_HEIGHT = create("interaction:interaction_height", Interaction.class, Float.class);

    /**
     * The {@code interaction:interaction_width} property of {@link Interaction}.
     *
     * @see Interaction#getInteractionWidth()
     * @since 0.9.0
     */
    public static final EntityProperty<Interaction, Float> INTERACTION_INTERACTION_WIDTH = create("interaction:interaction_width", Interaction.class, Float.class);

    /**
     * The {@code interaction:responsive} property of {@link Interaction}.
     *
     * @see Interaction#isResponsive()
     * @since 0.9.0
     */
    public static final EntityProperty<Interaction, Boolean> INTERACTION_RESPONSIVE = create("interaction:responsive", Interaction.class, Boolean.class);

    /**
     * The {@code item:item_stack} property of {@link Item}.
     *
     * @see Item#getItemStack()
     * @since 0.9.0
     */
    public static final EntityProperty<Item, ItemStack> ITEM_ITEM_STACK = create("item:item_stack", Item.class, ItemStack.class);

    /**
     * The {@code item_display:item_display_transform} property of {@link ItemDisplay}.
     *
     * @see ItemDisplay#getItemDisplayTransform()
     * @since 0.9.0
     */
    public static final EntityProperty<ItemDisplay, ItemDisplay.ItemDisplayTransform> ITEM_DISPLAY_ITEM_DISPLAY_TRANSFORM = create("item_display:item_display_transform", ItemDisplay.class, ItemDisplay.ItemDisplayTransform.class);

    /**
     * The {@code item_display:item_stack} property of {@link ItemDisplay}.
     *
     * @see ItemDisplay#getItemStack()
     * @since 0.9.0
     */
    public static final EntityProperty<ItemDisplay, ItemStack> ITEM_DISPLAY_ITEM_STACK = create("item_display:item_stack", ItemDisplay.class, ItemStack.class);

    /**
     * The {@code item_frame:fixed} property of {@link ItemFrame}.
     *
     * @see ItemFrame#isFixed()
     * @since 0.9.0
     */
    public static final EntityProperty<ItemFrame, Boolean> ITEM_FRAME_FIXED = create("item_frame:fixed", ItemFrame.class, Boolean.class);

    /**
     * The {@code item_frame:item} property of {@link ItemFrame}.
     *
     * @see ItemFrame#getItem()
     * @since 0.9.0
     */
    public static final EntityProperty<ItemFrame, ItemStack> ITEM_FRAME_ITEM = create("item_frame:item", ItemFrame.class, ItemStack.class);

    /**
     * The {@code item_frame:rotation} property of {@link ItemFrame}.
     *
     * @see ItemFrame#getRotation()
     * @since 0.9.0
     */
    public static final EntityProperty<ItemFrame, Rotation> ITEM_FRAME_ROTATION = create("item_frame:rotation", ItemFrame.class, Rotation.class);

    /**
     * The {@code item_frame:visible} property of {@link ItemFrame}.
     *
     * @see ItemFrame#isVisible()
     * @since 0.9.0
     */
    public static final EntityProperty<ItemFrame, Boolean> ITEM_FRAME_VISIBLE = create("item_frame:visible", ItemFrame.class, Boolean.class);

    /**
     * The {@code living_entity:riptiding} property of {@link LivingEntity}.
     *
     * @see LivingEntity#isRiptiding()
     * @since 0.9.0
     */
    public static final EntityProperty<LivingEntity, Boolean> LIVING_ENTITY_RIPTIDING = create("living_entity:riptiding", LivingEntity.class, Boolean.class);

    /**
     * The {@code llama:color} property of {@link Llama}.
     *
     * @see Llama#getColor()
     * @since 0.9.0
     */
    public static final EntityProperty<Llama, Llama.Color> LLAMA_COLOR = create("llama:color", Llama.class, Llama.Color.class);

    /**
     * The {@code mannequin:description} property of {@link Mannequin}.
     *
     * @see Mannequin#getDescription()
     * @since 0.9.0
     */
    public static final EntityProperty<Mannequin, Component> MANNEQUIN_DESCRIPTION = create("mannequin:description", Mannequin.class, Component.class);

    /**
     * The {@code minecart:display_block_data} property of {@link Minecart}.
     *
     * @see Minecart#getDisplayBlockData()
     * @since 0.9.0
     */
    public static final EntityProperty<Minecart, BlockData> MINECART_DISPLAY_BLOCK_DATA = create("minecart:display_block_data", Minecart.class, BlockData.class);

    /**
     * The {@code minecart:display_block_offset} property of {@link Minecart}.
     *
     * @see Minecart#getDisplayBlockOffset()
     * @since 0.9.0
     */
    public static final EntityProperty<Minecart, Integer> MINECART_DISPLAY_BLOCK_OFFSET = create("minecart:display_block_offset", Minecart.class, Integer.class);

    /**
     * The {@code mushroom_cow:variant} property of {@link MushroomCow}.
     *
     * @see MushroomCow#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<MushroomCow, MushroomCow.Variant> MUSHROOM_COW_VARIANT = create("mushroom_cow:variant", MushroomCow.class, MushroomCow.Variant.class);

    /**
     * The {@code painting:art} property of {@link Painting}.
     *
     * @see Painting#getArt()
     * @since 0.9.0
     */
    public static final EntityProperty<Painting, Art> PAINTING_ART = create("painting:art", Painting.class, Art.class);

    /**
     * The {@code panda:eating} property of {@link Panda}.
     *
     * @see Panda#isEating()
     * @since 0.9.0
     */
    public static final EntityProperty<Panda, Boolean> PANDA_EATING = create("panda:eating", Panda.class, Boolean.class);

    /**
     * The {@code panda:hidden_gene} property of {@link Panda}.
     *
     * @see Panda#getHiddenGene()
     * @since 0.9.0
     */
    public static final EntityProperty<Panda, Panda.Gene> PANDA_HIDDEN_GENE = create("panda:hidden_gene", Panda.class, Panda.Gene.class);

    /**
     * The {@code panda:main_gene} property of {@link Panda}.
     *
     * @see Panda#getMainGene()
     * @since 0.9.0
     */
    public static final EntityProperty<Panda, Panda.Gene> PANDA_MAIN_GENE = create("panda:main_gene", Panda.class, Panda.Gene.class);

    /**
     * The {@code panda:on_back} property of {@link Panda}.
     *
     * @see Panda#isOnBack()
     * @since 0.9.0
     */
    public static final EntityProperty<Panda, Boolean> PANDA_ON_BACK = create("panda:on_back", Panda.class, Boolean.class);

    /**
     * The {@code panda:rolling} property of {@link Panda}.
     *
     * @see Panda#isRolling()
     * @since 0.9.0
     */
    public static final EntityProperty<Panda, Boolean> PANDA_ROLLING = create("panda:rolling", Panda.class, Boolean.class);

    /**
     * The {@code panda:sneezing} property of {@link Panda}.
     *
     * @see Panda#isSneezing()
     * @since 0.9.0
     */
    public static final EntityProperty<Panda, Boolean> PANDA_SNEEZING = create("panda:sneezing", Panda.class, Boolean.class);

    /**
     * The {@code parrot:variant} property of {@link Parrot}.
     *
     * @see Parrot#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Parrot, Parrot.Variant> PARROT_VARIANT = create("parrot:variant", Parrot.class, Parrot.Variant.class);

    /**
     * The {@code phantom:should_burn_in_day} property of {@link Phantom}.
     *
     * @see Phantom#shouldBurnInDay()
     * @since 0.9.0
     */
    public static final EntityProperty<Phantom, Boolean> PHANTOM_SHOULD_BURN_IN_DAY = create("phantom:should_burn_in_day", Phantom.class, Boolean.class);

    /**
     * The {@code phantom:size} property of {@link Phantom}.
     *
     * @see Phantom#getSize()
     * @since 0.9.0
     */
    public static final EntityProperty<Phantom, Integer> PHANTOM_SIZE = create("phantom:size", Phantom.class, Integer.class);

    /**
     * The {@code pig:sound_variant} property of {@link Pig}.
     *
     * @see Pig#getSoundVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Pig, Pig.SoundVariant> PIG_SOUND_VARIANT = create("pig:sound_variant", Pig.class, Pig.SoundVariant.class);

    /**
     * The {@code pig:variant} property of {@link Pig}.
     *
     * @see Pig#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Pig, Pig.Variant> PIG_VARIANT = create("pig:variant", Pig.class, Pig.Variant.class);

    /**
     * The {@code piglin:charging_crossbow} property of {@link Piglin}.
     *
     * @see Piglin#isChargingCrossbow()
     * @since 0.9.0
     */
    public static final EntityProperty<Piglin, Boolean> PIGLIN_CHARGING_CROSSBOW = create("piglin:charging_crossbow", Piglin.class, Boolean.class);

    /**
     * The {@code piglin:dancing} property of {@link Piglin}.
     *
     * @see Piglin#isDancing()
     * @since 0.9.0
     */
    public static final EntityProperty<Piglin, Boolean> PIGLIN_DANCING = create("piglin:dancing", Piglin.class, Boolean.class);

    /**
     * The {@code polar_bear:standing} property of {@link PolarBear}.
     *
     * @see PolarBear#isStanding()
     * @since 0.9.0
     */
    public static final EntityProperty<PolarBear, Boolean> POLAR_BEAR_STANDING = create("polar_bear:standing", PolarBear.class, Boolean.class);

    /**
     * The {@code puffer_fish:puff_state} property of {@link PufferFish}.
     *
     * @see PufferFish#getPuffState()
     * @since 0.9.0
     */
    public static final EntityProperty<PufferFish, Integer> PUFFER_FISH_PUFF_STATE = create("puffer_fish:puff_state", PufferFish.class, Integer.class);

    /**
     * The {@code rabbit:variant} property of {@link Rabbit}.
     *
     * @see Rabbit#getRabbitType()
     * @since 0.9.0
     */
    public static final EntityProperty<Rabbit, Rabbit.Type> RABBIT_VARIANT = create("rabbit:variant", Rabbit.class, Rabbit.Type.class);

    /**
     * The {@code raider:celebrating} property of {@link Raider}.
     *
     * @see Raider#isCelebrating()
     * @since 0.9.0
     */
    public static final EntityProperty<Raider, Boolean> RAIDER_CELEBRATING = create("raider:celebrating", Raider.class, Boolean.class);

    /**
     * The {@code salmon:variant} property of {@link Salmon}.
     *
     * @see Salmon#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Salmon, Salmon.Variant> SALMON_VARIANT = create("salmon:variant", Salmon.class, Salmon.Variant.class);

    /**
     * The {@code sheep:sheared} property of {@link Sheep}.
     *
     * @see Sheep#isSheared()
     * @since 0.9.0
     */
    public static final EntityProperty<Sheep, Boolean> SHEEP_SHEARED = create("sheep:sheared", Sheep.class, Boolean.class);

    /**
     * The {@code shulker:attached_face} property of {@link Shulker}.
     *
     * @see Shulker#getAttachedFace()
     * @since 0.9.0
     */
    public static final EntityProperty<Shulker, BlockFace> SHULKER_ATTACHED_FACE = create("shulker:attached_face", Shulker.class, BlockFace.class);

    /**
     * The {@code shulker:peek} property of {@link Shulker}.
     *
     * @see Shulker#getPeek()
     * @since 0.9.0
     */
    public static final EntityProperty<Shulker, Float> SHULKER_PEEK = create("shulker:peek", Shulker.class, Float.class);

    /**
     * The {@code sized_fireball:display_item} property of {@link SizedFireball}.
     *
     * @see SizedFireball#getDisplayItem()
     * @since 0.9.0
     */
    public static final EntityProperty<SizedFireball, ItemStack> SIZED_FIREBALL_DISPLAY_ITEM = create("sized_fireball:display_item", SizedFireball.class, ItemStack.class);

    /**
     * The {@code sniffer:state} property of {@link Sniffer}.
     *
     * @see Sniffer#getState()
     * @since 0.9.0
     */
    public static final EntityProperty<Sniffer, Sniffer.State> SNIFFER_STATE = create("sniffer:state", Sniffer.class, Sniffer.State.class);

    /**
     * The {@code snowman:derp} property of {@link Snowman}.
     *
     * @see Snowman#isDerp()
     * @since 0.9.0
     */
    public static final EntityProperty<Snowman, Boolean> SNOWMAN_DERP = create("snowman:derp", Snowman.class, Boolean.class);

    /**
     * The {@code spellcaster:spell} property of {@link Spellcaster}.
     *
     * @see Spellcaster#getSpell()
     * @since 0.9.0
     */
    public static final EntityProperty<Spellcaster, Spellcaster.Spell> SPELLCASTER_SPELL = create("spellcaster:spell", Spellcaster.class, Spellcaster.Spell.class);

    /**
     * The {@code strider:shivering} property of {@link Strider}.
     *
     * @see Strider#isShivering()
     * @since 0.9.0
     */
    public static final EntityProperty<Strider, Boolean> STRIDER_SHIVERING = create("strider:shivering", Strider.class, Boolean.class);

    /**
     * The {@code tadpole:age} property of {@link Tadpole}.
     *
     * @see Tadpole#getAge()
     * @since 0.9.0
     */
    public static final EntityProperty<Tadpole, Integer> TADPOLE_AGE = create("tadpole:age", Tadpole.class, Integer.class);

    /**
     * The {@code tadpole:age_lock} property of {@link Tadpole}.
     *
     * @see Tadpole#getAgeLock()
     * @since 0.9.0
     */
    public static final EntityProperty<Tadpole, Boolean> TADPOLE_AGE_LOCK = create("tadpole:age_lock", Tadpole.class, Boolean.class);

    /**
     * The {@code tnt_primed:block_data} property of {@link TNTPrimed}.
     *
     * @see TNTPrimed#getBlockData()
     * @since 0.9.0
     */
    public static final EntityProperty<TNTPrimed, BlockData> TNT_PRIMED_BLOCK_DATA = create("tnt_primed:block_data", TNTPrimed.class, BlockData.class);

    /**
     * The {@code text_display:alignment} property of {@link TextDisplay}.
     *
     * @see TextDisplay#getAlignment()
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, TextDisplay.TextAlignment> TEXT_DISPLAY_ALIGNMENT = create("text_display:alignment", TextDisplay.class, TextDisplay.TextAlignment.class);

    /**
     * The {@code text_display:background_color} property of {@link TextDisplay}.
     *
     * @see TextDisplay#getBackgroundColor()
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, Color> TEXT_DISPLAY_BACKGROUND_COLOR = create("text_display:background_color", TextDisplay.class, Color.class);

    /**
     * The {@code text_display:default_background} property of {@link TextDisplay}.
     *
     * @see TextDisplay#isDefaultBackground()
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, Boolean> TEXT_DISPLAY_DEFAULT_BACKGROUND = create("text_display:default_background", TextDisplay.class, Boolean.class);

    /**
     * The {@code text_display:line_width} property of {@link TextDisplay}.
     *
     * @see TextDisplay#getLineWidth()
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, Integer> TEXT_DISPLAY_LINE_WIDTH = create("text_display:line_width", TextDisplay.class, Integer.class);

    /**
     * The {@code text_display:see_through} property of {@link TextDisplay}.
     *
     * @see TextDisplay#isSeeThrough()
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, Boolean> TEXT_DISPLAY_SEE_THROUGH = create("text_display:see_through", TextDisplay.class, Boolean.class);

    /**
     * The {@code text_display:shadowed} property of {@link TextDisplay}.
     *
     * @see TextDisplay#isShadowed()
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, Boolean> TEXT_DISPLAY_SHADOWED = create("text_display:shadowed", TextDisplay.class, Boolean.class);

    /**
     * The {@code text_display:text} property of {@link TextDisplay}.
     *
     * @see TextDisplay#text()
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, Component> TEXT_DISPLAY_TEXT = create("text_display:text", TextDisplay.class, Component.class);

    /**
     * The {@code text_display:text_opacity} property of {@link TextDisplay}.
     *
     * @since 0.9.0
     */
    public static final EntityProperty<TextDisplay, Integer> TEXT_DISPLAY_TEXT_OPACITY = create("text_display:text_opacity", TextDisplay.class, Integer.class);

    /**
     * The {@code throwable_projectile:item} property of {@link ThrowableProjectile}.
     *
     * @see ThrowableProjectile#getItem()
     * @since 0.9.0
     */
    public static final EntityProperty<ThrowableProjectile, ItemStack> THROWABLE_PROJECTILE_ITEM = create("throwable_projectile:item", ThrowableProjectile.class, ItemStack.class);

    /**
     * The {@code trident:glint} property of {@link Trident}.
     *
     * @see Trident#hasGlint()
     * @since 0.9.0
     */
    public static final EntityProperty<Trident, Boolean> TRIDENT_GLINT = create("trident:glint", Trident.class, Boolean.class);

    /**
     * The {@code tropical_fish:body_color} property of {@link TropicalFish}.
     *
     * @see TropicalFish#getBodyColor()
     * @since 0.9.0
     */
    public static final EntityProperty<TropicalFish, DyeColor> TROPICAL_FISH_BODY_COLOR = create("tropical_fish:body_color", TropicalFish.class, DyeColor.class);

    /**
     * The {@code tropical_fish:pattern} property of {@link TropicalFish}.
     *
     * @see TropicalFish#getPattern()
     * @since 0.9.0
     */
    public static final EntityProperty<TropicalFish, TropicalFish.Pattern> TROPICAL_FISH_PATTERN = create("tropical_fish:pattern", TropicalFish.class, TropicalFish.Pattern.class);

    /**
     * The {@code tropical_fish:pattern_color} property of {@link TropicalFish}.
     *
     * @see TropicalFish#getPatternColor()
     * @since 0.9.0
     */
    public static final EntityProperty<TropicalFish, DyeColor> TROPICAL_FISH_PATTERN_COLOR = create("tropical_fish:pattern_color", TropicalFish.class, DyeColor.class);

    /**
     * The {@code turtle:has_egg} property of {@link Turtle}.
     *
     * @see Turtle#hasEgg()
     * @since 0.9.0
     */
    public static final EntityProperty<Turtle, Boolean> TURTLE_HAS_EGG = create("turtle:has_egg", Turtle.class, Boolean.class);

    /**
     * The {@code vex:charging} property of {@link Vex}.
     *
     * @see Vex#isCharging()
     * @since 0.9.0
     */
    public static final EntityProperty<Vex, Boolean> VEX_CHARGING = create("vex:charging", Vex.class, Boolean.class);

    /**
     * The {@code villager:profession} property of {@link Villager}.
     *
     * @see Villager#getProfession()
     * @since 0.9.0
     */
    public static final EntityProperty<Villager, Villager.Profession> VILLAGER_PROFESSION = create("villager:profession", Villager.class, Villager.Profession.class);

    /**
     * The {@code villager:villager_level} property of {@link Villager}.
     *
     * @see Villager#getVillagerLevel()
     * @since 0.9.0
     */
    public static final EntityProperty<Villager, Integer> VILLAGER_VILLAGER_LEVEL = create("villager:villager_level", Villager.class, Integer.class);

    /**
     * The {@code villager:villager_type} property of {@link Villager}.
     *
     * @see Villager#getVillagerType()
     * @since 0.9.0
     */
    public static final EntityProperty<Villager, Villager.Type> VILLAGER_VILLAGER_TYPE = create("villager:villager_type", Villager.class, Villager.Type.class);

    /**
     * The {@code wither_skull:charged} property of {@link WitherSkull}.
     *
     * @see WitherSkull#isCharged()
     * @since 0.9.0
     */
    public static final EntityProperty<WitherSkull, Boolean> WITHER_SKULL_CHARGED = create("wither_skull:charged", WitherSkull.class, Boolean.class);

    /**
     * The {@code wolf:angry} property of {@link Wolf}.
     *
     * @see Wolf#isAngry()
     * @since 0.9.0
     */
    public static final EntityProperty<Wolf, Boolean> WOLF_ANGRY = create("wolf:angry", Wolf.class, Boolean.class);

    /**
     * The {@code wolf:interested} property of {@link Wolf}.
     *
     * @see Wolf#isInterested()
     * @since 0.9.0
     */
    public static final EntityProperty<Wolf, Boolean> WOLF_INTERESTED = create("wolf:interested", Wolf.class, Boolean.class);

    /**
     * The {@code wolf:sound_variant} property of {@link Wolf}.
     *
     * @see Wolf#getSoundVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Wolf, Wolf.SoundVariant> WOLF_SOUND_VARIANT = create("wolf:sound_variant", Wolf.class, Wolf.SoundVariant.class);

    /**
     * The {@code wolf:variant} property of {@link Wolf}.
     *
     * @see Wolf#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<Wolf, Wolf.Variant> WOLF_VARIANT = create("wolf:variant", Wolf.class, Wolf.Variant.class);

    /**
     * The {@code zombie:should_burn_in_day} property of {@link Zombie}.
     *
     * @see Zombie#shouldBurnInDay()
     * @since 0.9.0
     */
    public static final EntityProperty<Zombie, Boolean> ZOMBIE_SHOULD_BURN_IN_DAY = create("zombie:should_burn_in_day", Zombie.class, Boolean.class);

    /**
     * The {@code zombie_nautilus:variant} property of {@link ZombieNautilus}.
     *
     * @see ZombieNautilus#getVariant()
     * @since 0.9.0
     */
    public static final EntityProperty<ZombieNautilus, ZombieNautilus.Variant> ZOMBIE_NAUTILUS_VARIANT = create("zombie_nautilus:variant", ZombieNautilus.class, ZombieNautilus.Variant.class);

    /**
     * The {@code zombie_villager:villager_profession} property of {@link ZombieVillager}.
     *
     * @see ZombieVillager#getVillagerProfession()
     * @since 0.9.0
     */
    public static final EntityProperty<ZombieVillager, Villager.Profession> ZOMBIE_VILLAGER_VILLAGER_PROFESSION = create("zombie_villager:villager_profession", ZombieVillager.class, Villager.Profession.class);

    /**
     * The {@code zombie_villager:villager_type} property of {@link ZombieVillager}.
     *
     * @see ZombieVillager#getVillagerType()
     * @since 0.9.0
     */
    public static final EntityProperty<ZombieVillager, Villager.Type> ZOMBIE_VILLAGER_VILLAGER_TYPE = create("zombie_villager:villager_type", ZombieVillager.class, Villager.Type.class);

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
