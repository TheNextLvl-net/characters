package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NullMarked
public final class ColorArgumentType implements CustomArgumentType.Converted<Color, String> {
    private static final Map<String, Color> predefined = Map.ofEntries(
            Map.entry("aqua", Color.AQUA),
            Map.entry("black", Color.BLACK),
            Map.entry("blue", Color.BLUE),
            Map.entry("fuchsia", Color.FUCHSIA),
            Map.entry("gray", Color.GRAY),
            Map.entry("green", Color.GREEN),
            Map.entry("lime", Color.LIME),
            Map.entry("maroon", Color.MAROON),
            Map.entry("navy", Color.NAVY),
            Map.entry("orange", Color.ORANGE),
            Map.entry("olive", Color.OLIVE),
            Map.entry("purple", Color.PURPLE),
            Map.entry("red", Color.RED),
            Map.entry("silver", Color.SILVER),
            Map.entry("teal", Color.TEAL),
            Map.entry("white", Color.WHITE),
            Map.entry("yellow", Color.YELLOW)
    );

    @Override
    public Color convert(String nativeType) {
        var color = predefined.get(nativeType);
        if (color != null) return color;

        var argb = Long.decode("0x" + nativeType);
        return Color.fromARGB(argb.intValue());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        predefined.keySet().stream()
                .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
