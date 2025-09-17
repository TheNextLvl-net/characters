package net.thenextlvl.character.plugin.command.skin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.PlayerCharacter;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.skin.SkinLayer;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.playerCharacterArgument;

@NullMarked
class CharacterSkinLayerCommand extends BrigadierCommand {
    private CharacterSkinLayerCommand(CharacterPlugin plugin) {
        super(plugin, "layer", "characters.command.skin.layer");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterSkinLayerCommand(plugin);
        return command.create()
                .then(command.layer("hide", false))
                .then(command.layer("show", true));
    }

    private LiteralArgumentBuilder<CommandSourceStack> layer(String name, boolean visible) {
        return Commands.literal(name).then(layerArgument().then(playerCharacterArgument(plugin)
                .executes(context -> toggle(context, visible))));
    }

    private ArgumentBuilder<CommandSourceStack, ?> layerArgument() {
        return Commands.argument("layer", EnumArgumentType.of(SkinLayer.class, EnumStringCodec.lowerHyphen()));
    }

    private int toggle(CommandContext<CommandSourceStack> context, boolean visible) {
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
}
