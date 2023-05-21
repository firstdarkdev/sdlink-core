/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.linking;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import java.util.List;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

/**
 * @author HypherionSA
 * Command to unlink a discord and minecraft account, that was previously linked
 */
public class UnlinkAccountSlashCommand extends SDLinkSlashCommand {

    public UnlinkAccountSlashCommand() {
        super(false);
        this.name = "unlinkaccount";
        this.help = "Unlink your previously linked Discord and Minecraft accounts";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        List<SDLinkAccount> accounts = sdlinkDatabase.findAll(SDLinkAccount.class);

        if (accounts.isEmpty()) {
            event.reply("Sorry, but this server does not contain any stored players in its database").setEphemeral(true).queue();
            return;
        }

        for (SDLinkAccount account : accounts) {
            if (account.getDiscordID() != null && account.getDiscordID().equalsIgnoreCase(event.getMember().getId())) {
                MinecraftAccount minecraftAccount = MinecraftAccount.standard(account.getUsername());
                Result result = minecraftAccount.unlinkAccount();
                event.reply(result.getMessage()).setEphemeral(true).queue();
                break;
            }
        }
    }

}
