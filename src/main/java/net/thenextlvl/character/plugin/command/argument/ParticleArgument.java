package net.thenextlvl.character.plugin.command.argument;

import core.paper.command.WrappedArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;

public class ParticleArgument extends WrappedArgumentType<Particle, Particle> {
    public ParticleArgument() {
        super(ArgumentTypes.resource(RegistryKey.PARTICLE_TYPE), (reader, type) -> {
            if (type.getDataType().equals(Void.class)) return type;
            throw new IllegalArgumentException("This particle is not allowed");
        }, (context, builder) -> {
            RegistryAccess.registryAccess().getRegistry(RegistryKey.PARTICLE_TYPE).stream()
                    .filter(particle -> particle.getDataType().equals(Void.class))
                    .map(Particle::getKey)
                    .map(Key::asString)
                    .filter(key -> key.toLowerCase().contains(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
