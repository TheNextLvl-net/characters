package net.thenextlvl.character.plugin.action;

import com.google.common.io.ByteStreams;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN;

@NullMarked
public final class SimpleActionTypes implements ActionTypes {
    private final ActionType<String> sendActionbar = ActionType.builder("send_actionbar", String.class)
            .action((character, player, input) -> {
                var placeholder = Placeholder.parsed("player", player.getName());
                var message = MiniMessage.miniMessage().deserialize(input, placeholder);
                player.sendActionBar(message);
            })
            .build();

    private final ActionType<String> sendMessage = ActionType.builder("send_message", String.class)
            .action((character, player, input) -> {
                var placeholder = Placeholder.parsed("player", player.getName());
                player.sendMessage(MiniMessage.miniMessage().deserialize(input, placeholder));
            })
            .build();

    private final ActionType<EntityEffect> sendEntityEffect = ActionType.builder("send_entity_effect", EntityEffect.class)
            .action((character, player, effect) -> character.getEntity().ifPresent(entity -> player.sendEntityEffect(effect, entity)))
            .applicable((effect, character) -> effect.isApplicableTo(character.getEntityClass()) && !isDeprecated(effect))
            .build();

    private final ActionType<InetSocketAddress> transfer = ActionType.builder("transfer", InetSocketAddress.class)
            .action((character, player, address) -> player.transfer(address.getHostName(), address.getPort()))
            .build();

    private final ActionType<Location> teleport = ActionType.builder("teleport", Location.class)
            .action((character, player, location) -> player.teleportAsync(location, PLUGIN))
            .build();

    private final ActionType<Sound> playSound = ActionType.builder("play_sound", Sound.class)
            .action((character, player, sound) -> player.playSound(sound))
            .build();

    private final ActionType<String> runConsoleCommand = ActionType.builder("run_console_command", String.class)
            .action((character, player, input) -> {
                var command = input.replace("<player>", player.getName());
                player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command);
            })
            .build();

    private final ActionType<String> runCommand = ActionType.builder("run_command", String.class)
            .action((character, player, input) -> player.performCommand(input.replace("<player>", player.getName())))
            .build();

    private final ActionType<Title> sendTitle = ActionType.builder("send_title", Title.class)
            .action((character, player, title) -> player.showTitle(title))
            .build();

    private final ActionType<String> connect;

    public SimpleActionTypes(final CharacterPlugin plugin) {
        this.connect = ActionType.builder("connect", String.class)
                .action((character, player, server) -> {
                    var dataOutput = ByteStreams.newDataOutput();
                    dataOutput.writeUTF("Connect");
                    dataOutput.writeUTF(server);
                    player.sendPluginMessage(plugin, "BungeeCord", dataOutput.toByteArray());
                })
                .build();
    }

    @Override
    public ActionType<String> sendActionbar() {
        return sendActionbar;
    }

    @Override
    public ActionType<String> sendMessage() {
        return sendMessage;
    }

    @Override
    public ActionType<EntityEffect> sendEntityEffect() {
        return sendEntityEffect;
    }

    @Override
    public ActionType<InetSocketAddress> transfer() {
        return transfer;
    }

    @Override
    public ActionType<Location> teleport() {
        return teleport;
    }

    @Override
    public ActionType<Sound> playSound() {
        return playSound;
    }

    @Override
    public ActionType<String> runConsoleCommand() {
        return runConsoleCommand;
    }

    @Override
    public ActionType<String> runCommand() {
        return runCommand;
    }

    @Override
    public ActionType<Title> sendTitle() {
        return sendTitle;
    }

    @Override
    public ActionType<String> connect() {
        return connect;
    }

    private boolean isDeprecated(final Enum<?> anEnum) {
        try {
            return anEnum.getDeclaringClass().getField(anEnum.name()).isAnnotationPresent(Deprecated.class);
        } catch (final NoSuchFieldException e) {
            return false;
        }
    }
}
