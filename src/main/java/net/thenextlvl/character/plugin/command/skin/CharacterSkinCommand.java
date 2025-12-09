package net.thenextlvl.character.plugin.command.skin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.bukkit.entity.Mannequin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static net.thenextlvl.character.plugin.command.CharacterCommand.mannequinCharacterArgument;

@NullMarked
public final class CharacterSkinCommand extends BrigadierCommand {
    private CharacterSkinCommand(CharacterPlugin plugin) {
        super(plugin, "skin", "characters.command.skin");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(CharacterPlugin plugin) {
        var command = new CharacterSkinCommand(plugin);
        return command.create()
                .then(CharacterSkinLayerCommand.create(plugin))
                .then(command.reset())
                .then(command.set());
    }

    private ArgumentBuilder<CommandSourceStack, ?> reset() {
        return Commands.literal("reset").then(mannequinCharacterArgument(plugin)
                .executes(context -> setSkin(context, null)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set").then(mannequinCharacterArgument(plugin)
                .then(fileSkinArgument())
                .then(playerSkinArgument())
                .then(urlSkinArgument()));
    }

    @SuppressWarnings("unchecked")
    private int setSkin(CommandContext<CommandSourceStack> context, @Nullable ResolvableProfile profile) {
        var sender = context.getSource().getSender();
        var character = (Character<@NonNull Mannequin>) context.getArgument("character", Character.class);

        var success = character.getEntity().map(mannequin -> {
            var newProfile = profile != null ? profile : ResolvableProfile.resolvableProfile().name(character.getName()).build();
            if (mannequin.getProfile().equals(newProfile)) return false;
            mannequin.setProfile(newProfile);
            return true;
        }).orElse(false);
        var message = success ? "character.skin" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.unparsed("character", character.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private ArgumentBuilder<CommandSourceStack, ?> fileSkinArgument() {
        return Commands.literal("file").then(Commands.argument("file", StringArgumentType.string())
                .then(Commands.literal("slim").executes(context -> setFileSkin(context, true)))
                .executes(context -> setFileSkin(context, false)));
    }

    private ArgumentBuilder<CommandSourceStack, ?> playerSkinArgument() {
        return Commands.literal("player")
                .then(Commands.argument("offline-player", StringArgumentType.word())
                        .executes(this::setOfflinePlayerSkin))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(this::setPlayerSkin));
    }

    private ArgumentBuilder<CommandSourceStack, ?> urlSkinArgument() {
        return Commands.literal("url").then(Commands.argument("url", StringArgumentType.string())
                .then(Commands.literal("slim").executes(context -> setUrlSkin(context, true)))
                .executes(context -> setUrlSkin(context, false)));
    }

    private int setFileSkin(CommandContext<CommandSourceStack> context, boolean slim) {
        var sender = context.getSource().getSender();
        var file = new File(context.getArgument("file", String.class));
        if (!file.isFile() || !file.getName().endsWith(".png")) {
            plugin.bundle().sendMessage(sender, "character.skin.file");
            return 0;
        }
        plugin.bundle().sendMessage(sender, "character.skin.generating");
        plugin.skinFactory().skinFromFile(file, slim)
                .thenAccept(textures -> setSkin(context, ResolvableProfile.resolvableProfile().addProperty(textures).build()))
                .exceptionally(throwable -> {
                    plugin.bundle().sendMessage(sender, "character.skin.image");
                    return null;
                });
        return Command.SINGLE_SUCCESS;
    }

    private int setOfflinePlayerSkin(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var name = context.getArgument("offline-player", String.class);
        if (name.length() <= 16) {
            return setSkin(context, ResolvableProfile.resolvableProfile().name(name).build());
        } else {
            plugin.bundle().sendMessage(sender, "player.not_found", Placeholder.unparsed("name", name));
            return 0;
        }
    }

    private int setPlayerSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        var player = resolver.resolve(context.getSource()).getFirst();
        return setSkin(context, ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
    }

    private int setUrlSkin(CommandContext<CommandSourceStack> context, boolean slim) {
        try {
            var path = context.getArgument("url", String.class);
            var url = new URI(!path.startsWith("http") ? "https://" + path : path).toURL();
            plugin.bundle().sendMessage(context.getSource().getSender(), "character.skin.generating");
            plugin.skinFactory().skinFromURL(url, slim)
                    .thenAccept(textures -> setSkin(context, ResolvableProfile.resolvableProfile().addProperty(textures).build()))
                    .exceptionally(throwable -> {
                        plugin.bundle().sendMessage(context.getSource().getSender(), "character.skin.image");
                        return null;
                    });
            return Command.SINGLE_SUCCESS;
        } catch (MalformedURLException | URISyntaxException e) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "character.skin.url");
            return 0;
        }
    }
}
