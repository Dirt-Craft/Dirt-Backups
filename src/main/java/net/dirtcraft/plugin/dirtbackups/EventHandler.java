package net.dirtcraft.plugin.dirtbackups;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;

public class EventHandler {

    public EventHandler() {
        DirtBackups.getLogger().info("Event Listeners registered for " + DirtBackups.getContainer().getName());
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        Scheduler.initScheduler();
    }

    @Listener
    public void onSaveWorld(SaveWorldEvent.Pre event) {
        if (DirtBackups.isBackingUp) {
            event.setCancelled(true);
            DirtBackups.getLogger().warn("Server is backing up! Level saving is disabled.");
        }
    }

}
