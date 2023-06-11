/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.whitelist;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import com.hypherionmc.sdlink.core.util.SystemUtils;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import io.jsondb.InvalidJsonDbApiUsageException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

/**
 * @author HypherionSA
 * Command to start the Whitelisting process
 * This will generate the verification code the player needs to enter, to
 * verify the account belongs to them
 */
public class WhitelistAccountCommand extends SDLinkSlashCommand {

    public WhitelistAccountCommand() {
        super(SDLinkConfig.INSTANCE.whitelistingAndLinking.whitelisting.staffOnlyWhitelist);

        this.name = "whitelist";
        this.help = "Start the process of Whitelisting your Minecraft Account";

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
            account.setWhitelistCode(confirmCode);

            try {
                sdlinkDatabase.insert(account);
                event.reply("Please join the Minecraft server and check the Kick Message for your account whitelist code. Then, run the command /whitelistconfirm codehere to finish whitelisting your account").setEphemeral(true).queue();
            } catch (InvalidJsonDbApiUsageException e) {
                e.printStackTrace();
                event.reply("Could not start account whitelisting process. Please notify the server owner").setEphemeral(true).queue();
            }
        } else {
            if (account.isWhitelisted() || !SDLinkPlatform.minecraftHelper.isPlayerWhitelisted(minecraftAccount).isError()) {
                event.reply("Sorry, this Minecraft account is already whitelisted").setEphemeral(true).queue();
                return;
            }

            account.setWhitelistCode(confirmCode);

            try {
                sdlinkDatabase.upsert(account);
                event.reply("Please join the Minecraft server and check the Kick Message for your account whitelist code. Then, run the command /whitelistconfirm codehere to finish whitelisting your account").setEphemeral(true).queue();
            } catch (InvalidJsonDbApiUsageException e) {
                e.printStackTrace();
                event.reply("Could not start account whitelisting process. Please notify the server owner").setEphemeral(true).queue();
            }
        }
    }

}
