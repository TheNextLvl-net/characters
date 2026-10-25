package net.thenextlvl.character.action;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import net.thenextlvl.binder.StaticBinder;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

import java.net.InetSocketAddress;

/**
 * Represents a collection of action types that can be performed on a character.
 *
 * @since 0.5.0
 */
@ApiStatus.Internal
public interface ActionTypes {
    /**
     * Gets the action types.
     *
     * @return action types
     * @since 0.5.0
     */
    static @CheckReturnValue ActionTypes types() {
        return StaticBinder.getInstance(ActionTypes.class.getClassLoader()).find(ActionTypes.class);
    }

    /**
     * Gets the action type for sending an action bar.
     *
     * @return action type for sending an action bar
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<String> sendActionbar();

    /**
     * Gets the action type for sending a message.
     *
     * @return action type for sending a message
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<String> sendMessage();

    /**
     * Gets the action type for sending an entity effect.
     *
     * @return action type for sending an entity effect
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<EntityEffect> sendEntityEffect();

    /**
     * Gets the action type for transferring a player.
     *
     * @return action type for transferring a player
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<InetSocketAddress> transfer();

    /**
     * Gets the action type for teleporting a player.
     *
     * @return action type for teleporting a player
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<Location> teleport();

    /**
     * Gets the action type for playing a sound.
     *
     * @return action type for playing a sound
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<Sound> playSound();

    /**
     * Gets the action type for running a console command.
     *
     * @return action type for running a console command
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<String> runConsoleCommand();

    /**
     * Gets the action type for running a command as the player.
     *
     * @return action type for running a command as the player
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<String> runCommand();

    /**
     * Gets the action type for sending a title.
     *
     * @return action type for sending a title
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<Title> sendTitle();

    /**
     * Gets the action type for connecting to a server.
     *
     * @return action type for connecting to a server
     * @since 0.5.0
     */
    @Contract(pure = true)
    ActionType<String> connect();
}
