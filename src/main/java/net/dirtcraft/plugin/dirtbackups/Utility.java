package net.dirtcraft.plugin.dirtbackups;

import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
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
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utility {

    public static DateTimeFormatter format = DateTimeFormatter.ofPattern(Utility.getFormat());

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
            Optional<File> optionalLatestBackup = getLatestBackup();
            if (optionalLatestBackup.isPresent()) {
                File latestBackup = optionalLatestBackup.get();
                
                Date now = new Date();
                Date backupLastModified = new Date(latestBackup.lastModified());
                long difference = now.getTime() - backupLastModified.getTime();

                if (difference < (PluginConfiguration.interval * 3600000)) return;
            }
            DirtBackups.getLogger().warn("Starting backup...");
            DirtBackups.isBackingUp = true;

            // Delete previous backups if over quantity
            deleteBackups(PluginConfiguration.quantity);

            // Create new backup
            File world = Sponge.getGame().getSavesDirectory().resolve(Sponge.getServer().getDefaultWorldName()).toFile();

            ZipFile backup = new ZipFile(Sponge.getGame().getGameDirectory().toFile().getCanonicalPath() + File.separator + "backups" + File.separator + LocalDateTime.now().format(Utility.format) + ".zip");
            backup.addFolder(world, new ZipParameters());
        } catch (IOException | ZipException exception) {
            exception.printStackTrace();
        }
        DirtBackups.getLogger().warn("Backup Complete!");
        DirtBackups.isBackingUp = false;
    }

    private static Optional<File> getLatestBackup() throws IOException {
        List<File> backups = listBackups();
        if (backups.size() == 0) return Optional.empty();
        List<LocalDateTime> times = new ArrayList<>();
        for (File file : backups) times.add(LocalDateTime.from(format.parse(file.getName().replace(".zip", ""))));
        File latestBackup = backups.get(times.indexOf(Collections.max(times)));
        return Optional.of(latestBackup);
    }

    public static List<File> listBackups() throws IOException {
        File backupDir = new File(Sponge.getGame().getGameDirectory().toFile(), "backups");
        File[] files = backupDir.listFiles();
        if (files == null) return new ArrayList<>();
        for (File file : files) {
            DateTimeFormatter format = Utility.format;
            try {
                LocalDateTime.from(format.parse(file.getName().replace(".zip", "")));
            } catch (DateTimeParseException exception) {
                deleteRecursively(file);
            }
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return Arrays.asList(files);
    }

    private static void deleteBackups(int numKeep) throws IOException {
        List<File> files = listBackups();
        int counter = 1;
        files.sort(Comparator.comparingLong(File::lastModified).reversed());
        for (File file : files) {
            counter++;
            if (counter > numKeep) {
                DirtBackups.getLogger().warn("Warning! Deleting oldest backup...");
                deleteRecursively(file);
            }
        }
    }

    private static void deleteRecursively(File file) {
        if (file.delete()) return;
        if (!file.isDirectory()) return;
        File[] files = file.listFiles();
        if (files == null) return;
        for (File file1 : files)
            if (!file1.delete() && file1.isDirectory()) deleteRecursively(file1);
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
