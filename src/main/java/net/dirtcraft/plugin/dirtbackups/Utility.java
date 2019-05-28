package net.dirtcraft.plugin.dirtbackups;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class Utility {

    public static void doBackup(int numKeep) {
        Task.builder()
                .async()
                .execute(() -> {
                    try {
                        File world = Sponge.getGame().getSavesDirectory().resolve(Sponge.getServer().getDefaultWorldName()).toFile();
                        ZipFile backup = new ZipFile(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + File.separator + "backups" + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy-HH:mm:ss")) + ".zip");
                        backup.addFolder(world, new ZipParameters());
                        deleteBackups(numKeep);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .submit(DirtBackups.getInstance());
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
                    file.delete();
                }
            }
        }
    }

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

}
