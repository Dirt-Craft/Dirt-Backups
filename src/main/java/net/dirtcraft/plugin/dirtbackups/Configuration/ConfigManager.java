package net.dirtcraft.plugin.dirtbackups.Configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

public class ConfigManager {
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationOptions options;
    private PluginConfiguration config;

    public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
        options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
        update();
    }

    private void update() {
        try {
            CommentedConfigurationNode node = loader.load(options);
            PluginConfiguration config = node.getValue(TypeToken.of(PluginConfiguration.class), new PluginConfiguration());
            loader.save(node);
            this.config = config;
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }
}