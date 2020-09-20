package net.dirtcraft.plugin.dirtbackups;

import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.lang3.SystemUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Utility {

    public static void deleteOtherBackupMods() {
        Path gameDirectory = Sponge.getGame().getGameDirectory();
        File modsDir = new File(gameDirectory.toFile(), "mods");
        File[] mods = modsDir.listFiles();
        if (mods == null) return;
        for (File mod : mods) {
            String modName = mod.getName().toLowerCase();
            if (!modName.contains("backup")) continue;
            if (modName.contains("dirt")) continue;
            if (!mod.delete()) return;
            DirtBackups.getLogger().warn("Dirt Backups detected another backup mod. Removing the mod and restarting the server now!");
            Sponge.getServer().shutdown(Utility.format("&6Dirt Backups&7 detected another backup mod. &cRemoving&7 the mod and restarting the server now!"));
        }
    }

    public static String getFormat() {
        String format = "MM-dd-yyyy-HH:mm:ss";
        if (SystemUtils.IS_OS_WINDOWS) format = format.replace(":", "_");
        return format;
    }

    public static void doBackup() {
        if (DirtBackups.isBackingUp) {
            DirtBackups.getLogger().error("Already backing up!");
            return;
        }
        try {
            DirtBackups.getLogger().warn("Starting backup...");
            DirtBackups.isBackingUp = true;
            File world = Sponge.getGame().getSavesDirectory().resolve(Sponge.getServer().getDefaultWorldName()).toFile();

            ZipFile backup = new ZipFile(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + File.separator + "backups" + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern(getFormat())) + ".zip");
            backup.addFolder(world, new ZipParameters());
            deleteBackups(PluginConfiguration.quantity);
        } catch (IOException | ZipException exception) {
            exception.printStackTrace();
        }
        DirtBackups.getLogger().warn("Backup Complete!");
        DirtBackups.isBackingUp = false;
    }

    public static List<File> listBackups() throws IOException {
        File backupDir = new File(Sponge.getGame().getGameDirectory().toFile(), "backups");
        File[] files = backupDir.listFiles();
        if (files == null) return new ArrayList<>();
        for (File file : files) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(Utility.getFormat());
            try {
                LocalDateTime.from(format.parse(file.getName().replace(".zip", "")));
            } catch (DateTimeParseException exception) {
                file.delete();
            }
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return Arrays.asList(files);
    }

    private static void deleteBackups(int numKeep) throws IOException {
        List<File> files = listBackups();
        int counter = 0;
        files.sort(Comparator.comparingLong(File::lastModified).reversed());
        for (File file : files) {
            counter++;
            if (counter > numKeep) {
                DirtBackups.getLogger().warn("Warning! Deleting oldest backup...");
                if (file.delete()) DirtBackups.getLogger().warn("Oldest backup successfully deleted.");
            }
        }
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

}
