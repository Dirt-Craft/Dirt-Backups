package net.dirtcraft.plugin.dirtbackups.Configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class PluginConfiguration {

    @Setting(value = "Interval", comment = "The frequency that backups are taken at (in minutes)")
    public static int interval = 120;

    @Setting(value = "Quantity", comment = "The number of backups to keep.")
    public static int quantity = 7;

}
