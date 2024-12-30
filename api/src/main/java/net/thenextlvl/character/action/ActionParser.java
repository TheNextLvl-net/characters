package net.thenextlvl.character.action;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;

import java.net.InetSocketAddress;

@SuppressWarnings("PatternValidation")
public interface ActionParser<T> {
    ActionParser<InetSocketAddress> ADDRESS = input -> {
        var split = input.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    };
    ActionParser<Component> COMPONENT = input -> MiniMessage.miniMessage().deserialize(input);
    ActionParser<Location> LOCATION = input -> new Location(null, 0, 0, 0); // todo: add world, x, y, z, yaw and pitch
    ActionParser<Title> TITLE = input -> Title.title(Component.empty(), Component.empty()); // todo: add title and times
    ActionParser<Sound> SOUND = input -> Sound.sound(Key.key(input), Sound.Source.MASTER, 1f, 1f); // todo add sound source volume and pitch
    ActionParser<String> IDENTITY = input -> input;

    static <T extends Enum<T>> ActionParser<T> forEnum(Class<T> type) {
        return input -> Enum.valueOf(type, input.toUpperCase());
    }

    T parse(String input);
}
