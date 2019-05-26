package net.dirtcraft.plugin.dirtbackups;

import com.google.inject.Inject;
import net.dirtcraft.plugin.dirtbackups.Configuration.ConfigManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(
        id = "dirt-backups",
        name = "Dirt Backups",
        description = "Backups plugin, made for Sponge.",
        url = "https://dirtcraft.net/",
        authors = {
                "juliann"
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

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        loadConfig();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }

    private void loadConfig() {
        this.cfgManager = new ConfigManager(loader);
    }
}
