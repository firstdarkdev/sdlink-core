/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash.setup;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.hypherionmc.sdlink.core.util.EncryptionUtil;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class SetChannelCommand extends SDLinkSlashCommand {

    public SetChannelCommand() {
        super(true);
        this.name = "setchannel";
        this.help = "Configure a channel for the mod to use";
        this.guildOnly = true;

        List<Command.Choice> choices = new ArrayList<>();
        choices.add(new Command.Choice("Chat", "chat"));
        choices.add(new Command.Choice("Events", "events"));
        choices.add(new Command.Choice("Console", "console"));

        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.CHANNEL, "channel", "The channel to set").setChannelTypes(ChannelType.TEXT).setRequired(true));
        optionData.add(new OptionData(OptionType.STRING, "type", "The type of channel to assign this channel to").addChoices(choices).setRequired(true));
        optionData.add(new OptionData(OptionType.BOOLEAN, "webhook", "Create a webhook instead of using the channel").setRequired(true));

        this.options = optionData;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(true).queue();

        StandardGuildMessageChannel channel = event.getOption("channel").getAsChannel().asStandardGuildMessageChannel();
        String type = event.getOption("type").getAsString();
        boolean webhook = event.getOption("webhook").getAsBoolean();

        if (!channel.canTalk()) {
            event.getHook().sendMessage("I do not have permission to send messages in " + channel.getAsMention()).setEphemeral(true).queue();
            return;
        }

        Result result;

        if (webhook) {
            result = setWebhook(channel, type);
        } else {
            result = setChannel(channel, type);
        }

        event.getHook().sendMessage(result.getMessage()).setEphemeral(true).queue();
    }

    private Result setChannel(StandardGuildMessageChannel channel, String type) {
        try {
            switch (type.toLowerCase()) {
                case "chat": {
                    SDLinkConfig.INSTANCE.channelsAndWebhooks.channels.chatChannelID = channel.getId();
                    SDLinkConfig.INSTANCE.saveConfig(SDLinkConfig.INSTANCE);
                }
                case "event": {
                    SDLinkConfig.INSTANCE.channelsAndWebhooks.channels.eventsChannelID = channel.getId();
                    SDLinkConfig.INSTANCE.saveConfig(SDLinkConfig.INSTANCE);
                }
                case "console": {
                    SDLinkConfig.INSTANCE.channelsAndWebhooks.channels.consoleChannelID = channel.getId();
                    SDLinkConfig.INSTANCE.saveConfig(SDLinkConfig.INSTANCE);
                }
            }

            return Result.success("Saved Channel to Config. Restart your server for the channel to become active");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Failed to save config: " + e.getMessage());
        }
    }

    private Result setWebhook(StandardGuildMessageChannel channel, String type) {
        try {
            switch (type.toLowerCase()) {
                case "chat": {
                    channel.createWebhook("SDLink " + type).queue(s -> {
                        SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.chatWebhook = EncryptionUtil.INSTANCE.encrypt(s.getUrl());
                        SDLinkConfig.INSTANCE.saveConfig(SDLinkConfig.INSTANCE);
                    });
                }
                case "event": {
                    channel.createWebhook("SDLink " + type).queue(s -> {
                        SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.eventsWebhook = EncryptionUtil.INSTANCE.encrypt(s.getUrl());
                        SDLinkConfig.INSTANCE.saveConfig(SDLinkConfig.INSTANCE);
                    });
                }
                case "console": {
                    channel.createWebhook("SDLink " + type).queue(s -> {
                        SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.consoleWebhook = EncryptionUtil.INSTANCE.encrypt(s.getUrl());
                        SDLinkConfig.INSTANCE.saveConfig(SDLinkConfig.INSTANCE);
                    });
                }
            }

            return Result.success("Saved Webhook to Config. Restart your server for the webhook to become active");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Failed to save config: " + e.getMessage());
        }
    }

}
