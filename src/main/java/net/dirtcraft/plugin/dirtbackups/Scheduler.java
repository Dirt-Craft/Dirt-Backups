package net.dirtcraft.plugin.dirtbackups;

import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class Scheduler {

    public static void initScheduler() {
        Task.builder()
                .async()
                .delay(1, TimeUnit.MINUTES)
                .interval(1, TimeUnit.MINUTES)
                .execute(Utility::doBackup)
                .submit(DirtBackups.getInstance());
    }

}
