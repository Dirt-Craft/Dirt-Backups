package net.dirtcraft.plugin.dirtbackups;

import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class Scheduler {

    public Scheduler() {
        Task.builder()
                .execute(() -> {
                    DirtBackups.isBackingUp = true;
                    Utility.doBackup(PluginConfiguration.quantity);
                    DirtBackups.isBackingUp = false;
                })
                .interval(PluginConfiguration.interval, TimeUnit.MINUTES)
                .submit(DirtBackups.getInstance());
    }

}
