package net.thenextlvl.character.plugin.command.equipment;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.SimpleCommand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import net.thenextlvl.character.property.EntityProperties;
import net.thenextlvl.character.property.EquipmentSnapshot;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.equipment.CharacterEquipmentCommand.equipmentSlotArgument;

@NullMarked
final class CharacterEquipmentSetCommand extends SimpleCommand {
    private CharacterEquipmentSetCommand(final CharacterPlugin plugin) {
        super(plugin, "set", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterEquipmentSetCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .then(equipmentSlotArgument().then(Commands.argument(
                        "item", ArgumentTypes.itemStack()
                ).executes(command))));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var character = context.getArgument("character", Character.class);
        final var slot = context.getArgument("equipment-slot", EquipmentSlot.class);
        final var item = context.getArgument("item", ItemStack.class);
        final var success = character.setProperty(EntityProperties.LIVING_ENTITY_EQUIPMENT, character.getProperty(EntityProperties.LIVING_ENTITY_EQUIPMENT)
                .orElseGet(EquipmentSnapshot::empty).with(slot, item));
        final var message = !success ? "nothing.changed" : item.isEmpty()
                ? "character.equipment.slot.cleared" : "character.equipment.slot";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.component("item", item.effectiveName().hoverEvent(item.asHoverEvent())),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("slot", slot.name().toLowerCase().replace("_", "-")));
        return success ? SINGLE_SUCCESS : 0;
    }
}
