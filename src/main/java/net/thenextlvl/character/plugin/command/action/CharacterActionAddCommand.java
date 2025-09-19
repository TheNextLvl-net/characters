package net.thenextlvl.character.plugin.command.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.action.argument.ConnectCommand;
import net.thenextlvl.character.plugin.command.action.argument.PlaySoundCommand;
import net.thenextlvl.character.plugin.command.action.argument.RunConsoleCommand;
import net.thenextlvl.character.plugin.command.action.argument.RunPlayerCommand;
import net.thenextlvl.character.plugin.command.action.argument.SendActionbarCommand;
import net.thenextlvl.character.plugin.command.action.argument.SendEntityEffectCommand;
import net.thenextlvl.character.plugin.command.action.argument.SendMessageCommand;
import net.thenextlvl.character.plugin.command.action.argument.SendTitleCommand;
import net.thenextlvl.character.plugin.command.action.argument.TeleportCommand;
import net.thenextlvl.character.plugin.command.action.argument.TransferCommand;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import net.thenextlvl.character.plugin.model.ClickTypes;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.action.CharacterActionCommand.actionArgument;

@NullMarked
public final class CharacterActionAddCommand extends BrigadierCommand {
    private CharacterActionAddCommand(CharacterPlugin plugin) {
        super(plugin, "add", "characters.command.action.add");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterActionAddCommand(plugin);
        var commands = clickTypesArgument()
                .then(ConnectCommand.create(plugin))
                .then(PlaySoundCommand.create(plugin))
                .then(RunConsoleCommand.create(plugin))
                .then(RunPlayerCommand.create(plugin))
                .then(SendActionbarCommand.create(plugin))
                .then(SendEntityEffectCommand.create(plugin))
                .then(SendMessageCommand.create(plugin))
                .then(SendTitleCommand.create(plugin))
                .then(TeleportCommand.create(plugin))
                .then(TransferCommand.create(plugin));
        return command.create().then(characterArgument(plugin)
                .then(actionArgument(plugin).then(commands)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> clickTypesArgument() {
        return Commands.argument("click-types", EnumArgumentType.of(ClickTypes.class, EnumStringCodec.lowerHyphen()));
    }
}
