package net.thenextlvl.character.action;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

/**
 *
 * @since 0.5.0
 */
@NullMarked
public sealed interface ActionTypes permits SimpleActionTypes {
    @Contract(pure = true)
    static ActionTypes types() {
        return SimpleActionTypes.INSTANCE;
    }

    @Contract(pure = true)
    ActionType<String> sendActionbar();

    @Contract(pure = true)
    ActionType<String> sendMessage();

    @Contract(pure = true)
    ActionType<EntityEffect> sendEntityEffect();

    @Contract(pure = true)
    ActionType<InetSocketAddress> transfer();

    @Contract(pure = true)
    ActionType<Location> teleport();

    @Contract(pure = true)
    ActionType<Sound> playSound();

    @Contract(pure = true)
    ActionType<String> runConsoleCommand();

    @Contract(pure = true)
    ActionType<String> runCommand();

    @Contract(pure = true)
    ActionType<Title> sendTitle();

    @Contract(pure = true)
    ActionType<String> connect();
}
