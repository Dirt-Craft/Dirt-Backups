package net.dirtcraft.plugin.dirtbackups.Configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class PluginConfiguration {
    @Setting(value = "Main")
    private PluginConfiguration.Main main = new PluginConfiguration.Main();

    @ConfigSerializable
    public static class Main {
        @Setting(value = "ArrayList")
        public static ArrayList<String> test = new ArrayList<String>() {{
            add("Hello");
        }};

        @Setting(value = "String")
        public static String string = "Test";

        @Setting(value = "Integer")
        public static int integer = 0;

        @Setting(value = "Boolean")
        public static boolean testBoolean = true;

    }

}
