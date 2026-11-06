package net.thenextlvl.character.plugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import net.kyori.adventure.key.Key;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DamageType;
import org.jspecify.annotations.NullMarked;

/**
 * Registers the damage type of attacks by characters that act like players.
 */
@NullMarked
public final class CharacterBootstrap implements PluginBootstrap {
    /**
     * The attack of a mannequin, like vanilla's player attack, its damage does not scale with the difficulty.
     * <p>
     * Vanilla only skips the scaling for attacks caused by players,
     * mannequins are no players, so their attacks need a damage type that never scales.
     */
    public static final TypedKey<DamageType> MANNEQUIN_ATTACK = TypedKey.create(RegistryKey.DAMAGE_TYPE, Key.key("characters", "mannequin_attack"));

    @Override
    public void bootstrap(final BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DAMAGE_TYPE.compose().newHandler(event -> {
            // vanilla's player attack, death messages read "slain by" the mannequin
            event.registry().register(MANNEQUIN_ATTACK, builder -> builder
                    .messageId("player")
                    .exhaustion(0.1f)
                    .damageScaling(DamageScaling.NEVER));
        }));
    }
}
