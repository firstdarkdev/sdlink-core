/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.whitelist;

import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.util.MessageUtil;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

/**
 * @author HypherionSA
 * Staff command to view whitelisted players on their server
 * This list only contains players whitelisted using the bot
 */
public class ViewWhitelistedAccountsSlashCommand extends SDLinkSlashCommand {

    public ViewWhitelistedAccountsSlashCommand() {
        super(true);

        this.name = "whitelisted";
        this.help = "View a list of Whitelisted MC accounts";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        EmbedPaginator.Builder paginator = MessageUtil.defaultPaginator(event);

        List<SDLinkAccount> accounts = sdlinkDatabase.findAll(SDLinkAccount.class).stream().filter(SDLinkAccount::isWhitelisted).toList();

        EmbedBuilder builder = new EmbedBuilder();
        ArrayList<MessageEmbed> pages = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();

        if (accounts.isEmpty()) {
            event.reply("There are no whitelisted accounts for this discord").setEphemeral(true).queue();
            return;
        }

        MessageUtil.listBatches(accounts, 10).forEach(itm -> {
            count.getAndIncrement();
            builder.clear();
            builder.setTitle("Whitelisted Accounts - Page " + count + "/" + (int)Math.ceil(((float)accounts.size() / 10)));
            builder.setColor(Color.GREEN);
            StringBuilder sBuilder = new StringBuilder();

            itm.forEach(v -> {
                Member member = null;

                if (v.getDiscordID() != null && !v.getDiscordID().isEmpty()) {
                    member = event.getGuild().getMemberById(v.getDiscordID());
                }

                sBuilder.append(v.getUsername()).append(" -> ").append(member == null ? "Unlinked" : member.getAsMention()).append("\r\n");
            });
            builder.setDescription(sBuilder);
            pages.add(builder.build());
        });

        paginator.setItems(pages);
        EmbedPaginator embedPaginator = paginator.build();

        event.replyEmbeds(pages.get(0)).setEphemeral(false).queue(success -> success.retrieveOriginal().queue(msg -> embedPaginator.paginate(msg, 1)));
    }

}
