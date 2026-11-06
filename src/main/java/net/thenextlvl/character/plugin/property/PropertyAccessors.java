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
import org.bukkit.material.Colorable;
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

            enumeration(EntityProperties.ENTITY_VISUAL_FIRE, Entity::getVisualFire, Entity::setVisualFire),
            integer(EntityProperties.ENTITY_FIRE_TICKS, Entity::getFireTicks, Entity::setFireTicks),
            bool(EntityProperties.ENTITY_GLOWING, Entity::isGlowing, Entity::setGlowing),
            bool(EntityProperties.ENTITY_GRAVITY, Entity::hasGravity, Entity::setGravity),
            bool(EntityProperties.ENTITY_INVISIBLE, Entity::isInvisible, Entity::setInvisible),
            bool(EntityProperties.ENTITY_INVULNERABLE, Entity::isMarkedInvulnerable, Entity::setInvulnerable),
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
            integer(EntityProperties.LIVING_ENTITY_ARROWS_IN_BODY, LivingEntity::getArrowsInBody, (entity, arrows) -> entity.setArrowsInBody(arrows, false)),
            integer(EntityProperties.LIVING_ENTITY_BEE_STINGERS_IN_BODY, LivingEntity::getBeeStingersInBody, LivingEntity::setBeeStingersInBody),
            floating(EntityProperties.LIVING_ENTITY_BODY_YAW, LivingEntity::getBodyYaw, LivingEntity::setBodyYaw),
            // todo: can we persist the health without issues?
            // doubleFloating("living_entity:health", LivingEntity.class, LivingEntity::getHealth, LivingEntity::setHealth),
            bool(EntityProperties.LIVING_ENTITY_COLLIDABLE, LivingEntity::isCollidable, LivingEntity::setCollidable),

            bool(EntityProperties.MOB_AGGRESSIVE, Mob::isAggressive, Mob::setAggressive),
            bool(EntityProperties.MOB_LEFT_HANDED, Mob::isLeftHanded, Mob::setLeftHanded),

            bool(EntityProperties.SITTABLE_SITTING, Sittable::isSitting, Sittable::setSitting),

            bool(EntityProperties.STEERABLE_SADDLED, Steerable::hasSaddle, Steerable::setSaddle),

            bool(EntityProperties.TAMEABLE_TAMED, Tameable::isTamed, Tameable::setTamed),

            bool(EntityProperties.ABSTRACT_ARROW_CRITICAL, AbstractArrow::isCritical, AbstractArrow::setCritical),

            integer(EntityProperties.ABSTRACT_CUBE_MOB_SIZE, AbstractCubeMob::getSize, AbstractCubeMob::setSize),

            bool(EntityProperties.ABSTRACT_HORSE_EATING, AbstractHorse::isEating, AbstractHorse::setEating),
            bool(EntityProperties.ABSTRACT_HORSE_EATING_GRASS, AbstractHorse::isEatingGrass, AbstractHorse::setEatingGrass),
            bool(EntityProperties.ABSTRACT_HORSE_REARING, AbstractHorse::isRearing, AbstractHorse::setRearing),

            bool(EntityProperties.ABSTRACT_SKELETON_SHOULD_BURN_IN_DAY, AbstractSkeleton::shouldBurnInDay, AbstractSkeleton::setShouldBurnInDay),

            bool(EntityProperties.AGEABLE_AGE_LOCK, Ageable::getAgeLock, Ageable::setAgeLock),

            property(EntityProperties.ARMOR_STAND_BODY_ROTATIONS, ArmorStand::getBodyRotations, ArmorStand::setBodyRotations, null),
            property(EntityProperties.ARMOR_STAND_HEAD_ROTATIONS, ArmorStand::getHeadRotations, ArmorStand::setHeadRotations, null),
            property(EntityProperties.ARMOR_STAND_LEFT_ARM_ROTATIONS, ArmorStand::getLeftArmRotations, ArmorStand::setLeftArmRotations, null),
            property(EntityProperties.ARMOR_STAND_LEFT_LEG_ROTATIONS, ArmorStand::getLeftLegRotations, ArmorStand::setLeftLegRotations, null),
            property(EntityProperties.ARMOR_STAND_RIGHT_ARM_ROTATIONS, ArmorStand::getRightArmRotations, ArmorStand::setRightArmRotations, null),
            property(EntityProperties.ARMOR_STAND_RIGHT_LEG_ROTATIONS, ArmorStand::getRightLegRotations, ArmorStand::setRightLegRotations, null),

            enumeration(EntityProperties.ARROW_BASE_POTION_TYPE, Arrow::getBasePotionType, Arrow::setBasePotionType),

            bool(EntityProperties.AXOLOTL_PLAYING_DEAD, Axolotl::isPlayingDead, Axolotl::setPlayingDead),
            enumeration(EntityProperties.AXOLOTL_VARIANT, Axolotl::getVariant, Axolotl::setVariant),

            bool(EntityProperties.BAT_AWAKE, Bat::isAwake, Bat::setAwake),

            bool(EntityProperties.BEE_HAS_NECTAR, Bee::hasNectar, Bee::setHasNectar),
            bool(EntityProperties.BEE_HAS_STUNG, Bee::hasStung, Bee::setHasStung),
            enumeration(EntityProperties.BEE_ROLLING_OVERRIDE, Bee::getRollingOverride, Bee::setRollingOverride),

            property(EntityProperties.BLOCK_DISPLAY_BLOCK, BlockDisplay::getBlock, BlockDisplay::setBlock, new BlockDataArgumentType()),

            bool(EntityProperties.BOGGED_SHEARED, Bogged::isSheared, Bogged::setSheared),

            bool(EntityProperties.CAMEL_DASHING, Camel::isDashing, Camel::setDashing),

            property(EntityProperties.CAT_SOUND_VARIANT, Cat::getSoundVariant, Cat::setSoundVariant, ArgumentTypes.resource(RegistryKey.CAT_SOUND_VARIANT)),

            bool(EntityProperties.CHESTED_HORSE_CARRYING_CHEST, ChestedHorse::isCarryingChest, ChestedHorse::setCarryingChest),

            property(EntityProperties.CHICKEN_SOUND_VARIANT, Chicken::getSoundVariant, Chicken::setSoundVariant, ArgumentTypes.resource(RegistryKey.CHICKEN_SOUND_VARIANT)),
            property(EntityProperties.CHICKEN_VARIANT, Chicken::getVariant, Chicken::setVariant, ArgumentTypes.resource(RegistryKey.CHICKEN_VARIANT)),

            enumeration(EntityProperties.COLORABLE_COLOR, Colorable::getColor, Colorable::setColor),

            enumeration(EntityProperties.COPPER_GOLEM_GOLEM_STATE, CopperGolem::getGolemState, CopperGolem::setGolemState),
            enumeration(EntityProperties.COPPER_GOLEM_WEATHERING_STATE, CopperGolem::getWeatheringState, CopperGolem::setWeatheringState),

            property(EntityProperties.COW_SOUND_VARIANT, Cow::getSoundVariant, Cow::setSoundVariant, ArgumentTypes.resource(RegistryKey.COW_SOUND_VARIANT)),
            property(EntityProperties.COW_VARIANT, Cow::getVariant, Cow::setVariant, ArgumentTypes.resource(RegistryKey.COW_VARIANT)),

            enumeration(EntityProperties.DISPLAY_BILLBOARD, Display::getBillboard, Display::setBillboard),
            property(EntityProperties.DISPLAY_BRIGHTNESS, Display::getBrightness, Display::setBrightness, null),
            floating(EntityProperties.DISPLAY_DISPLAY_HEIGHT, Display::getDisplayHeight, Display::setDisplayHeight),
            floating(EntityProperties.DISPLAY_DISPLAY_WIDTH, Display::getDisplayWidth, Display::setDisplayWidth),
            property(EntityProperties.DISPLAY_GLOW_COLOR_OVERRIDE, Display::getGlowColorOverride, Display::setGlowColorOverride, new ColorArgumentType()),
            floating(EntityProperties.DISPLAY_SHADOW_RADIUS, Display::getShadowRadius, Display::setShadowRadius),
            floating(EntityProperties.DISPLAY_SHADOW_STRENGTH, Display::getShadowStrength, Display::setShadowStrength),
            property(EntityProperties.DISPLAY_TRANSFORMATION, Display::getTransformation, Display::setTransformation, null),
            floating(EntityProperties.DISPLAY_VIEW_RANGE, Display::getViewRange, Display::setViewRange),

            property(EntityProperties.ENDER_CRYSTAL_BEAM_TARGET, EnderCrystal::getBeamTarget, EnderCrystal::setBeamTarget, null),
            bool(EntityProperties.ENDER_CRYSTAL_SHOWING_BOTTOM, EnderCrystal::isShowingBottom, EnderCrystal::setShowingBottom),

            integer(EntityProperties.EXPERIENCE_ORB_EXPERIENCE, ExperienceOrb::getExperience, ExperienceOrb::setExperience),

            property(EntityProperties.FALLING_BLOCK_BLOCK_DATA, FallingBlock::getBlockData, FallingBlock::setBlockData, new BlockDataArgumentType()),

            bool(EntityProperties.FOX_FACEPLANTED, Fox::isFaceplanted, Fox::setFaceplanted),
            bool(EntityProperties.FOX_INTERESTED, Fox::isInterested, Fox::setInterested),

            property(EntityProperties.FROG_VARIANT, Frog::getVariant, Frog::setVariant, ArgumentTypes.resource(RegistryKey.FROG_VARIANT)),

            bool(EntityProperties.GHAST_CHARGING, Ghast::isCharging, Ghast::setCharging),

            bool(EntityProperties.GOAT_LEFT_HORN, Goat::hasLeftHorn, Goat::setLeftHorn),
            bool(EntityProperties.GOAT_RIGHT_HORN, Goat::hasRightHorn, Goat::setRightHorn),
            bool(EntityProperties.GOAT_SCREAMING, Goat::isScreaming, Goat::setScreaming),

            enumeration(EntityProperties.HORSE_COLOR, Horse::getColor, Horse::setColor),
            enumeration(EntityProperties.HORSE_STYLE, Horse::getStyle, Horse::setStyle),

            floating(EntityProperties.INTERACTION_INTERACTION_HEIGHT, Interaction::getInteractionHeight, Interaction::setInteractionHeight),
            floating(EntityProperties.INTERACTION_INTERACTION_WIDTH, Interaction::getInteractionWidth, Interaction::setInteractionWidth),
            bool(EntityProperties.INTERACTION_RESPONSIVE, Interaction::isResponsive, Interaction::setResponsive),

            property(EntityProperties.ITEM_ITEM_STACK, Item::getItemStack, Item::setItemStack, ArgumentTypes.itemStack()),

            enumeration(EntityProperties.ITEM_DISPLAY_ITEM_DISPLAY_TRANSFORM, ItemDisplay::getItemDisplayTransform, ItemDisplay::setItemDisplayTransform),
            property(EntityProperties.ITEM_DISPLAY_ITEM_STACK, ItemDisplay::getItemStack, ItemDisplay::setItemStack, ArgumentTypes.itemStack()),

            bool(EntityProperties.ITEM_FRAME_FIXED, ItemFrame::isFixed, ItemFrame::setFixed),
            property(EntityProperties.ITEM_FRAME_ITEM, ItemFrame::getItem, ItemFrame::setItem, ArgumentTypes.itemStack()),
            enumeration(EntityProperties.ITEM_FRAME_ROTATION, ItemFrame::getRotation, ItemFrame::setRotation),
            bool(EntityProperties.ITEM_FRAME_VISIBLE, ItemFrame::isVisible, ItemFrame::setVisible),

            bool(EntityProperties.LIVING_ENTITY_RIPTIDING, LivingEntity::isRiptiding, LivingEntity::setRiptiding),

            enumeration(EntityProperties.LLAMA_COLOR, Llama::getColor, Llama::setColor),

            property(EntityProperties.MANNEQUIN_DESCRIPTION, Mannequin::getDescription, Mannequin::setDescription, ArgumentTypes.component()),

            property(EntityProperties.MINECART_DISPLAY_BLOCK_DATA, Minecart::getDisplayBlockData, Minecart::setDisplayBlockData, new BlockDataArgumentType()),
            integer(EntityProperties.MINECART_DISPLAY_BLOCK_OFFSET, Minecart::getDisplayBlockOffset, Minecart::setDisplayBlockOffset),

            enumeration(EntityProperties.MUSHROOM_COW_VARIANT, MushroomCow::getVariant, MushroomCow::setVariant),

            property(EntityProperties.PAINTING_ART, Painting::getArt, Painting::setArt, ArgumentTypes.resource(RegistryKey.PAINTING_VARIANT)),

            bool(EntityProperties.PANDA_EATING, Panda::isEating, Panda::setEating),
            enumeration(EntityProperties.PANDA_HIDDEN_GENE, Panda::getHiddenGene, Panda::setHiddenGene),
            enumeration(EntityProperties.PANDA_MAIN_GENE, Panda::getMainGene, Panda::setMainGene),
            bool(EntityProperties.PANDA_ON_BACK, Panda::isOnBack, Panda::setOnBack),
            bool(EntityProperties.PANDA_ROLLING, Panda::isRolling, Panda::setRolling),
            bool(EntityProperties.PANDA_SNEEZING, Panda::isSneezing, Panda::setSneezing),

            enumeration(EntityProperties.PARROT_VARIANT, Parrot::getVariant, Parrot::setVariant),

            bool(EntityProperties.PHANTOM_SHOULD_BURN_IN_DAY, Phantom::shouldBurnInDay, Phantom::setShouldBurnInDay),
            integer(EntityProperties.PHANTOM_SIZE, Phantom::getSize, Phantom::setSize),

            property(EntityProperties.PIG_SOUND_VARIANT, Pig::getSoundVariant, Pig::setSoundVariant, ArgumentTypes.resource(RegistryKey.PIG_SOUND_VARIANT)),
            property(EntityProperties.PIG_VARIANT, Pig::getVariant, Pig::setVariant, ArgumentTypes.resource(RegistryKey.PIG_VARIANT)),

            bool(EntityProperties.PIGLIN_CHARGING_CROSSBOW, Piglin::isChargingCrossbow, Piglin::setChargingCrossbow),
            bool(EntityProperties.PIGLIN_DANCING, Piglin::isDancing, Piglin::setDancing),

            bool(EntityProperties.POLAR_BEAR_STANDING, PolarBear::isStanding, PolarBear::setStanding),

            integer(EntityProperties.PUFFER_FISH_PUFF_STATE, PufferFish::getPuffState, PufferFish::setPuffState),

            enumeration(EntityProperties.RABBIT_VARIANT, Rabbit::getRabbitType, Rabbit::setRabbitType),

            bool(EntityProperties.RAIDER_CELEBRATING, Raider::isCelebrating, Raider::setCelebrating),

            enumeration(EntityProperties.SALMON_VARIANT, Salmon::getVariant, Salmon::setVariant),

            bool(EntityProperties.SHEEP_SHEARED, Sheep::isSheared, Sheep::setSheared),

            enumeration(EntityProperties.SHULKER_ATTACHED_FACE, Shulker::getAttachedFace, Shulker::setAttachedFace),
            floating(EntityProperties.SHULKER_PEEK, Shulker::getPeek, Shulker::setPeek),

            property(EntityProperties.SIZED_FIREBALL_DISPLAY_ITEM, SizedFireball::getDisplayItem, SizedFireball::setDisplayItem, ArgumentTypes.itemStack()),

            enumeration(EntityProperties.SNIFFER_STATE, Sniffer::getState, Sniffer::setState),

            bool(EntityProperties.SNOWMAN_DERP, Snowman::isDerp, Snowman::setDerp),

            enumeration(EntityProperties.SPELLCASTER_SPELL, Spellcaster::getSpell, Spellcaster::setSpell),

            bool(EntityProperties.STRIDER_SHIVERING, Strider::isShivering, Strider::setShivering),

            integer(EntityProperties.TADPOLE_AGE, Tadpole::getAge, Tadpole::setAge),
            bool(EntityProperties.TADPOLE_AGE_LOCK, Tadpole::getAgeLock, Tadpole::setAgeLock),

            property(EntityProperties.TNT_PRIMED_BLOCK_DATA, TNTPrimed::getBlockData, TNTPrimed::setBlockData, new BlockDataArgumentType()),

            enumeration(EntityProperties.TEXT_DISPLAY_ALIGNMENT, TextDisplay::getAlignment, TextDisplay::setAlignment),
            property(EntityProperties.TEXT_DISPLAY_BACKGROUND_COLOR, TextDisplay::getBackgroundColor, TextDisplay::setBackgroundColor, new ColorArgumentType()),
            bool(EntityProperties.TEXT_DISPLAY_DEFAULT_BACKGROUND, TextDisplay::isDefaultBackground, TextDisplay::setDefaultBackground),
            integer(EntityProperties.TEXT_DISPLAY_LINE_WIDTH, TextDisplay::getLineWidth, TextDisplay::setLineWidth),
            bool(EntityProperties.TEXT_DISPLAY_SEE_THROUGH, TextDisplay::isSeeThrough, TextDisplay::setSeeThrough),
            bool(EntityProperties.TEXT_DISPLAY_SHADOWED, TextDisplay::isShadowed, TextDisplay::setShadowed),
            property(EntityProperties.TEXT_DISPLAY_TEXT, TextDisplay::text, TextDisplay::text, ArgumentTypes.component()),
            integer(EntityProperties.TEXT_DISPLAY_TEXT_OPACITY, display -> display.getTextOpacity() & 0xFF, (display, opacity) -> display.setTextOpacity((byte) (int) opacity)),

            property(EntityProperties.THROWABLE_PROJECTILE_ITEM, ThrowableProjectile::getItem, ThrowableProjectile::setItem, ArgumentTypes.itemStack()),

            bool(EntityProperties.TRIDENT_GLINT, Trident::hasGlint, Trident::setGlint),

            enumeration(EntityProperties.TROPICAL_FISH_BODY_COLOR, TropicalFish::getBodyColor, TropicalFish::setBodyColor),
            enumeration(EntityProperties.TROPICAL_FISH_PATTERN, TropicalFish::getPattern, TropicalFish::setPattern),
            enumeration(EntityProperties.TROPICAL_FISH_PATTERN_COLOR, TropicalFish::getPatternColor, TropicalFish::setPatternColor),

            bool(EntityProperties.TURTLE_HAS_EGG, Turtle::hasEgg, Turtle::setHasEgg),

            bool(EntityProperties.VEX_CHARGING, Vex::isCharging, Vex::setCharging),

            property(EntityProperties.VILLAGER_PROFESSION, Villager::getProfession, Villager::setProfession, ArgumentTypes.resource(RegistryKey.VILLAGER_PROFESSION)),
            integer(EntityProperties.VILLAGER_VILLAGER_LEVEL, Villager::getVillagerLevel, Villager::setVillagerLevel),
            property(EntityProperties.VILLAGER_VILLAGER_TYPE, Villager::getVillagerType, Villager::setVillagerType, ArgumentTypes.resource(RegistryKey.VILLAGER_TYPE)),

            bool(EntityProperties.WITHER_SKULL_CHARGED, WitherSkull::isCharged, WitherSkull::setCharged),

            bool(EntityProperties.WOLF_ANGRY, Wolf::isAngry, Wolf::setAngry),
            bool(EntityProperties.WOLF_INTERESTED, Wolf::isInterested, Wolf::setInterested),
            property(EntityProperties.WOLF_SOUND_VARIANT, Wolf::getSoundVariant, Wolf::setSoundVariant, ArgumentTypes.resource(RegistryKey.WOLF_SOUND_VARIANT)),
            property(EntityProperties.WOLF_VARIANT, Wolf::getVariant, Wolf::setVariant, ArgumentTypes.resource(RegistryKey.WOLF_VARIANT)),

            bool(EntityProperties.ZOMBIE_SHOULD_BURN_IN_DAY, Zombie::shouldBurnInDay, Zombie::setShouldBurnInDay),

            property(EntityProperties.ZOMBIE_NAUTILUS_VARIANT, ZombieNautilus::getVariant, ZombieNautilus::setVariant, ArgumentTypes.resource(RegistryKey.ZOMBIE_NAUTILUS_VARIANT)),

            property(EntityProperties.ZOMBIE_VILLAGER_VILLAGER_PROFESSION, ZombieVillager::getVillagerProfession, ZombieVillager::setVillagerProfession, ArgumentTypes.resource(RegistryKey.VILLAGER_PROFESSION)),
            property(EntityProperties.ZOMBIE_VILLAGER_VILLAGER_TYPE, ZombieVillager::getVillagerType, ZombieVillager::setVillagerType, ArgumentTypes.resource(RegistryKey.VILLAGER_TYPE))
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
