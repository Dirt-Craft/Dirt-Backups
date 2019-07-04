package net.dirtcraft.plugin.dirtbackups;

import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class Scheduler {

    public static void initScheduler() {
        Task.builder()
                .async()
                .delay(PluginConfiguration.interval * 60, TimeUnit.MINUTES)
                .interval(PluginConfiguration.interval * 60, TimeUnit.MINUTES)
                .execute(Utility::doBackup)
                .submit(DirtBackups.getInstance());
    }

}
