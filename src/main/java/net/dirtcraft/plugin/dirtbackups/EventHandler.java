package net.dirtcraft.plugin.dirtbackups;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;

public class EventHandler {

    public EventHandler() {
        DirtBackups.getLogger().info("Event Listeners registered for " + DirtBackups.getContainer().getName());
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        Scheduler.initScheduler();
    }

}
