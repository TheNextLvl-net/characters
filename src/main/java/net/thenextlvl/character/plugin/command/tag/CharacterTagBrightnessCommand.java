package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagBrightnessCommand extends SimpleCommand {
    private CharacterTagBrightnessCommand(CharacterPlugin plugin) {
        super(plugin, "brightness", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagBrightnessCommand(plugin);
        return command.create().then(Commands.argument(
                "block-light", IntegerArgumentType.integer(0, 15)
        ).then(Commands.argument(
                "sky-light", IntegerArgumentType.integer(0, 15)
        ).executes(command)));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagBrightnessCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var blockLight = tryGetArgument(context, "block-light", int.class).orElse(null);
        var skyLight = tryGetArgument(context, "sky-light", int.class).orElse(null);
        var brightness = blockLight != null && skyLight != null ? new Display.Brightness(blockLight, skyLight) : null;
        var success = character.getTagOptions().setBrightness(brightness);
        var message = success ? "character.tag.brightness" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("block_light", String.valueOf(blockLight)),
                Placeholder.unparsed("sky_light", String.valueOf(skyLight)),
                Placeholder.unparsed("character", character.getName()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
