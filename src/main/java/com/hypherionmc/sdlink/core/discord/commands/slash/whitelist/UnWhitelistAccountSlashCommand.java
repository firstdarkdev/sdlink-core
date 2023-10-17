/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.whitelist;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import java.util.List;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

/**
 * @author HypherionSA
 * Remove a player from the whitelist, that was previously whitelisted through the bot
 */
public class UnWhitelistAccountSlashCommand extends SDLinkSlashCommand {

    public UnWhitelistAccountSlashCommand() {
        super(false);
        this.name = "unwhitelist";
        this.help = "Remove your previously whitelisted Minecraft account";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (SDLinkConfig.INSTANCE.whitelistingAndLinking.whitelisting.autoWhitelist) {
            event.reply("Sorry, but this server uses auto-whitelisting based on roles. This command cannot be used").setEphemeral(true).queue();
            return;
        }

        sdlinkDatabase.reloadCollection("accounts");
        List<SDLinkAccount> accounts = sdlinkDatabase.findAll(SDLinkAccount.class);

        if (accounts.isEmpty()) {
            event.reply("Sorry, but this server does not contain any stored players in its database").setEphemeral(true).queue();
            return;
        }

        for (SDLinkAccount account : accounts) {
            if (account.getAddedBy().equalsIgnoreCase(event.getMember().getId())) {
                MinecraftAccount minecraftAccount = MinecraftAccount.standard(account.getUsername());
                if (SDLinkPlatform.minecraftHelper.isPlayerWhitelisted(minecraftAccount).isError()) {
                    event.reply("Your account is not whitelisted in Minecraft. Cannot remove your account").setEphemeral(true).queue();
                } else {
                    Result result = minecraftAccount.unwhitelistAccount(event.getMember(), event.getGuild());
                    event.reply(result.getMessage()).setEphemeral(true).queue();
                }
                break;
            }
        }
    }

}
