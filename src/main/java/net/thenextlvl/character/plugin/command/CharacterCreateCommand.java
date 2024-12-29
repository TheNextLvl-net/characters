package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.nameArgument;

@NullMarked
class CharacterCreateCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var type = typeArgument(plugin).executes(context -> {
            var entityType = context.getArgument("type", EntityType.class);
            return create(context, entityType, plugin);
        });
        var create = nameArgument(plugin).executes(context ->
                create(context, EntityType.PLAYER, plugin)
        ).then(type);
        return Commands.literal("create").then(create);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, EntityType> typeArgument(CharacterPlugin plugin) {
        return Commands.argument("type", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE));
    }

    private static int create(CommandContext<CommandSourceStack> context, EntityType type, CharacterPlugin plugin) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("name", String.class);
        if (name.length() > 16) {
            plugin.bundle().sendMessage(sender, "character.name.too-long");
            return 0;
        } else if (plugin.characterController().characterExists(name)) {
            plugin.bundle().sendMessage(sender, "character.exists", Placeholder.unparsed("name", name));
            return 0;
        } else {
            plugin.characterController().spawnCharacter(name, context.getSource().getLocation(), type);
            plugin.bundle().sendMessage(sender, "character.created", Placeholder.unparsed("name", name),
                    Placeholder.unparsed("type", type.key().asString()));
            return Command.SINGLE_SUCCESS;
        }
    }
}
