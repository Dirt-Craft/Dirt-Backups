package net.dirtcraft.plugin.dirtbackups;

import com.google.inject.Inject;
import net.dirtcraft.plugin.dirtbackups.Configuration.ConfigManager;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "dirt-backups",
        name = "Dirt Backups",
        description = "Backups plugin, made for Sponge.",
        url = "https://dirtcraft.net/",
        authors = {
                "juliann",
                "TechDweebGaming"
        }
)
public class DirtBackups {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigManager cfgManager;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    private DirtBackups instance;

    private Task backupTask;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        loadConfig();

        try {
            File backupDir = new File(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + "\\backups");
            if(!backupDir.exists()) backupDir.mkdir();

            CommandSpec backupCommand = CommandSpec.builder()
                    .description(Text.of("Forces the server to save a backup"))
                    .permission("dirtcraft.backups.forcebackup")
                    .executor(new BackupCommand(loader.load().getNode("Quantity").getInt()))
                    .build();
            Sponge.getCommandManager().register(instance, backupCommand, "backup", "forcebackup");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        try {
            int quantity = loader.load().getNode("Quantity").getInt();
            backupTask = Task.builder()
                    .execute(task -> doBackup(quantity))
                    .interval(new Long(loader.load().getNode("Interval").getInt()), TimeUnit.MINUTES)
                    .async()
                    .submit(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        this.cfgManager = new ConfigManager(loader);
    }

    public static void doBackup(int numKeep) {
        try {
            File world = Sponge.getGame().getSavesDirectory().resolve("world").toFile();
            ZipFile backup = new ZipFile(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + "\\backups" + "\\" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm")) + ".zip");
            backup.addFolder(world, new ZipParameters());
            deleteBackups(numKeep);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteBackups(int numKeep) throws IOException  {
        File backupDir = new File(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + "\\backups");
        if(backupDir.listFiles() != null) {
            int counter = 0;
            File[] files = backupDir.listFiles();
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            for(File file : files) {
                counter++;
                if(counter > numKeep) {
                    file.delete();
                }
            }
        }
    }
}
