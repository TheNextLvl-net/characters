package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.nameArgument;

@NullMarked
final class CharacterCreateCommand extends SimpleCommand {
    private CharacterCreateCommand(CharacterPlugin plugin) {
        super(plugin, "create", "characters.command.create");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterCreateCommand(plugin);
        return command.create().then(nameArgument(plugin)
                .then(typeArgument(plugin).executes(command))
                .executes(command));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, EntityType> typeArgument(CharacterPlugin plugin) {
        return Commands.argument("type", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("name", String.class);
        if (name.length() > 16) {
            plugin.bundle().sendMessage(sender, "character.name.too-long");
            return 0;
        } else if (plugin.characterController().characterExists(name)) {
            plugin.bundle().sendMessage(sender, "character.exists", Placeholder.unparsed("name", name));
            return 0;
        } else {
            var type = tryGetArgument(context, "type", EntityType.class).orElse(EntityType.PLAYER);
            plugin.characterController().spawnCharacter(name, context.getSource().getLocation(), type);
            plugin.bundle().sendMessage(sender, "character.created", Placeholder.unparsed("name", name),
                    Placeholder.unparsed("type", type.key().asString()));
            return SINGLE_SUCCESS;
        }
    }
}
