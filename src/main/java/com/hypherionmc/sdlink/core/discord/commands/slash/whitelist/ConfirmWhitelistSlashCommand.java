/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.whitelist;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

/**
 * @author HypherionSA
 * Command to confirm a Whitelist request
 */
public class ConfirmWhitelistSlashCommand extends SDLinkSlashCommand {

    public ConfirmWhitelistSlashCommand() {
        super(false);
        this.name = "whitelistconfirm";
        this.help = "Confirm your Minecraft Account to complete whitelisting";

        this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "code", "The verification code from the Minecraft Kick Message").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int mcCode = event.getOption("code") != null ? event.getOption("code").getAsInt() : 0;

        if (mcCode == 0) {
            event.reply("You need to provide a verification code").setEphemeral(true).queue();
            return;
        }

        List<SDLinkAccount> accounts = sdlinkDatabase.findAll(SDLinkAccount.class);

        if (accounts.isEmpty()) {
            event.reply("Sorry, but this server does not contain any stored players in its database").setEphemeral(true).queue();
            return;
        }

        for (SDLinkAccount account : accounts) {
            if (account.getWhitelistCode().equalsIgnoreCase(String.valueOf(mcCode))) {
                MinecraftAccount minecraftAccount = MinecraftAccount.standard(account.getUsername());
                Result result = minecraftAccount.whitelistAccount(event.getMember(), event.getGuild());
                event.reply(result.getMessage()).setEphemeral(true).queue();
                return;
            }
        }

        event.reply("Sorry, we could not verify your Minecraft account. Please try again").setEphemeral(true).queue();
    }

}
