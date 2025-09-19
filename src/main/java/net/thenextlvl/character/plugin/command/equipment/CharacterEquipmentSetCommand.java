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
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;
import static net.thenextlvl.character.plugin.command.equipment.CharacterEquipmentCommand.equipmentSlotArgument;

@NullMarked
final class CharacterEquipmentSetCommand extends SimpleCommand {
    private CharacterEquipmentSetCommand(CharacterPlugin plugin) {
        super(plugin, "set", null);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterEquipmentSetCommand(plugin);
        return command.create().then(characterArgument(plugin)
                .then(equipmentSlotArgument().then(Commands.argument(
                        "item", ArgumentTypes.itemStack()
                ).executes(command))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var character = (net.thenextlvl.character.Character<?>) context.getArgument("character", Character.class);
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
        return success ? SINGLE_SUCCESS : 0;
    }
}
