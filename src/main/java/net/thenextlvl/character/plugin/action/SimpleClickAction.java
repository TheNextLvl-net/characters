package net.thenextlvl.character.plugin.action;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.action.ActionType;
import net.thenextlvl.character.action.ClickAction;
import net.thenextlvl.character.action.ClickType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@NullMarked
final class SimpleClickAction<T> implements ClickAction<T> {
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    private final ActionType<T> actionType;
    private volatile EnumSet<ClickType> clickTypes;
    private volatile T input;

    private volatile @Nullable String permission = null;
    private volatile @Range(from = 0, to = 100) int chance = 100;
    private volatile Duration cooldown = Duration.ZERO;
    private volatile @Nullable String cooldownMessage = null;
    private volatile @Nullable Sound cooldownSound = null;

    public SimpleClickAction(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input) {
        this.actionType = actionType;
        this.clickTypes = EnumSet.copyOf(clickTypes);
        this.input = input;
    }

    @Override
    public ActionType<T> getActionType() {
        return actionType;
    }

    @Override
    public EnumSet<ClickType> getClickTypes() {
        return clickTypes.clone();
    }

    @Override
    public boolean setClickTypes(final EnumSet<ClickType> clickTypes) {
        if (Objects.equals(this.clickTypes, clickTypes)) return false;
        this.clickTypes = clickTypes.clone();
        return true;
    }

    @Override
    public boolean isSupportedClickType(final ClickType type) {
        return clickTypes.contains(type);
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public boolean setInput(final T input) {
        if (Objects.equals(this.input, input)) return false;
        this.input = input;
        return true;
    }

    @Override
    public @Range(from = 0, to = 100) int getChance() {
        return chance;
    }

    @Override
    public boolean setChance(@Range(from = 0, to = 100) final int chance) {
        if (this.chance == chance) return false;
        this.chance = Math.clamp(chance, 0, 100);
        return true;
    }

    @Override
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    @Override
    public boolean setPermission(@Nullable final String permission) {
        if (Objects.equals(this.permission, permission)) return false;
        this.permission = permission;
        return true;
    }

    @Override
    public Duration getCooldown() {
        return cooldown;
    }

    @Override
    public boolean setCooldown(final Duration cooldown) {
        if (Objects.equals(this.cooldown, cooldown)) return false;
        this.cooldown = cooldown;
        return true;
    }

    @Override
    public boolean isOnCooldown(final Player player) {
        return cooldown.isPositive() && cooldowns.computeIfPresent(player.getUniqueId(), (ignored, lastUsed) -> {
            if (System.currentTimeMillis() - cooldown.toMillis() > lastUsed) return null;
            return lastUsed;
        }) != null;
    }

    @Override
    public Duration getRemainingCooldown(final Player player) {
        final var lastUsed = cooldowns.get(player.getUniqueId());
        if (lastUsed == null || !cooldown.isPositive()) return Duration.ZERO;
        final var remaining = lastUsed + cooldown.toMillis() - System.currentTimeMillis();
        return remaining > 0 ? Duration.ofMillis(remaining) : Duration.ZERO;
    }

    @Override
    public Optional<String> getCooldownMessage() {
        return Optional.ofNullable(cooldownMessage);
    }

    @Override
    public boolean setCooldownMessage(@Nullable final String message) {
        if (Objects.equals(this.cooldownMessage, message)) return false;
        this.cooldownMessage = message;
        return true;
    }

    @Override
    public Optional<Sound> getCooldownSound() {
        return Optional.ofNullable(cooldownSound);
    }

    @Override
    public boolean setCooldownSound(@Nullable final Sound sound) {
        if (Objects.equals(this.cooldownSound, sound)) return false;
        this.cooldownSound = sound;
        return true;
    }

    // tells the player the action is on cooldown, with the remaining seconds
    private void sendCooldownFeedback(final Player player) {
        final var message = cooldownMessage;
        if (message != null) {
            final var seconds = getRemainingCooldown(player).toMillis() / 1000d;
            player.sendActionBar(MiniMessage.miniMessage().deserialize(message,
                    Formatter.number("cooldown", seconds),
                    Placeholder.parsed("player", player.getName())));
        }
        final var sound = cooldownSound;
        if (sound != null) player.playSound(sound);
    }

    @Override
    public boolean resetCooldown(final Player player) {
        return cooldowns.remove(player.getUniqueId()) != null;
    }

    @Override
    public boolean invoke(final Character character, final Player player) {
        final var permission1 = permission;
        if (permission1 != null && !player.hasPermission(permission1)) return false;
        if (isOnCooldown(player)) {
            sendCooldownFeedback(player);
            return false;
        }
        if (cooldown.isPositive()) cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        if (ThreadLocalRandom.current().nextInt(100) >= chance) return false;
        actionType.action().invoke(character, player, input);
        return true;
    }

    @Override
    public ClickAction<T> copyFrom(final ClickAction<T> other) {
        clickTypes = other.getClickTypes();
        input = other.getInput();
        chance = other.getChance();
        cooldown = other.getCooldown();
        permission = other.getPermission().orElse(null);
        cooldownMessage = other.getCooldownMessage().orElse(null);
        cooldownSound = other.getCooldownSound().orElse(null);
        return this;
    }
}
