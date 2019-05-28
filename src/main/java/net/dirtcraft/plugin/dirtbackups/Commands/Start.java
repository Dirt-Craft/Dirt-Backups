package net.dirtcraft.plugin.dirtbackups.Commands;

import net.dirtcraft.plugin.dirtbackups.Configuration.PluginConfiguration;
import net.dirtcraft.plugin.dirtbackups.DirtBackups;
import net.dirtcraft.plugin.dirtbackups.Utility;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;

public class Start implements CommandExecutor {

    private final int numKeep;

    public Start(int numKeep) {
        this.numKeep = numKeep;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        try {
            if (Utility.listBackups().length >= PluginConfiguration.quantity) {
                source.sendMessage(Utility.format("&cWarning! &7Deleting oldest backup..."));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new CommandException(Utility.format("&cThere was an error getting backups!"));
        }

        source.sendMessage(Utility.format("&7Saving backup..."));

        DirtBackups.isBackingUp = true;
        Utility.doBackup(numKeep);
        DirtBackups.isBackingUp = false;

        source.sendMessage(Utility.format("&7Backup &asuccessfully&7 saved!"));
        return CommandResult.success();
    }
}