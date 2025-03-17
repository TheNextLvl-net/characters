package net.thenextlvl.character.plugin.command;

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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import net.thenextlvl.character.skin.SkinLayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static net.thenextlvl.character.plugin.command.CharacterCommand.playerCharacterArgument;

@NullMarked
class CharacterSkinCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("skin")
                .requires(source -> source.getSender().hasPermission("characters.command.skin"))
                .then(layer(plugin))
                .then(reset(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> layer(CharacterPlugin plugin) {
        return Commands.literal("layer")
                .then(layer("hide", plugin, false))
                .then(layer("show", plugin, true));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(CharacterPlugin plugin) {
        return Commands.literal("reset").then(playerCharacterArgument(plugin)
                .executes(context -> setSkin(context, null, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(playerCharacterArgument(plugin)
                .then(fileSkinArgument(plugin))
                .then(playerSkinArgument(plugin))
                .then(urlSkinArgument(plugin)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> layer(String name, CharacterPlugin plugin, boolean visible) {
        return Commands.literal(name).then(layerArgument(plugin).then(playerCharacterArgument(plugin)
                .executes(context -> layerToggle(context, plugin, visible))));
    }

    private static int setSkin(CommandContext<CommandSourceStack> context, @Nullable ProfileProperty textures, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", PlayerCharacter.class);

        var success = textures == null ? character.clearTextures()
                : character.setTextures(textures.getValue(), textures.getSignature());
        var message = success ? "character.skin" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> fileSkinArgument(CharacterPlugin plugin) {
        return Commands.literal("file").then(Commands.argument("file", StringArgumentType.string())
                .then(Commands.literal("slim").executes(context -> setFileSkin(context, true, plugin)))
                .executes(context -> setFileSkin(context, false, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> playerSkinArgument(CharacterPlugin plugin) {
        return Commands.literal("player")
                .then(Commands.argument("offline-player", StringArgumentType.word())
                        .executes(context -> setOfflinePlayerSkin(context, plugin)))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(context -> setPlayerSkin(context, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> urlSkinArgument(CharacterPlugin plugin) {
        return Commands.literal("url").then(Commands.argument("url", StringArgumentType.string())
                .then(Commands.literal("slim").executes(context -> setUrlSkin(context, true, plugin)))
                .executes(context -> setUrlSkin(context, false, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> layerArgument(CharacterPlugin plugin) {
        return Commands.argument("layer", new EnumArgument<>(SkinLayer.class));
    }

    private static int layerToggle(CommandContext<CommandSourceStack> context, CharacterPlugin plugin, boolean visible) {
        var sender = context.getSource().getSender();
        var character = context.getArgument("character", PlayerCharacter.class);

        var layer = context.getArgument("layer", SkinLayer.class);
        var skinParts = plugin.skinFactory().skinPartBuilder()
                .parts(character.getSkinParts())
                .toggle(layer, visible).build();

        var success = character.setSkinParts(skinParts);
        var message = !success ? "nothing.changed" : visible
                ? "character.skin_layer.shown" : "character.skin_layer.hidden";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.component("layer", Component.translatable(layer)),
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private static int setFileSkin(CommandContext<CommandSourceStack> context, boolean slim, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var file = new File(context.getArgument("file", String.class));
        if (!file.isFile() || !file.getName().endsWith(".png")) {
            plugin.bundle().sendMessage(sender, "character.skin.file");
            return 0;
        }
        plugin.bundle().sendMessage(sender, "character.skin.generating");
        plugin.skinFactory().skinFromFile(file, slim)
                .thenAccept(textures -> setSkin(context, textures, plugin))
                .exceptionally(throwable -> {
                    plugin.bundle().sendMessage(sender, "character.skin.image");
                    return null;
                });
        return Command.SINGLE_SUCCESS;
    }

    private static int setOfflinePlayerSkin(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("offline-player", String.class);
        if (name.length() <= 16) {
            plugin.getServer().createProfile(name).update().thenAccept(profile -> profile.getProperties().stream()
                    .filter(property -> property.getName().equals("textures")).findAny()
                    .ifPresentOrElse(textures -> setSkin(context, textures, plugin), () -> {
                        if (profile.getName() == null) plugin.bundle().sendMessage(sender, "player.not_found",
                                Placeholder.unparsed("name", name));
                        else plugin.bundle().sendMessage(sender, "character.skin.not_found",
                                Placeholder.unparsed("player", profile.getName()));
                    }));
            return Command.SINGLE_SUCCESS;
        } else {
            plugin.bundle().sendMessage(sender, "character.name.too-long");
            return 0;
        }
    }

    private static int setPlayerSkin(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) throws CommandSyntaxException {
        var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        var player = resolver.resolve(context.getSource()).getFirst();
        var textures = player.getPlayerProfile().getProperties().stream()
                .filter(property -> property.getName().equals("textures"))
                .findAny().orElse(null);
        if (textures != null) return setSkin(context, textures, plugin);
        plugin.bundle().sendMessage(player, "character.skin.not_found",
                Placeholder.unparsed("player", player.getName()));
        return 0;
    }

    private static int setUrlSkin(CommandContext<CommandSourceStack> context, boolean slim, CharacterPlugin plugin) {
        try {
            var path = context.getArgument("url", String.class);
            var url = new URI(!path.startsWith("http") ? "https://" + path : path).toURL();
            plugin.bundle().sendMessage(context.getSource().getSender(), "character.skin.generating");
            plugin.skinFactory().skinFromURL(url, slim)
                    .thenAccept(textures -> setSkin(context, textures, plugin))
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
