package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.argument.EnumArgument;
import net.thenextlvl.character.skin.SkinLayer;
import org.jspecify.annotations.NullMarked;

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
        return Commands.literal("reset").then(playerCharacterArgument(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(playerCharacterArgument(plugin));
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
}
