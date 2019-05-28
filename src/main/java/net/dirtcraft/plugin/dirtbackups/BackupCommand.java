package net.dirtcraft.plugin.dirtbackups;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class BackupCommand implements CommandExecutor {

    private int numKeep;

    public BackupCommand(int numKeep) { this.numKeep = numKeep; }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.of("Saving Backup..."));
        DirtBackups.doBackup(numKeep);
        src.sendMessage(Text.of("Backup Saved!"));
        return CommandResult.success();
    }
}
