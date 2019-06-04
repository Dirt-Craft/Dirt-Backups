package net.dirtcraft.plugin.dirtbackups.Configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class PluginConfiguration {

    @Setting(value = "Interval", comment = "The frequency that backups are taken at (in minutes)")
    public static int interval = 4;

    @Setting(value = "Quantity", comment = "The number of backups to keep before deleting old ones.")
    public static int quantity = 6;

}
