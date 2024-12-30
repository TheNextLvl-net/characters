package net.thenextlvl.character.action;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

@NullMarked
public record ActionType<T>(Key key, ActionParser<T> parser, BiConsumer<Player, T> consumer) implements Keyed {
    public static ActionType<Component> ACTIONBAR = new ActionType<>(Key.key("action", "send_actionbar"), ActionParser.COMPONENT, Audience::sendActionBar);
    public static ActionType<Component> MESSAGE = new ActionType<>(Key.key("action", "send_message"), ActionParser.COMPONENT, Audience::sendMessage);
    public static ActionType<Location> TELEPORT = new ActionType<>(Key.key("action", "teleport"), ActionParser.LOCATION, Entity::teleportAsync);
    public static ActionType<Sound> SOUND = new ActionType<>(Key.key("action", "play_sound"), ActionParser.SOUND, Audience::playSound);
    // public static ActionType<String> CONNECT = new ActionType<>(Key.key("action", "connect"), ActionParser.IDENTITY, ); // todo: bungee connect
    public static ActionType<String> CONSOLE_COMMAND = new ActionType<>(Key.key("action", "run_console_command"), ActionParser.IDENTITY,
            (player, command) -> player.getServer().dispatchCommand(player, command));
    public static ActionType<String> PLAYER_COMMAND = new ActionType<>(Key.key("action", "run_command"), ActionParser.IDENTITY, Player::performCommand);
    public static ActionType<InetSocketAddress> TRANSFER = new ActionType<>(Key.key("action", "transfer"), ActionParser.ADDRESS,
            (player, address) -> player.transfer(address.getHostName(), address.getPort()));
    public static ActionType<Title> TITLE = new ActionType<>(Key.key("action", "send_title"), ActionParser.TITLE, Audience::showTitle);
}
