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
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

// todo: split up into multiple commands
@NullMarked
final class CharacterEquipmentCommand extends BrigadierCommand {
    private CharacterEquipmentCommand(CharacterPlugin plugin) {
        super(plugin, "equipment", "characters.command.equipment");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterEquipmentCommand(plugin);
        return command.create()
                .then(command.clear())
                .then(command.set());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> equipmentSlotArgument() {
        return Commands.argument("equipment-slot", EnumArgumentType.of(EquipmentSlot.class, EnumStringCodec.lowerHyphen()));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> itemArgument() {
        return Commands.argument("item", ArgumentTypes.itemStack());
    }

    private ArgumentBuilder<CommandSourceStack, ?> clear() {
        return Commands.literal("clear").then(characterArgument(plugin)
                .executes(this::clearEquipment)
                .then(equipmentSlotArgument().executes(this::clearSlot)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set").then(characterArgument(plugin)
                .then(equipmentSlotArgument().then(itemArgument()
                        .executes(this::setSlot))));
    }

    private int clearEquipment(CommandContext<CommandSourceStack> context) {
        var character = (Character<?>) context.getArgument("character", Character.class);
        var success = character.getEntity(LivingEntity.class).map(LivingEntity::getEquipment).map(equipment -> {
            equipment.clear();
            return true;
        }).orElse(false);
        var message = success ? "character.equipment.cleared" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private int clearSlot(CommandContext<CommandSourceStack> context) {
        var character = (Character<?>) context.getArgument("character", Character.class);
        var slot = context.getArgument("equipment-slot", EquipmentSlot.class);
        var success = character.getEntity(LivingEntity.class).map(LivingEntity::getEquipment).map(equipment -> {
            equipment.setItem(slot, null, true);
            return true;
        }).orElse(false);
        var message = success ? "character.equipment.slot.cleared" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("slot", slot.name().toLowerCase().replace("_", "-")));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private int setSlot(CommandContext<CommandSourceStack> context) {
        var character = (Character<?>) context.getArgument("character", Character.class);
        var slot = context.getArgument("equipment-slot", EquipmentSlot.class);
        var item = context.getArgument("item", ItemStack.class);
        var success = character.getEntity(LivingEntity.class).map(LivingEntity::getEquipment).map(equipment -> {
            equipment.setItem(slot, item, true);
            return true;
        }).orElse(false);
        var message = !success ? "nothing.changed" : item.isEmpty()
                ? "character.equipment.slot.cleared" : "character.equipment.slot";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.component("item", item.effectiveName().hoverEvent(item.asHoverEvent())),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("slot", slot.name().toLowerCase().replace("_", "-")));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
