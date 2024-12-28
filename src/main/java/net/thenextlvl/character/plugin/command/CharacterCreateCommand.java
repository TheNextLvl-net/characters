package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
class CharacterCreateCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("create")
                .then(nameArgument(plugin).then(typeArgument(plugin)));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> nameArgument(CharacterPlugin plugin) {
        return Commands.argument("name", StringArgumentType.greedyString())
                .executes(context -> create(context, EntityType.PLAYER, plugin));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, EntityType> typeArgument(CharacterPlugin plugin) {
        return Commands.argument("type", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE))
                .executes(context -> {
                    var type = context.getArgument("type", EntityType.class);
                    return create(context, type, plugin);
                });
    }

    private static int create(CommandContext<CommandSourceStack> context, EntityType type, CharacterPlugin plugin) {
        var name = context.getArgument("name", String.class);
        if (plugin.characterController().characterExists(name)) {
            return 0;
        } else {
            var character = plugin.characterController().createCharacter(name, type);
            return Command.SINGLE_SUCCESS;
        }
    }
}
