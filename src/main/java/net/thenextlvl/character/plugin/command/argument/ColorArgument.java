package net.thenextlvl.character.plugin.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import org.bukkit.Color;

import java.util.Map;

public class ColorArgument extends WrappedArgumentType<String, Color> {
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

    public ColorArgument() {
        super(StringArgumentType.word(), (reader, type) -> {
            var predefined = ColorArgument.predefined.get(type);
            if (predefined != null) return predefined;
            int argb = Integer.decode("0x" + type);
            return Color.fromRGB(argb);
        }, (context, builder) -> {
            ColorArgument.predefined.keySet().stream()
                    .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
