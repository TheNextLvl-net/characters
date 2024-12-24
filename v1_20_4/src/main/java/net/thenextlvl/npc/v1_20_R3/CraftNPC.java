package net.thenextlvl.npc.v1_20_R3;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.thenextlvl.hologram.api.HologramProvider;
import net.thenextlvl.hologram.api.hologram.Hologram;
import net.thenextlvl.character.Interaction;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.equipment.Equipment;
import net.thenextlvl.character.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class CraftNPC implements Character {
    private static final @Nullable HologramProvider provider;

    static {
        var registration = Bukkit.getServicesManager().getRegistration(HologramProvider.class);
        if (registration == null) provider = null;
        else provider = registration.getProvider();
    }

    private final ServerPlayer player;
    private Location location;
    private PlayerProfile profile;
    private Component displayName;
    private Equipment equipment;
    private @Nullable Skin skin;
    private @Nullable Hologram nameTag;
    private @Nullable Interaction interaction;

    public CraftNPC(Location location, PlayerProfile profile, Component displayName, Equipment equipment, @Nullable Skin skin) {
        this.location = location;
        this.profile = profile;
        this.displayName = displayName;
        this.equipment = equipment;
        this.skin = skin;
        this.nameTag = provider != null ? provider.getHologramFactory().createHologram(
                location.clone().add(0, 2, 0),
                getDisplayName()
        ) : null;
        this.player = new ServerPlayer(
                MinecraftServer.getServer(),
                ((CraftWorld) location.getWorld()).getHandle(),
                ((CraftPlayerProfile) profile).getGameProfile(),
                ClientInformation.createDefault()
        );
    }

    @Override
    public int getEntityId() {
        return getPlayer().getId();
    }

    @Override
    public int getLoadingRange() {
        return ((CraftWorld) getLocation().getWorld()).getHandle().spigotConfig.displayTrackingRange;
    }

    @Override
    public Character clone() {
        try {
            var clone = (CraftNPC) super.clone();
            clone.profile = (PlayerProfile) profile.clone();
            clone.equipment = equipment.clone();
            clone.location = location.clone();
            clone.skin = skin != null ? skin.clone() : null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
