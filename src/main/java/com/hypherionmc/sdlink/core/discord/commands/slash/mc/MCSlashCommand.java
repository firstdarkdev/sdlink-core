/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.mc;

/*import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.config.impl.LinkedCommandsConfig;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.managers.RoleManager;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

public class MCSlashCommand extends SDLinkSlashCommand {

    public MCSlashCommand() {
        super(false);
        this.name = "mc";
        this.help = "Execute Minecraft Command from Discord";

        this.options = new ArrayList<>() {{
            add(new OptionData(OptionType.STRING, "slug", "The discordCommand slug defined in the config").setRequired(true));
            add(new OptionData(OptionType.STRING, "args0", "Additional arguments to pass to the %args% variable").setRequired(false));
            add(new OptionData(OptionType.STRING, "args1", "Additional arguments to pass to the %args% variable").setRequired(false));
            add(new OptionData(OptionType.STRING, "args2", "Additional arguments to pass to the %args% variable").setRequired(false));
            add(new OptionData(OptionType.STRING, "args3", "Additional arguments to pass to the %args% variable").setRequired(false));
            add(new OptionData(OptionType.STRING, "args4", "Additional arguments to pass to the %args% variable").setRequired(false));
            add(new OptionData(OptionType.STRING, "args5", "Additional arguments to pass to the %args% variable").setRequired(false));
        }};
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (SDLinkConfig.INSTANCE.linkedCommands.enabled) {
            String slug = event.getOption("slug") != null ? event.getOption("slug").getAsString() : "";
            String args0 = event.getOption("args0") != null ? event.getOption("args0").getAsString() : "";
            String args1 = event.getOption("args1") != null ? event.getOption("args1").getAsString() : "";
            String args2 = event.getOption("args2") != null ? event.getOption("args2").getAsString() : "";
            String args3 = event.getOption("args3") != null ? event.getOption("args3").getAsString() : "";
            String args4 = event.getOption("args4") != null ? event.getOption("args4").getAsString() : "";
            String args5 = event.getOption("args5") != null ? event.getOption("args5").getAsString() : "";

            Optional<LinkedCommandsConfig.Command> linkedCommand = SDLinkConfig.INSTANCE.linkedCommands.commands.stream().filter(c -> c.discordCommand.equalsIgnoreCase(slug)).findFirst();

            StringBuilder args = new StringBuilder();
            if (!args0.isEmpty())
                args.append(args0);
            if (!args1.isEmpty())
                args.append(" ").append(args1);
            if (!args2.isEmpty())
                args.append(" ").append(args2);
            if (!args3.isEmpty())
                args.append(" ").append(args3);
            if (!args4.isEmpty())
                args.append(" ").append(args4);
            if (!args5.isEmpty())
                args.append(" ").append(args5);

            sdlinkDatabase.reloadCollection("accounts");
            List<SDLinkAccount> accounts = sdlinkDatabase.findAll(SDLinkAccount.class);
            Optional<SDLinkAccount> account = accounts.stream().filter(u -> u.getDiscordID().equals(event.getMember().getId())).findFirst();

            linkedCommand.ifPresent(command -> {
                if (!command.discordRole.isEmpty()) {
                    Role role = RoleManager.getCommandRoles().isEmpty() ? null : RoleManager.getCommandRoles().get(command.discordCommand);

                    boolean userRole = role != null && event.getMember().getRoles().stream().anyMatch(r -> r.getIdLong() == role.getIdLong());
                    if (userRole) {
                        executeCommand(event, command, args.toString(), event.getMember(), account.orElse(null));
                    } else {
                        event.reply("You need the " + role.getName() + " role to perform this action").setEphemeral(true).queue();
                    }
                } else {
                    executeCommand(event, command, args.toString(), event.getMember(), account.orElse(null));
                }
            });

            if (linkedCommand.isEmpty()) {
                event.reply("Cannot find linked command " + slug).setEphemeral(true).queue();
            }

        } else {
            event.reply("Linked commands are not enabled!").setEphemeral(true).queue();
        }
    }

    private void executeCommand(SlashCommandEvent event, LinkedCommandsConfig.Command mcCommand, String args, Member member, SDLinkAccount account) {
        Result result = SDLinkPlatform.minecraftHelper.executeMinecraftCommand(mcCommand.mcCommand, args, member, account);
        event.reply(result.getMessage()).setEphemeral(true).queue();
    }
}*/
