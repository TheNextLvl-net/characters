package net.thenextlvl.character.plugin.command.skin;

import com.destroystokyo.paper.PaperSkinParts;
import com.destroystokyo.paper.SkinParts;
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
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.model.SkinLayer;
import org.bukkit.entity.Mannequin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.mannequinCharacterArgument;

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
        return Commands.literal(name).then(layerArgument().then(mannequinCharacterArgument(plugin)
                .executes(context -> toggle(context, visible))));
    }

    private ArgumentBuilder<CommandSourceStack, ?> layerArgument() {
        return Commands.argument("layer", EnumArgumentType.of(SkinLayer.class, EnumStringCodec.lowerHyphen()));
    }

    @SuppressWarnings("unchecked")
    private int toggle(CommandContext<CommandSourceStack> context, boolean visible) {
        var character = (Character<@NonNull Mannequin>) context.getArgument("character", Character.class);

        var layer = context.getArgument("layer", SkinLayer.class);
        var raw = character.getEntity().map(Mannequin::getSkinParts).orElseGet(SkinParts::allParts).getRaw();
        var skinParts = new PaperSkinParts(visible ? raw | layer.getMask() : raw & ~layer.getMask());

        var success = character.getEntity().map(mannequin -> {
            if (mannequin.getSkinParts().getRaw() == skinParts.getRaw()) return false;
            mannequin.setSkinParts(skinParts);
            return true;
        }).orElse(false);
        var message = !success ? "nothing.changed" : visible
                ? "character.skin_layer.shown" : "character.skin_layer.hidden";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.component("layer", Component.translatable(layer)),
                Placeholder.unparsed("character", character.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
