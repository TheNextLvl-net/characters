package net.thenextlvl.character.plugin.codec;

import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.entity.CollarColorable;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.character.codec.EntityCodec;
import net.thenextlvl.character.codec.EntityCodecRegistry;
import net.thenextlvl.character.plugin.command.argument.BlockDataArgument;
import net.thenextlvl.character.plugin.model.PaperEntityEquipment;
import net.thenextlvl.character.plugin.serialization.AttributeAdapter;
import net.thenextlvl.character.plugin.serialization.BlockDataAdapter;
import net.thenextlvl.character.plugin.serialization.EntityEquipmentAdapter;
import net.thenextlvl.character.plugin.serialization.RegistryAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.AttributeInstance;
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Tameable;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public final class EntityCodecs {

    public static void registerAll() {
        EntityCodecRegistry.registry().registerAll(List.of(
                AGE,
                BASE_POTION_TYPE, AREA_EFFECT_CLOUD_COLOR, DURATION_ON_USE, DURATION, PARTICLE, ATTRIBUTES, RADIUS, REAPPLICATION_DELAY, WAIT_TIME,
                ARROW_COLOR,
                HEAD_UP, LYING_DOWN, CAT_VARIANT,
                DANCING, CAN_DUPLICATE, DUPLICATION_COOLDOWN,
                COLLAR_COLOR,
                POWERED, MAX_FUSE_TICKS, EXPLOSION_RADIUS,
                ABSORPTION_AMOUNT, 
                SCREAMING, STARED_AT, CARRIED_BLOCK,
                VISUAL_FIRE, FIRE_TICKS, GLOWING, GRAVITY, INVISIBLE, INVULNERABLE, NO_PHYSICS, POSE, FREEZE_TICKS, LOCK_FREEZE_TICKS, SILENT, SNEAKING,
                CROUCHING, LEAPING, SLEEPING, VARIANT,
                GLIDING,
                EQUIPMENT, AI, ARROWS_IN_BODY, BEE_STINGERS_IN_BODY, BODY_YAW, COLLIDABLE,
                AGGRESSIVE, AWARE, LEFT_HANDED,
                SITTING,
                SADDLE,
                TAMED
        ));
    }

    private static final EntityCodec<?, ?> AGE = EntityCodec.intCodec(Key.key("ageable", "age"), Ageable.class)
            .getter(Ageable::getAge)
            .setter(Ageable::setAge)
            .build();

    private static final EntityCodec<?, ?> EQUIPMENT = EntityCodec.builder(Key.key("living_entity", "equipment"), LivingEntity.class, PaperEntityEquipment.class)
            .getter(PaperEntityEquipment::of)
            .setter((livingEntity, paperEntityEquipment) -> {
                paperEntityEquipment.apply(livingEntity);
            })
            .adapter(new EntityEquipmentAdapter())
            .build();

    private static final EntityCodec<?, ?> BASE_POTION_TYPE = EntityCodec.enumCodec(Key.key("area_effect_cloud", "base_potion_type"), AreaEffectCloud.class, PotionType.class)
            .getter(AreaEffectCloud::getBasePotionType)
            .setter(AreaEffectCloud::setBasePotionType)
            .build();

    private static final EntityCodec<?, ?> AREA_EFFECT_CLOUD_COLOR = EntityCodec.intCodec(Key.key("area_effect_cloud", "color"), AreaEffectCloud.class)
            .getter(areaEffectCloud -> areaEffectCloud.getColor().asRGB())
            .setter((areaEffectCloud, integer) -> {
                areaEffectCloud.setColor(integer != null ? Color.fromARGB(integer) : null);
            })
            .build();

    private static final EntityCodec<?, ?> DURATION_ON_USE = EntityCodec.intCodec(Key.key("area_effect_cloud", "duration_on_use"), AreaEffectCloud.class)
            .getter(AreaEffectCloud::getDurationOnUse)
            .setter(AreaEffectCloud::setDurationOnUse)
            .build();

    private static final EntityCodec<?, ?> DURATION = EntityCodec.intCodec(Key.key("area_effect_cloud", "duration"), AreaEffectCloud.class)
            .getter(AreaEffectCloud::getDuration)
            .setter(AreaEffectCloud::setDuration)
            .build();

    private static final EntityCodec<?, ?> PARTICLE = EntityCodec.enumCodec(Key.key("area_effect_cloud", "particle"), AreaEffectCloud.class, Particle.class)
            .getter(AreaEffectCloud::getParticle)
            .setter((areaEffectCloud, particle) -> {
                areaEffectCloud.setParticle(particle);
            })
            .build();

    private static final EntityCodec<?, ?> RADIUS = EntityCodec.floatCodec(Key.key("area_effect_cloud", "radius"), AreaEffectCloud.class)
            .getter(AreaEffectCloud::getRadius).setter(AreaEffectCloud::setRadius).build();

    private static final EntityCodec<?, ?> REAPPLICATION_DELAY = EntityCodec.intCodec(Key.key("area_effect_cloud", "reapplication_delay"), AreaEffectCloud.class)
            .getter(AreaEffectCloud::getDurationOnUse).setter(AreaEffectCloud::setReapplicationDelay)
            .argumentType(ArgumentTypes.time()).build();

    private static final EntityCodec<?, ?> WAIT_TIME = EntityCodec.intCodec(Key.key("area_effect_cloud", "wait_time"), AreaEffectCloud.class)
            .getter(AreaEffectCloud::getWaitTime).setter(AreaEffectCloud::setWaitTime)
            .argumentType(ArgumentTypes.time()).build();

    private static final EntityCodec<?, ?> ARROW_COLOR = EntityCodec.intCodec(Key.key("arrow", "color"), Arrow.class)
            .getter(arrow -> {
                var color = arrow.getColor();
                return color != null ? color.asARGB() : null;
            }).setter((arrow, color) -> {
                arrow.setColor(color != null ? Color.fromARGB(color) : null);
            }).build();

    private static final EntityCodec<?, ?> ATTRIBUTES = EntityCodec.<Attributable, Set<AttributeInstance>>builder(
                    Key.key("attributable", "attributes"), Attributable.class, Set.class
            ).getter(attributable -> Registry.ATTRIBUTE.stream()
                    .map(attributable::getAttribute)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()))
            .setter((attributable, attributes) -> {
                attributes.forEach(attributeInstance -> {
                    var attribute = attributable.getAttribute(attributeInstance.getAttribute());
                    if (attribute == null) return;
                    attribute.setBaseValue(attributeInstance.getBaseValue());
                    attribute.getModifiers().forEach(attributeModifier -> {
                        var modifier = attribute.getModifier(attributeModifier.key());
                        if (modifier != null) attribute.removeModifier(modifier);
                        attribute.addModifier(attributeModifier);
                    });
                });
            })
            .adapter(new AttributeAdapter())
            .build();


    private static final EntityCodec<?, ?> HEAD_UP = EntityCodec.booleanCodec(Key.key("cat", "head_up"), Cat.class)
            .getter(Cat::isHeadUp).setter(Cat::setHeadUp).build();

    private static final EntityCodec<?, ?> LYING_DOWN = EntityCodec.booleanCodec(Key.key("cat", "lying_down"), Cat.class)
            .getter(Cat::isLyingDown).setter(Cat::setLyingDown).build();

    private static final EntityCodec<?, ?> CAT_VARIANT = registryCodec(Key.key("cat", "variant"), Cat.class, Cat.Type.class, RegistryKey.CAT_VARIANT)
            .getter(Cat::getCatType).setter(Cat::setCatType).build();

    private static final EntityCodec<?, ?> DANCING = EntityCodec.booleanCodec(Key.key("allay", "dancing"), Allay.class)
            .getter(Allay::isDancing).setter((allay, dancing) -> {
                if (dancing) allay.startDancing();
                else allay.stopDancing();
            }).build();

    private static final EntityCodec<?, ?> CAN_DUPLICATE = EntityCodec.booleanCodec(Key.key("allay", "can_duplicate"), Allay.class)
            .getter(Allay::canDuplicate).setter(Allay::setCanDuplicate).build();

    private static final EntityCodec<?, ?> DUPLICATION_COOLDOWN = EntityCodec.longCodec(Key.key("allay", "duplication_cooldown"), Allay.class)
            .getter(Allay::getDuplicationCooldown).setter(Allay::setDuplicationCooldown).build();

    private static final EntityCodec<?, ?> COLLAR_COLOR = EntityCodec.enumCodec(Key.key("collar_colorable", "collar_color"), CollarColorable.class, DyeColor.class)
            .getter(CollarColorable::getCollarColor).setter(CollarColorable::setCollarColor).build();

    private static final EntityCodec<?, ?> POWERED = EntityCodec.booleanCodec(Key.key("creeper", "powered"), Creeper.class)
            .getter(Creeper::isPowered).setter(Creeper::setPowered).build();

    private static final EntityCodec<?, ?> MAX_FUSE_TICKS = EntityCodec.intCodec(Key.key("creeper", "max_fuse_ticks"), Creeper.class)
            .getter(Creeper::getMaxFuseTicks).setter(Creeper::setMaxFuseTicks).build();

    private static final EntityCodec<?, ?> EXPLOSION_RADIUS = EntityCodec.intCodec(Key.key("creeper", "explosion_radius"), Creeper.class)
            .getter(Creeper::getExplosionRadius).setter(Creeper::setExplosionRadius).build();

    private static final EntityCodec<?, ?> ABSORPTION_AMOUNT = EntityCodec.doubleCodec(Key.key("damageable", "absorption_amount"), Damageable.class)
            .getter(Damageable::getAbsorptionAmount).setter(Damageable::setAbsorptionAmount).build();

    private static final EntityCodec<?, ?> SCREAMING = EntityCodec.booleanCodec(Key.key("enderman", "screaming"), Enderman.class)
            .getter(Enderman::isScreaming).setter(Enderman::setScreaming).build();

    private static final EntityCodec<?, ?> STARED_AT = EntityCodec.booleanCodec(Key.key("enderman", "stared_at"), Enderman.class)
            .getter(Enderman::hasBeenStaredAt).setter(Enderman::setHasBeenStaredAt).build();

    private static final EntityCodec<?, ?> CARRIED_BLOCK = EntityCodec.builder(Key.key("enderman", "carried_block"), Enderman.class, BlockData.class)
            .getter(Enderman::getCarriedBlock)
            .setter(Enderman::setCarriedBlock)
            .adapter(new BlockDataAdapter(Bukkit.getServer())) // todo: get rid of Bukkit
            .argumentType(new BlockDataArgument())
            .build();

    private static final EntityCodec<?, ?> VISUAL_FIRE = EntityCodec.enumCodec(Key.key("entity", "visual_fire"), Entity.class, TriState.class)
            .getter(Entity::getVisualFire).setter((entity, triState) -> {
                entity.setVisualFire(triState);
            }).build();

    private static final EntityCodec<?, ?> FIRE_TICKS = EntityCodec.intCodec(Key.key("entity", "fire_ticks"), Entity.class)
            .getter(Entity::getFireTicks).setter(Entity::setFireTicks).build();

    private static final EntityCodec<?, ?> GLOWING = EntityCodec.booleanCodec(Key.key("entity", "glowing"), Entity.class)
            .getter(Entity::isGlowing).setter(Entity::setGlowing).build();

    private static final EntityCodec<?, ?> GRAVITY = EntityCodec.booleanCodec(Key.key("entity", "gravity"), Entity.class)
            .getter(Entity::hasGravity).setter(Entity::setGravity).build();

    private static final EntityCodec<?, ?> INVISIBLE = EntityCodec.booleanCodec(Key.key("entity", "invisible"), Entity.class)
            .getter(Entity::isInvisible).setter(Entity::setInvisible).build();

    private static final EntityCodec<?, ?> INVULNERABLE = EntityCodec.booleanCodec(Key.key("entity", "invulnerable"), Entity.class)
            .getter(Entity::isInvulnerable).setter(Entity::setInvulnerable).build();

    private static final EntityCodec<?, ?> NO_PHYSICS = EntityCodec.booleanCodec(Key.key("entity", "no_physics"), Entity.class)
            .getter(Entity::hasNoPhysics).setter(Entity::setNoPhysics).build();

    private static final EntityCodec<?, ?> POSE = EntityCodec.enumCodec(Key.key("entity", "pose"), Entity.class, Pose.class)
            .getter(Entity::getPose).setter((entity, pose) -> {
                entity.setPose(pose, true);
            }).build();

    private static final EntityCodec<?, ?> FREEZE_TICKS = EntityCodec.intCodec(Key.key("entity", "freeze_ticks"), Entity.class)
            .getter(Entity::getFreezeTicks).setter(Entity::setFreezeTicks).build();

    private static final EntityCodec<?, ?> LOCK_FREEZE_TICKS = EntityCodec.booleanCodec(Key.key("entity", "lock_freeze_ticks"), Entity.class)
            .getter(Entity::isFreezeTickingLocked).setter(Entity::lockFreezeTicks).build();

    private static final EntityCodec<?, ?> SILENT = EntityCodec.booleanCodec(Key.key("entity", "silent"), Entity.class)
            .getter(Entity::isSilent).setter(Entity::setSilent).build();

    private static final EntityCodec<?, ?> SNEAKING = EntityCodec.booleanCodec(Key.key("entity", "sneaking"), Entity.class)
            .getter(Entity::isSneaking).setter(Entity::setSneaking).build();

    private static final EntityCodec<?, ?> CROUCHING = EntityCodec.booleanCodec(Key.key("fox", "crouching"), Fox.class)
            .getter(Fox::isCrouching).setter(Fox::setCrouching).build();

    private static final EntityCodec<?, ?> LEAPING = EntityCodec.booleanCodec(Key.key("fox", "leaping"), Fox.class)
            .getter(Fox::isLeaping).setter(Fox::setLeaping).build();

    private static final EntityCodec<?, ?> SLEEPING = EntityCodec.booleanCodec(Key.key("fox", "sleeping"), Fox.class)
            .getter(Fox::isSleeping).setter(Fox::setSleeping).build();

    private static final EntityCodec<?, ?> VARIANT = EntityCodec.enumCodec(Key.key("fox", "variant"), Fox.class, Fox.Type.class)
            .getter(Fox::getFoxType).setter(Fox::setFoxType).build();

    private static final EntityCodec<?, ?> GLIDING = EntityCodec.booleanCodec(Key.key("player", "gliding"), Player.class)
            .getter(Player::isGliding).setter(Player::setGliding).build();

    private static final EntityCodec<?, ?> AI = EntityCodec.booleanCodec(Key.key("living_entity", "ai"), LivingEntity.class)
            .getter(LivingEntity::hasAI).setter(LivingEntity::setAI).build();

    private static final EntityCodec<?, ?> ARROWS_IN_BODY = EntityCodec.intCodec(Key.key("living_entity", "arrows_in_body"), LivingEntity.class)
            .getter(LivingEntity::getArrowsInBody).setter((livingEntity, arrows) -> {
                livingEntity.setArrowsInBody(arrows, false);
            }).build();

    private static final EntityCodec<?, ?> BEE_STINGERS_IN_BODY = EntityCodec.intCodec(Key.key("living_entity", "bee_stingers_in_body"), LivingEntity.class)
            .getter(LivingEntity::getBeeStingersInBody).setter(LivingEntity::setBeeStingersInBody).build();

    private static final EntityCodec<?, ?> BODY_YAW = EntityCodec.floatCodec(Key.key("living_entity", "body_yaw"), LivingEntity.class)
            .getter(LivingEntity::getBodyYaw).setter(LivingEntity::setBodyYaw).build();

    private static final EntityCodec<?, ?> COLLIDABLE = EntityCodec.booleanCodec(Key.key("living_entity", "collidable"), LivingEntity.class)
            .getter(LivingEntity::isCollidable).setter(LivingEntity::setCollidable).build();

    private static final EntityCodec<?, ?> AGGRESSIVE = EntityCodec.booleanCodec(Key.key("mob", "aggressive"), Mob.class)
            .getter(Mob::isAggressive).setter(Mob::setAggressive).build();

    private static final EntityCodec<?, ?> AWARE = EntityCodec.booleanCodec(Key.key("mob", "aware"), Mob.class)
            .getter(Mob::isAware).setter(Mob::setAware).build();

    private static final EntityCodec<?, ?> LEFT_HANDED = EntityCodec.booleanCodec(Key.key("mob", "left_handed"), Mob.class)
            .getter(Mob::isLeftHanded).setter(Mob::setLeftHanded).build();

    private static final EntityCodec<?, ?> SITTING = EntityCodec.booleanCodec(Key.key("sittable", "sitting"), Sittable.class)
            .getter(Sittable::isSitting).setter(Sittable::setSitting).build();

    private static final EntityCodec<?, ?> SADDLE = EntityCodec.booleanCodec(Key.key("steerable", "saddled"), Steerable.class)
            .getter(Steerable::hasSaddle).setter(Steerable::setSaddle).build();

    private static final EntityCodec<?, ?> TAMED = EntityCodec.booleanCodec(Key.key("tameable", "tamed"), Tameable.class)
            .getter(Tameable::isTamed).setter(Tameable::setTamed).build();

    private static <E, T extends Keyed> EntityCodec.Builder<E, T> registryCodec(Key key, Class<E> entityType, Class<T> type, RegistryKey<T> registryKey) {
        return EntityCodec.builder(key, entityType, type)
                .argumentType(ArgumentTypes.resource(registryKey))
                .adapter(new RegistryAdapter<>(registryKey));
    }
}
