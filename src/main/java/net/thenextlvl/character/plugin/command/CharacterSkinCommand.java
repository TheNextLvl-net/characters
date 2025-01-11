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

import java.util.Collection;

import static net.thenextlvl.character.plugin.command.CharacterCommand.playerCharacterArgument;

@NullMarked
class CharacterSkinCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("skin")
                .then(layer(plugin))
                .then(reset(plugin))
                .then(set(plugin));
    }


    private static ArgumentBuilder<CommandSourceStack, ?> layer(CharacterPlugin plugin) {
        return Commands.literal("layer")
                .then(layer("hide", plugin, false))
                .then(layer("show", plugin, true));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> layer(String name, CharacterPlugin plugin, boolean visible) {
        return Commands.literal(name).then(layerArgument(plugin).then(playerCharacterArgument(plugin)
                .executes(context -> layerToggle(context, plugin, visible))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> layerArgument(CharacterPlugin plugin) {
        return Commands.argument("layer", new EnumArgument<>(SkinLayer.class));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(CharacterPlugin plugin) {
        return Commands.literal("reset").then(playerCharacterArgument(plugin)); // todo implement
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(playerCharacterArgument(plugin)
                .then(fileSkinArgument(plugin))
                .then(playerSkinArgument(plugin))
                .then(urlSkinArgument(plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> fileSkinArgument(CharacterPlugin plugin) {
        return Commands.literal("file").then(Commands.argument("file", StringArgumentType.string())
                .executes(context -> setFileSkin(context, plugin)));
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
                .executes(context -> setUrlSkin(context, plugin)));
    }

    private static int setFileSkin(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        return Command.SINGLE_SUCCESS; // todo: implement
    }

    private static int setOfflinePlayerSkin(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var name = context.getArgument("offline-player", String.class);
        plugin.getServer().createProfile(name).update().thenAccept(profile ->
                setSkin(context, profile.getProperties(), plugin));
        return Command.SINGLE_SUCCESS;
    }

    private static int setPlayerSkin(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) throws CommandSyntaxException {
        var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        var player = resolver.resolve(context.getSource()).getFirst();
        return setSkin(context, player.getPlayerProfile().getProperties(), plugin);
    }

    private static int setUrlSkin(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        return Command.SINGLE_SUCCESS; // todo: implement
    }

    private static int layerToggle(CommandContext<CommandSourceStack> context, CharacterPlugin plugin, boolean visible) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("character", String.class);
        var character = plugin.characterController().getCharacter(name).orElse(null);

        if (character == null) {
            plugin.bundle().sendMessage(sender, "character.not_found", Placeholder.unparsed("name", name));
            return 0;
        }

        if (!(character instanceof PlayerCharacter player)) {
            plugin.bundle().sendMessage(sender, "character.not_player");
            return 0;
        }

        var layer = context.getArgument("layer", SkinLayer.class);
        var skinParts = plugin.characterProvider()
                .skinPartBuilder(player.getSkinParts())
                .toggle(layer, visible)
                .build();

        if (skinParts.equals(player.getSkinParts())) {
            plugin.bundle().sendMessage(sender, "nothing.changed");
            return 0;
        }

        player.setSkinParts(skinParts);

        var message = visible ? "character.skin_layer.shown" : "character.skin_layer.hidden";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.component("layer", Component.translatable(layer)),
                Placeholder.unparsed("character", name));

        return Command.SINGLE_SUCCESS;
    }

    private static int setSkin(CommandContext<CommandSourceStack> context, Collection<ProfileProperty> properties, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("character", String.class);
        var character = plugin.characterController().getCharacter(name).orElse(null);

        if (character == null) {
            plugin.bundle().sendMessage(sender, "character.not_found", Placeholder.unparsed("name", name));
            return 0;
        }

        if (!(character instanceof PlayerCharacter player)) {
            plugin.bundle().sendMessage(sender, "character.not_player");
            return 0;
        }

        player.getGameProfile().setProperties(properties);
        player.update();

         // todo: add result message

        return Command.SINGLE_SUCCESS;
    }
}
