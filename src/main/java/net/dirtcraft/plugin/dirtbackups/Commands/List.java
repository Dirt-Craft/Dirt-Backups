package net.dirtcraft.plugin.dirtbackups.Commands;

import net.dirtcraft.plugin.dirtbackups.DirtBackups;
import net.dirtcraft.plugin.dirtbackups.Utility;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class List implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
            Task.builder()
                    .async()
                    .execute(() -> {
                        try {
                            ArrayList<Text> contents = new ArrayList<>();
                            for (File file : Utility.listBackups()) {
                                DateTimeFormatter format = DateTimeFormatter.ofPattern(Utility.getFormat());
                                LocalDateTime time;
                                try {
                                    time = LocalDateTime.from(format.parse(file.getName().replace(".zip", "")));
                                } catch (DateTimeParseException exception) {
                                    Task.builder()
                                            .async()
                                            .execute(file::delete)
                                            .submit(DirtBackups.getInstance());
                                    continue;
                                }
                                contents.add(Text.builder()
                                        .append(
                                                Utility.format(
                                                        ("&7" + WordUtils.capitalizeFully(time.getMonth().name()) + " " +
                                                                time.getDayOfMonth() + "&8, &7" + time.getYear() + " " + "&c@" + "&6 " +
                                                                addZero(time.getHour()) + "&8:" + "&6" + addZero(time.getMinute()) +
                                                                "&8" + " " + Calendar.getInstance().getTimeZone().getDisplayName(false, TimeZone.SHORT))
                                                ))
                                        .onHover(TextActions.showText(Utility.format("&7Backup Size&8: &6" + Utility.readableFileSize(file.length()))))
                                        .build());
                            }

                            Text footer = Text.builder()
                                    .append(Utility.format("&8[&aStart Backup&8]"))
                                    .onHover(TextActions.showText(Utility.format("&5&nClick Me&7 to start a backup")))
                                    .onClick(TextActions.runCommand("/backup start"))
                                    .build();

                            PaginationList.builder()
                                    .title(Utility.format("&cDirtCraft &7Backups"))
                                    .padding(Utility.format("&4&m-"))
                                    .contents(contents)
                                    .footer(footer)
                                    .build()
                                    .sendTo(source);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            source.sendMessage(Utility.format("&cThere was an error retrieving backups!"));
                        }
                    })
                    .submit(DirtBackups.getInstance());

            return CommandResult.success();
    }

    private String addZero(int time) {
        if (10 > time) return "0" + time;
        else return String.valueOf(time);
    }

}
