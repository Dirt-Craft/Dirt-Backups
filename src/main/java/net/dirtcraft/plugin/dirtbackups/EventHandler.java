package net.dirtcraft.plugin.dirtbackups;

import org.spongepowered.api.Sponge;
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
    public void onPreSaveWorld(SaveWorldEvent.Pre event) {
        if (DirtBackups.isBackingUp) {
            event.setCancelled(true);
            DirtBackups.getLogger().warn("Server is backing up! Level saving is disabled.");
        } else if (Sponge.getServer().getDefaultWorld().isPresent() &&
                event.getTargetWorld().getUniqueId().equals(
                        Sponge.getServer().getDefaultWorld().get().getUniqueId())) {
            Sponge.getServer().getBroadcastChannel().send(
                    Utility.format(
                            "&7&oThe world is saving! Expect a short lag spike..."));
        }
    }

    @Listener
    public void onPostWorldSave(SaveWorldEvent.Post event) {
        if (!DirtBackups.isBackingUp) return;
        if (Sponge.getServer().getDefaultWorld().isPresent() &&
                event.getTargetWorld().getUniqueId().equals(
                        Sponge.getServer().getDefaultWorld().get().getUniqueId()))
        Sponge.getServer().getBroadcastChannel().send(
                Utility.format("&7&oWorld save complete!"));
    }

}
