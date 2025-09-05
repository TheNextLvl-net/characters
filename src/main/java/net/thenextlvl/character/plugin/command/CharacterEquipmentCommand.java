package net.thenextlvl.character.plugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
class CharacterEquipmentCommand {
    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        return Commands.literal("equipment")
                .requires(source -> source.getSender().hasPermission("characters.command.equipment"))
                .then(clear(plugin))
                .then(set(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> equipmentSlotArgument() {
        return Commands.argument("equipment-slot", EnumArgumentType.of(EquipmentSlot.class, EnumStringCodec.lowerHyphen()));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> itemArgument() {
        return Commands.argument("item", ArgumentTypes.itemStack());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> clear(CharacterPlugin plugin) {
        return Commands.literal("clear").then(characterArgument(plugin)
                .executes(context -> clearEquipment(context, plugin))
                .then(equipmentSlotArgument().executes(context ->
                        clearSlot(context, plugin))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(CharacterPlugin plugin) {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(equipmentSlotArgument().then(itemArgument()
                        .executes(context -> setSlot(context, plugin)))));
    }

    private static int clearEquipment(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var success = character.getEquipment().clear();
        var message = success ? "character.equipment.cleared" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static int clearSlot(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var slot = context.getArgument("equipment-slot", EquipmentSlot.class);
        var success = character.getEquipment().setItem(slot, null, false);
        var message = success ? "character.equipment.slot.cleared" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("slot", slot.name().toLowerCase().replace("_", "-")));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static int setSlot(CommandContext<CommandSourceStack> context, CharacterPlugin plugin) {
        var character = context.getArgument("character", Character.class);
        var slot = context.getArgument("equipment-slot", EquipmentSlot.class);
        var item = context.getArgument("item", ItemStack.class);
        var success = character.getEquipment().setItem(slot, item, true);
        var message = !success ? "nothing.changed" : item.isEmpty()
                ? "character.equipment.slot.cleared" : "character.equipment.slot";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.component("item", item.effectiveName().hoverEvent(item.asHoverEvent())),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("slot", slot.name().toLowerCase().replace("_", "-")));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
