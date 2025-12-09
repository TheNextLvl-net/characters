package net.thenextlvl.character.plugin.command.equipment;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import core.paper.brigadier.arguments.EnumArgumentType;
import core.paper.brigadier.arguments.codecs.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CharacterEquipmentCommand extends BrigadierCommand {
    private CharacterEquipmentCommand(CharacterPlugin plugin) {
        super(plugin, "equipment", "characters.command.equipment");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterEquipmentCommand(plugin);
        return command.create()
                .then(CharacterEquipmentClearCommand.create(plugin))
                .then(CharacterEquipmentSetCommand.create(plugin));
    }

    static ArgumentBuilder<CommandSourceStack, ?> equipmentSlotArgument() {
        return Commands.argument("equipment-slot", EnumArgumentType.of(EquipmentSlot.class, EnumStringCodec.lowerHyphen()));
    }
}
