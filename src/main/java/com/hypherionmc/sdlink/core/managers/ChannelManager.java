/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.managers;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.messaging.MessageDestination;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

/**
 * @author HypherionSA
 * Load and Cache configured channels for later use
 */
public class ChannelManager {

    private static final HashMap<MessageDestination, Pair<StandardGuildMessageChannel, Boolean>> channelMap = new HashMap<>();
    private static StandardGuildMessageChannel consoleChannel;

    /**
     * Load configured channel, while always defaulting back to ChatChannel for channels that aren't configured
     */
    public static void loadChannels() {
        channelMap.clear();

        JDA jda = BotController.INSTANCE.getJDA();

        StandardGuildMessageChannel chatChannel = jda.getChannelById(StandardGuildMessageChannel.class, SDLinkConfig.INSTANCE.channelsAndWebhooks.channels.chatChannelID);
        StandardGuildMessageChannel eventChannel = jda.getChannelById(StandardGuildMessageChannel.class, SDLinkConfig.INSTANCE.channelsAndWebhooks.channels.eventsChannelID);
        consoleChannel = jda.getChannelById(StandardGuildMessageChannel.class, SDLinkConfig.INSTANCE.channelsAndWebhooks.channels.consoleChannelID);

        if (chatChannel != null) {
            channelMap.put(MessageDestination.CHAT, Pair.of(chatChannel, false));
        }

        channelMap.put(MessageDestination.EVENT, eventChannel != null ? Pair.of(eventChannel, false) : Pair.of(chatChannel, false));
        channelMap.put(MessageDestination.CONSOLE, consoleChannel != null ? Pair.of(consoleChannel, true) : Pair.of(chatChannel, false));
    }

    public static StandardGuildMessageChannel getConsoleChannel() {
        return consoleChannel;
    }

    public static StandardGuildMessageChannel getDestinationChannel(MessageDestination destination) {
        return channelMap.get(destination).getLeft();
    }
}
