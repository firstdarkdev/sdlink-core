/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.linking;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.util.SystemUtils;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import io.jsondb.InvalidJsonDbApiUsageException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

/**
 * @author HypherionSA
 * Command to start the Linking process of a Discord and MC Account
 * This will generate the verification code the player needs to enter, to
 * verify the account belongs to them
 */
public class LinkAccountCommand extends SDLinkSlashCommand {

    public LinkAccountCommand() {
        super(false);
        this.name = "linkaccount";
        this.help = "Start the process of linking your Discord and MC Accounts";

        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "mcname", "Your Minecraft Username").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String mcName = event.getOption("mcname") != null ? event.getOption("mcname").getAsString() : "";

        if (mcName.isEmpty()) {
            event.reply("You need to supply your Minecraft username").setEphemeral(true).queue();
            return;
        }

        MinecraftAccount minecraftAccount = MinecraftAccount.standard(mcName);
        String confirmCode = String.valueOf(SystemUtils.generateRandomJoinCode());
        SDLinkAccount account = minecraftAccount.getStoredAccount();

        if (account == null) {
            account = minecraftAccount.newDBEntry();
            account.setAccountLinkCode(confirmCode);

            try {
                sdlinkDatabase.insert(account);
                event.reply("Please join the Minecraft server and check the Kick Message for your account link code. Then, run the command /confirmlink codehere to finish linking your accounts").setEphemeral(true).queue();
            } catch (InvalidJsonDbApiUsageException e) {
                e.printStackTrace();
                event.reply("Could not start account linking process. Please notify the server owner").setEphemeral(true).queue();
            }
        } else {
            if (account.getDiscordID() != null || !account.getDiscordID().isEmpty()) {
                event.reply("Sorry, this Minecraft account is already linked to a discord account").setEphemeral(true).queue();
                return;
            }

            account.setAccountLinkCode(confirmCode);

            try {
                sdlinkDatabase.upsert(account);
                event.reply("Please join the Minecraft server and check the Kick Message for your account link code. Then, run the command /confirmlink codehere to finish linking your accounts").setEphemeral(true).queue();
            } catch (InvalidJsonDbApiUsageException e) {
                e.printStackTrace();
                event.reply("Could not start account linking process. Please notify the server owner").setEphemeral(true).queue();
            }
        }
    }

}
