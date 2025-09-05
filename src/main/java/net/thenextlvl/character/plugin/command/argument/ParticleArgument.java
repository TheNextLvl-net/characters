package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class ParticleArgument implements CustomArgumentType.Converted<Particle, Particle> {
    @Override
    public Particle convert(Particle nativeType) {
        if (nativeType.getDataType().equals(Void.class)) return nativeType;
        throw new IllegalArgumentException("This particle is not allowed");
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        RegistryAccess.registryAccess().getRegistry(RegistryKey.PARTICLE_TYPE).stream()
                .filter(particle -> particle.getDataType().equals(Void.class))
                .map(Particle::getKey)
                .map(Key::asString)
                .filter(key -> key.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<Particle> getNativeType() {
        return ArgumentTypes.resource(RegistryKey.PARTICLE_TYPE);
    }
}
