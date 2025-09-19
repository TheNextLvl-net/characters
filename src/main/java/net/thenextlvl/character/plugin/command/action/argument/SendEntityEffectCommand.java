package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.EntityEffect;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SendEntityEffectCommand extends CharacterActionCommand<EntityEffect> {
    private SendEntityEffectCommand(CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().sendEntityEffect(), "send-entity-effect");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new SendEntityEffectCommand(plugin);
        return command.create().then(entityEffectArgument().executes(command));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> entityEffectArgument() {
        return Commands.argument("entity-effect", EnumArgumentType.of(EntityEffect.class, EnumStringCodec.lowerHyphen()));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return addAction(context, context.getArgument("entity-effect", EntityEffect.class));
    }
}
