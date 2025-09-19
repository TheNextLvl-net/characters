package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SendActionbarCommand extends CharacterStringActionCommand {
    private SendActionbarCommand(CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().sendActionbar(), "send-actionbar", "message");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return new SendActionbarCommand(plugin).create();
    }
}
