/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.verification;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

public class StaffUnverifyCommand extends SDLinkSlashCommand {

    public StaffUnverifyCommand() {
        super(true);
        this.name = "staffunverify";
        this.help = "Unverify another player's Minecraft account";

        List<OptionData> options = new ArrayList<>() {{
            add(new OptionData(OptionType.USER, "discorduser", "The discord user the minecraft account belongs to").setRequired(true));
            add(new OptionData(OptionType.STRING, "mcname", "The minecraft account of the linked user").setRequired(true));
        }};

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        sdlinkDatabase.reloadCollection("verifiedaccounts");
        List<SDLinkAccount> accounts = sdlinkDatabase.findAll(SDLinkAccount.class);

        if (accounts.isEmpty()) {
            event.reply("Sorry, but this server does not contain any stored players in its database").setEphemeral(true).queue();
            return;
        }

        String mcname = event.getOption("mcname").getAsString();
        User user = event.getOption("discorduser").getAsUser();

        Member member = event.getGuild().getMemberById(user.getId());

        if (member == null) {
            event.reply(user.getEffectiveName() + " is not a member of this discord server").setEphemeral(true).queue();
            return;
        }

        MinecraftAccount minecraftAccount = MinecraftAccount.standard(mcname);
        Result result = minecraftAccount.unverifyAccount(member, event.getGuild());
        event.reply(result.getMessage()).setEphemeral(true).queue();
    }

}