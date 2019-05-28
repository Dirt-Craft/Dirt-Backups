package net.dirtcraft.plugin.dirtbackups.Commands;

import net.dirtcraft.plugin.dirtbackups.Utility;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;

public class List implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        try {
            ArrayList<Text> contents = new ArrayList<>();
            for (File file : Utility.listBackups()) {
                String[] backupDate = file.getName().split("-");

                String[] backupTime = backupDate[3].split(":");

                contents.add(Utility.format(
                        ("&7" + Month.of(Integer.valueOf(backupDate[0].replace("0", ""))) + " " +
                                backupDate[1].replace("0", "") + "&8, &7" + backupDate[2] + " " + "&c@" + "&6 " +
                                backupTime[0].replace("0", "") + "&8:" + "&6" + backupTime[1]
                ).replace(".zip", "")));

            }
            PaginationList.builder()
                    .title(Utility.format("&cDirtCraft &7Backups"))
                    .padding(Utility.format("&4&m-"))
                    .contents(contents)
                    .build()
                    .sendTo(source);

            return CommandResult.success();
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new CommandException(Utility.format("&cThere was an error retrieving backups"));
        }
    }

}