package net.thenextlvl.character.notification;

import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ClickAction;
import org.bukkit.entity.Player;

record SimpleNotificationType<T>(
        @KeyPattern.Value String name,
        Class<T> type,
        Sender<T> sender
) implements NotificationType<T> {
    public static final NotificationType<Sound> PLAY_SOUND = NotificationType.create("sound", Sound.class,
            (player, character, action, input) -> player.playSound(input));
    public static final NotificationType<String> MESSAGE = NotificationType.create("message", String.class,
            (player, character, action, input) -> player.sendMessage(MiniMessage.miniMessage()
                    .deserialize(input, placeholders(player, character, action))));
    public static final NotificationType<UnparsedTitle> TITLE = NotificationType.create("title", UnparsedTitle.class,
            (player, character, action, input) -> {
                final var miniMessage = MiniMessage.miniMessage();
                final var placeholders = placeholders(player, character, action);
                final var title = miniMessage.deserialize(input.title(), placeholders);
                final var subtitle = miniMessage.deserialize(input.subtitle(), placeholders);
                player.showTitle(Title.title(title, subtitle, input.times()));
            });
    public static final NotificationType<String> ACTIONBAR = NotificationType.create("actionbar", String.class,
            (player, character, action, input) -> player.sendActionBar(MiniMessage.miniMessage()
                    .deserialize(input, placeholders(player, character, action))));

    private static TagResolver[] placeholders(final Player player, final Character character, final ClickAction<?> action) {
        return new TagResolver[]{
                Formatter.number("cooldown", action.getCooldown().toMillis() / 1000d),
                Formatter.number("remaining-cooldown", action.getRemainingCooldown(player).toMillis() / 1000d),
                Placeholder.parsed("player", player.getName()),
                Placeholder.unparsed("character", character.getName()),
                Placeholder.parsed("permission", action.getPermission().orElse("undefined"))
        };
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public @KeyPattern.Value String getName() {
        return name;
    }

    @Override
    public Sender<T> getSender() {
        return sender;
    }
}
