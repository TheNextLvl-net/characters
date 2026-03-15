package net.thenextlvl.character.plugin.command.action.argument;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import core.paper.brigadier.arguments.EnumArgumentType;
import core.paper.brigadier.arguments.codecs.EnumStringCodec;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.RegistryArgumentExtractor;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.sound.Sound;
import net.thenextlvl.character.action.ActionTypes;
import net.thenextlvl.character.plugin.CharacterPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlaySoundCommand extends CharacterActionCommand<Sound> {
    private PlaySoundCommand(final CharacterPlugin plugin) {
        super(plugin, ActionTypes.types().playSound(), "play-sound");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new PlaySoundCommand(plugin);
        final var sound = soundArgument().executes(command);
        final var soundSource = soundSourceArgument().executes(command);
        final var volume = Commands.argument("volume", FloatArgumentType.floatArg(0)).executes(command);
        final var pitch = Commands.argument("pitch", FloatArgumentType.floatArg(0, 2)).executes(command);
        return command.create().then(sound.then(soundSource.then(volume.then(pitch))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> soundArgument() {
        return Commands.argument("sound", ArgumentTypes.resourceKey(RegistryKey.SOUND_EVENT));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> soundSourceArgument() {
        return Commands.argument("sound-source", EnumArgumentType.of(Sound.Source.class, EnumStringCodec.lowerHyphen()));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var source = tryGetArgument(context, "source", Sound.Source.class).orElse(Sound.Source.MASTER);
        final var volume = tryGetArgument(context, "volume", float.class).orElse(1f);
        final var pitch = tryGetArgument(context, "pitch", float.class).orElse(1f);
        final var sound = RegistryArgumentExtractor.getTypedKey(context, RegistryKey.SOUND_EVENT, "sound");
        return addAction(context, Sound.sound(sound, source, volume, pitch));
    }
}
