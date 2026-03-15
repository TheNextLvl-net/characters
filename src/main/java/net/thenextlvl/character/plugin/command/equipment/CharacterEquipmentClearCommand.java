package net.thenextlvl.character.plugin.command.equipment;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.equipment.CharacterEquipmentCommand.equipmentSlotArgument;

@NullMarked
final class CharacterEquipmentClearCommand extends BrigadierCommand {
    private CharacterEquipmentClearCommand(final CharacterPlugin plugin) {
        super(plugin, "clear", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterEquipmentClearCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .then(equipmentSlotArgument().executes(command::clearSlot))
                .executes(command::clearEquipment));
    }

    private int clearEquipment(final CommandContext<CommandSourceStack> context) {
        final var character = (Character<?>) context.getArgument("character", Character.class);
        final var success = character.getEntity(LivingEntity.class).map(LivingEntity::getEquipment).map(equipment -> {
            equipment.clear();
            return true;
        }).orElse(false);
        final var message = success ? "character.equipment.cleared" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private int clearSlot(final CommandContext<CommandSourceStack> context) {
        final var character = (Character<?>) context.getArgument("character", Character.class);
        final var slot = context.getArgument("equipment-slot", EquipmentSlot.class);
        final var success = character.getEntity(LivingEntity.class).map(LivingEntity::getEquipment).map(equipment -> {
            equipment.setItem(slot, null, true);
            return true;
        }).orElse(false);
        final var message = success ? "character.equipment.slot.cleared" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("slot", slot.name().toLowerCase().replace("_", "-")));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
