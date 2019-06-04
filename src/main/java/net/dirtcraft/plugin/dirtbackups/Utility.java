package net.dirtcraft.plugin.dirtbackups;

import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class Utility {

    public static void doBackup() {
        try {
            if (DirtBackups.isBackingUp) {
                DirtBackups.getLogger().error("Already backing up!");
                return;
            }
            DirtBackups.getLogger().warn("Starting backup...");
            DirtBackups.isBackingUp = true;
            File world = Sponge.getGame().getSavesDirectory().resolve(Sponge.getServer().getDefaultWorldName()).toFile();
            ZipFile backup = new ZipFile(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + File.separator + "backups" + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy-HH:mm:ss")) + ".zip");
            backup.addFolder(world, new ZipParameters());
            deleteBackups(PluginConfiguration.quantity);
        } catch (IOException | ZipException exception) {
            exception.printStackTrace();
        }
        DirtBackups.getLogger().warn("Backup Complete!");
        DirtBackups.isBackingUp = false;
    }

    public static File[] listBackups() throws IOException {
        File backupDir = new File(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + File.separator + "backups");
        File[] files = backupDir.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files;
    }

    private static void deleteBackups(int numKeep) throws IOException {
        File backupDir = new File(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + File.separator + "backups");
        if (backupDir.listFiles() != null) {
            int counter = 0;
            File[] files = backupDir.listFiles();
            if (files == null) return;
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            for (File file : files) {
                counter++;
                if (counter > numKeep) {
                    DirtBackups.getLogger().warn("Warning! Deleting oldest backup...");
                    file.delete();
                }
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
