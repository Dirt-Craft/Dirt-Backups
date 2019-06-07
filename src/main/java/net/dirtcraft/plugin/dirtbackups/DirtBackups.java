package net.dirtcraft.plugin.dirtbackups;

import com.google.inject.Inject;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dirtcraft.plugin.dirtbackups.Commands.Start;
import net.dirtcraft.plugin.dirtbackups.Commands.List;
import net.dirtcraft.plugin.dirtbackups.Configuration.ConfigManager;
import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;

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
    private ConfigManager configManager;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    private static DirtBackups instance;

    public static boolean isBackingUp = false;

    @Listener (order = Order.LAST)
    public void onPreInit(GamePreInitializationEvent event) {

        instance = this;
        //loadConfig();
        if (SpongeDiscordLib.getServerName().equalsIgnoreCase("stoneblock 1")) return;

        try {
            File backupDir = new File(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + File.separator + "backups");
            if(!backupDir.exists()) backupDir.mkdir();

            CommandSpec list = CommandSpec.builder()
                    .description(Text.of("Lists all backups in directory"))
                    .permission(container.getId().replace("-", "") + ".list")
                    .executor(new List())
                    .build();

            CommandSpec start = CommandSpec.builder()
                    .description(Text.of("Forces the server to save a backup"))
                    .permission(container.getId().replace("-", "") + ".start")
                    .executor(new Start())
                    .build();

            CommandSpec base = CommandSpec.builder()
                    .description(Text.of("Base command for " + container.getName()))
                    .child(list, "list")
                    .child(start, "start")
                    .build();

            Sponge.getCommandManager().register(instance, base, "backup");
            Sponge.getCommandManager().register(instance, list, "backups");

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Sponge.getEventManager().registerListeners(instance, new EventHandler());

    }

    private void loadConfig() {
        this.configManager = new ConfigManager(loader);
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    public static PluginContainer getContainer() {
        return instance.container;
    }

    public static DirtBackups getInstance() {
        return instance;
    }
}
