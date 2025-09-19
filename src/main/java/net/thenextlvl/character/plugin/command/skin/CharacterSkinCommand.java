package net.thenextlvl.character.plugin.command.skin;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static net.thenextlvl.character.plugin.command.CharacterCommand.playerCharacterArgument;

@NullMarked
public final class CharacterSkinCommand extends BrigadierCommand {
    private CharacterSkinCommand(CharacterPlugin plugin) {
        super(plugin, "skin", "characters.command.skin");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterSkinCommand(plugin);
        return command.create()
                .then(CharacterSkinLayerCommand.create(plugin))
                .then(command.reset())
                .then(command.set());
    }

    private ArgumentBuilder<CommandSourceStack, ?> reset() {
        return Commands.literal("reset").then(playerCharacterArgument(plugin)
                .executes(context -> setSkin(context, null)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set").then(playerCharacterArgument(plugin)
                .then(fileSkinArgument())
                .then(playerSkinArgument())
                .then(urlSkinArgument()));
    }

    private int setSkin(CommandContext<CommandSourceStack> context, @Nullable ProfileProperty textures) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", PlayerCharacter.class);

        var success = textures == null ? character.clearTextures()
                : character.setTextures(textures.getValue(), textures.getSignature());
        var message = success ? "character.skin" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> fileSkinArgument() {
        return Commands.literal("file").then(Commands.argument("file", StringArgumentType.string())
                .then(Commands.literal("slim").executes(context -> setFileSkin(context, true)))
                .executes(context -> setFileSkin(context, false)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> playerSkinArgument() {
        return Commands.literal("player")
                .then(Commands.argument("offline-player", StringArgumentType.word())
                        .executes(this::setOfflinePlayerSkin))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(this::setPlayerSkin));
    }

    private ArgumentBuilder<CommandSourceStack, ?> urlSkinArgument() {
        return Commands.literal("url").then(Commands.argument("url", StringArgumentType.string())
                .then(Commands.literal("slim").executes(context -> setUrlSkin(context, true)))
                .executes(context -> setUrlSkin(context, false)));
    }

    private int setFileSkin(CommandContext<CommandSourceStack> context, boolean slim) {
        var sender = context.getSource().getSender();
        var file = new File(context.getArgument("file", String.class));
        if (!file.isFile() || !file.getName().endsWith(".png")) {
            plugin.bundle().sendMessage(sender, "character.skin.file");
            return 0;
        }
        plugin.bundle().sendMessage(sender, "character.skin.generating");
        plugin.skinFactory().skinFromFile(file, slim)
                .thenAccept(textures -> setSkin(context, textures))
                .exceptionally(throwable -> {
                    plugin.bundle().sendMessage(sender, "character.skin.image");
                    return null;
                });
        return Command.SINGLE_SUCCESS;
    }

    private int setOfflinePlayerSkin(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("offline-player", String.class);
        if (name.length() <= 16) {
            plugin.getServer().createProfile(name).update().thenAccept(profile -> profile.getProperties().stream()
                    .filter(property -> property.getName().equals("textures")).findAny()
                    .ifPresentOrElse(textures -> setSkin(context, textures), () -> {
                        if (profile.getName() == null) plugin.bundle().sendMessage(sender, "player.not_found",
                                Placeholder.unparsed("name", name));
                        else plugin.bundle().sendMessage(sender, "character.skin.not_found",
                                Placeholder.unparsed("player", profile.getName()));
                    }));
            return Command.SINGLE_SUCCESS;
        } else {
            plugin.bundle().sendMessage(sender, "player.not_found", Placeholder.unparsed("name", name));
            return 0;
        }
    }

    private int setPlayerSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        var player = resolver.resolve(context.getSource()).getFirst();
        var textures = player.getPlayerProfile().getProperties().stream()
                .filter(property -> property.getName().equals("textures"))
                .findAny().orElse(null);
        if (textures != null) return setSkin(context, textures);
        plugin.bundle().sendMessage(player, "character.skin.not_found",
                Placeholder.unparsed("player", player.getName()));
        return 0;
    }

    private int setUrlSkin(CommandContext<CommandSourceStack> context, boolean slim) {
        try {
            var path = context.getArgument("url", String.class);
            var url = new URI(!path.startsWith("http") ? "https://" + path : path).toURL();
            plugin.bundle().sendMessage(context.getSource().getSender(), "character.skin.generating");
            plugin.skinFactory().skinFromURL(url, slim)
                    .thenAccept(textures -> setSkin(context, textures))
                    .exceptionally(throwable -> {
                        plugin.bundle().sendMessage(context.getSource().getSender(), "character.skin.image");
                        return null;
                    });
            return Command.SINGLE_SUCCESS;
        } catch (MalformedURLException | URISyntaxException e) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "character.skin.url");
            return 0;
        }
    }
}
