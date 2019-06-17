package net.dirtcraft.plugin.dirtbackups;

import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class Scheduler {

    public static void initScheduler() {
        Task.builder()
                .async()
                .execute(Utility::doBackup)
                .interval(PluginConfiguration.interval * 60, TimeUnit.MINUTES)
                .submit(DirtBackups.getInstance());
    }

}
