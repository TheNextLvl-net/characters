package net.thenextlvl.character;

import net.thenextlvl.character.controller.PaperCharacterController;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterPlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 24223);

    private final CharacterController characterController = new PaperCharacterController();

    @Override
    public void onEnable() {
//        var provider = getNPCProvider();
//        Bukkit.getServicesManager().register(CharacterProvider.class, provider, this, ServicePriority.Normal);
//        Bukkit.getPluginManager().registerEvents(new NPCListener(this, provider), this);
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
    }

    public CharacterController characterController() {
        return characterController;
    }
}
