package net.thenextlvl.character.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@NullMarked
final class SimpleClickAction<T> implements ClickAction<T> {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private final ActionType<T> actionType;
    private EnumSet<ClickType> clickTypes;
    private T input;

    private @Range(from = 0, to = 100) int chance = 100;
    private Duration cooldown = Duration.ZERO;
    private @Nullable String permission = null;

    public SimpleClickAction(ActionType<T> actionType, EnumSet<ClickType> clickTypes, T input) {
        this.actionType = actionType;
        this.clickTypes = clickTypes;
        this.input = input;
    }

    @Override
    public ActionType<T> getActionType() {
        return actionType;
    }

    @Override
    public EnumSet<ClickType> getClickTypes() {
        return clickTypes;
    }

    @Override
    public boolean setClickTypes(EnumSet<ClickType> clickTypes) {
        if (Objects.equals(this.clickTypes, clickTypes)) return false;
        this.clickTypes = clickTypes;
        return true;
    }

    @Override
    public boolean isSupportedClickType(ClickType type) {
        return clickTypes.contains(type);
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public boolean setInput(T input) {
        if (Objects.equals(this.input, input)) return false;
        this.input = input;
        return true;
    }

    @Override
    public @Range(from = 0, to = 100) int getChance() {
        return chance;
    }

    @Override
    public boolean setChance(@Range(from = 0, to = 100) int chance) {
        if (this.chance == chance) return false;
        this.chance = Math.clamp(chance, 0, 100);
        return true;
    }

    @Override
    public @Nullable String getPermission() {
        return permission;
    }

    @Override
    public boolean setPermission(@Nullable String permission) {
        if (Objects.equals(this.permission, permission)) return false;
        this.permission = permission;
        return true;
    }

    @Override
    public Duration getCooldown() {
        return cooldown;
    }

    @Override
    public boolean setCooldown(Duration cooldown) {
        if (Objects.equals(this.cooldown, cooldown)) return false;
        this.cooldown = cooldown;
        return true;
    }

    @Override
    public boolean isOnCooldown(Player player) {
        return cooldown.isPositive() && cooldowns.computeIfPresent(player.getUniqueId(), (ignored, lastUsed) -> {
            if (System.currentTimeMillis() - cooldown.toMillis() > lastUsed) return null;
            return lastUsed;
        }) != null;
    }

    @Override
    public boolean resetCooldown(Player player) {
        return cooldowns.remove(player.getUniqueId()) != null;
    }

    @Override
    public boolean canInvoke(Player player) {
        return (permission == null || player.hasPermission(permission)) && !isOnCooldown(player);
    }

    @Override
    public boolean invoke(Player player, Entity character) {
        if (!canInvoke(player)) return false;
        if (cooldown.isPositive()) cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        if (ThreadLocalRandom.current().nextInt(100) > chance) return false;
        actionType.action().invoke(player, character, input);
        return true;
    }
}
