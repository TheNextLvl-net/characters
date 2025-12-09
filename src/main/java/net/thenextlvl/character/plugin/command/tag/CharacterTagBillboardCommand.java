package net.thenextlvl.character.plugin.command.tag;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import core.paper.brigadier.arguments.EnumArgumentType;
import core.paper.brigadier.arguments.codecs.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.bukkit.entity.Display.Billboard;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class CharacterTagBillboardCommand extends SimpleCommand {
    private CharacterTagBillboardCommand(CharacterPlugin plugin) {
        super(plugin, "billboard", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> set(CharacterPlugin plugin) {
        var command = new CharacterTagBillboardCommand(plugin);
        return command.create().then(Commands.argument(
                "billboard", EnumArgumentType.of(Billboard.class, EnumStringCodec.lowerHyphen())
        ).executes(command));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reset(CharacterPlugin plugin) {
        var command = new CharacterTagBillboardCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = context.getArgument("character", Character.class);
        var billboard = tryGetArgument(context, "billboard", Billboard.class).orElse(Billboard.CENTER);
        var success = character.getTagOptions().setBillboard(billboard);
        var message = success ? "character.tag.billboard" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("value", billboard.name().toLowerCase()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
