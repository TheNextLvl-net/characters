package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagAlignmentCommand extends SimpleCommand {
    private CharacterTagAlignmentCommand(CharacterPlugin plugin) {
        super(plugin, "alignment", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagAlignmentCommand(plugin);
        return command.create().then(Commands.argument(
                "alignment", EnumArgumentType.of(TextAlignment.class, EnumStringCodec.lowerHyphen())
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagAlignmentCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var alignment = tryGetArgument(context, "alignment", TextAlignment.class).orElse(TextAlignment.CENTER);
        var success = character.getTagOptions().setAlignment(alignment);
        var message = success ? "character.tag.alignment" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", alignment.name().toLowerCase()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
